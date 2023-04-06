package dev.turtywurty.industria.blockentity;

import dev.turtywurty.industria.Industria;
import dev.turtywurty.industria.blockentity.util.directional.MultiDirectionalFluidModule;
import dev.turtywurty.industria.blockentity.util.directional.MultiDirectionalFluidTank;
import dev.turtywurty.industria.client.screens.AgitatorScreen.SwitchingWidget.IOType;
import dev.turtywurty.industria.init.BlockEntityInit;
import dev.turtywurty.industria.init.RecipeInit;
import dev.turtywurty.industria.menu.AgitatorMenu;
import dev.turtywurty.industria.network.PacketManager;
import dev.turtywurty.industria.network.clientbound.CAgitatorFluidUpdatePacket;
import dev.turtywurty.industria.network.serverbound.SSwitchAgitatorIOTypePacket;
import dev.turtywurty.industria.recipes.AgitatorRecipe;
import dev.turtywurty.turtylib.common.blockentity.ModularBlockEntity;
import dev.turtywurty.turtylib.common.blockentity.module.EnergyModule;
import dev.turtywurty.turtylib.common.blockentity.module.InventoryModule;
import dev.turtywurty.turtylib.core.util.Either3;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.wrapper.RecipeWrapper;
import org.jetbrains.annotations.NotNull;

import java.io.FileFilter;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class AgitatorBlockEntity extends ModularBlockEntity {
    public static final Component TITLE = Component.translatable("container." + Industria.MODID + ".agitator");

    private final InventoryModule itemInventory;
    private final MultiDirectionalFluidModule fluidInventory;
    private final EnergyModule energy;
    private final IOType[] types = new IOType[]{IOType.FLUID, IOType.FLUID, IOType.FLUID, IOType.FLUID, IOType.FLUID, IOType.FLUID};
    private final List<AgitatorMenu> openMenus = new ArrayList<>();


    private int progress = 0;
    private int maxProgress = 100;

    private final ContainerData containerData = new ContainerData() {
        @Override
        public int get(int pIndex) {
            return switch (pIndex) {
                case 0 -> AgitatorBlockEntity.this.progress;
                case 1 -> AgitatorBlockEntity.this.maxProgress;
                case 2 -> AgitatorBlockEntity.this.energy.getCapabilityInstance().getEnergyStored();
                case 3 -> AgitatorBlockEntity.this.energy.getCapabilityInstance().getMaxEnergyStored();
                default -> 0;
            };
        }

        @Override
        public void set(int pIndex, int pValue) {
            switch (pIndex) {
                case 0 -> AgitatorBlockEntity.this.progress = pValue;
                case 1 -> AgitatorBlockEntity.this.maxProgress = pValue;
                case 2 -> AgitatorBlockEntity.this.energy.getCapabilityInstance().setEnergy(pValue);
            }
        }

        @Override
        public int getCount() {
            return 4;
        }
    };

    private float prevRotorYRotation, rotorYRotation;
    private Consumer<List<FluidStack>> berFluidUpdateCallback = null;

    public AgitatorBlockEntity(BlockPos pos, BlockState state) {
        super(BlockEntityInit.AGITATOR.get(), pos, state);

        this.itemInventory = addModule(new InventoryModule(this, 6));
        this.fluidInventory = addModule(new MultiDirectionalFluidModule(
                new MultiDirectionalFluidModule.Builder(this).updateCallback(this::updateFluids)
                        .addTank(10000, Direction.NORTH).addTank(10000, Direction.EAST).addTank(10000, Direction.SOUTH)
                        .addTank(10000, Direction.WEST).addTank(10000, Direction.UP).addTank(10000, Direction.DOWN)));

        this.energy = addModule(new EnergyModule(this, new EnergyModule.Builder().capacity(10000).maxReceive(1000)));
    }

    public InventoryModule getInventory() {
        return this.itemInventory;
    }

    public MultiDirectionalFluidModule getFluidInventory() {
        return this.fluidInventory;
    }

    public EnergyModule getEnergy() {
        return this.energy;
    }

    public ContainerData getContainerData() {
        return this.containerData;
    }

    @Override
    protected void saveAdditional(CompoundTag nbt) {
        super.saveAdditional(nbt);
        nbt.putInt("progress", this.progress);
        nbt.putInt("maxProgress", this.maxProgress);
        nbt.putIntArray("types",
                new int[]{this.types[0].ordinal(), this.types[1].ordinal(), this.types[2].ordinal(), this.types[3].ordinal(), this.types[4].ordinal(), this.types[5].ordinal()});
    }

    @Override
    public void load(@NotNull CompoundTag nbt) {
        super.load(nbt);
        this.progress = nbt.getInt("progress");
        this.maxProgress = nbt.getInt("maxProgress");
        int[] types = nbt.getIntArray("types");
        for (int index = 0; index < 6; index++) {
            this.types[index] = IOType.values()[types[index]];
        }
    }

    @Override
    protected List<Consumer<CompoundTag>> getWriteSyncData() {
        return List.of(tag -> tag.putIntArray("types",
                        new int[]{this.types[0].ordinal(), this.types[1].ordinal(), this.types[2].ordinal(), this.types[3].ordinal(), this.types[4].ordinal(), this.types[5].ordinal()}),
                tag -> fluidInventory.serialize(this, tag));
    }

    @Override
    protected List<Consumer<CompoundTag>> getReadSyncData() {
        return List.of(tag -> {
            int[] types = tag.getIntArray("types");
            for (int index = 0; index < 6; index++) {
                this.types[index] = IOType.values()[types[index]];
            }
        }, tag -> fluidInventory.deserialize(this, tag));
    }

    public void setIOType(int index, IOType type) {
        if (this.level == null) return;

        if (this.level.isClientSide()) {
            PacketManager.sendToServer(new SSwitchAgitatorIOTypePacket(this.worldPosition, index, type));
            return;
        }

        this.types[index] = type;
        update();
    }

    public IOType getIOType(int index) {
        return this.types[index];
    }

    private AgitatorRecipe getRecipe() {
        if (this.level == null) {
            return null;
        }

        return this.level.getRecipeManager().getRecipeFor(RecipeInit.AGITATOR_TYPE.get(),
                new RecipeWrapper(this.getInventory().getCapabilityInstance()), this.level).orElse(null);
    }

    private boolean isValidRecipe(AgitatorRecipe recipe) {
        if (recipe == null) {
            return false;
        }

        for (int index = 0; index < 3; index++) {
            Direction direction = Direction.values()[index];
            Either3<AgitatorRecipe.CountedIngredient, FluidStack, FluidStack> input = recipe.getInput(index);
            IOType type = this.types[index];
            if (input == null) {
                continue;
            }

            if (input.left().isPresent()) {
                AgitatorRecipe.CountedIngredient ingredient = input.left().get();
                if (type != IOType.ITEM) {
                    return false;
                }

                if (!ingredient.ingredient().test(getInventory().getCapabilityInstance().getStackInSlot(index))) {
                    return false;
                }

                ItemStack stack = getInventory().getCapabilityInstance().getStackInSlot(index);
                if (stack.getCount() + ingredient.count() >= stack.getMaxStackSize()) {
                    return false;
                }

                continue;
            }

            if (input.middle().isPresent()) {
                FluidStack fluidStack = input.middle().get();
                if (type != IOType.FLUID) {
                    return false;
                }

                if (!getFluidInventory().getTank(direction).map(tank -> tank.isFluidValid(fluidStack)).orElse(false)) {
                    return false;
                }
            }
        }

        return true;
    }

    public boolean hasEnergy() {
        return this.energy.getCapabilityInstance().getEnergyStored() > 0;
    }

    @Override
    public void tick() {
        super.tick();

        if (this.level == null || this.level.isClientSide()) return;

        getEnergy().getCapabilityInstance().receiveEnergy(1000, false);
        if (!hasEnergy()) return;

        AgitatorRecipe recipe = getRecipe();
        if (!isValidRecipe(recipe)) {
            this.progress = 0;
            this.maxProgress = 0;
            return;
        }

        this.maxProgress = recipe.getProcessTime();
        this.progress++;

        if (this.progress >= this.maxProgress) {
            this.progress = 0;
            this.maxProgress = 0;
            for (int index = 0; index < 3; index++) {
                Direction direction = Direction.values()[index];

                Either3<AgitatorRecipe.CountedIngredient, FluidStack, FluidStack> input = recipe.getInput(index);
                IOType type = this.types[index];
                if (input == null) {
                    continue;
                }

                if (input.left().isPresent()) {
                    AgitatorRecipe.CountedIngredient ingredient = input.left().get();
                    if (type != IOType.ITEM) {
                        continue;
                    }

                    ItemStack stack = getInventory().getCapabilityInstance().getStackInSlot(index);
                    stack.shrink(ingredient.count());
                    getInventory().getCapabilityInstance().setStackInSlot(index, stack);
                    continue;
                }

                if (input.middle().isPresent()) {
                    FluidStack fluidStack = input.middle().get();
                    if (type != IOType.FLUID) {
                        continue;
                    }

                    getFluidInventory().getTank(direction)
                            .ifPresent(tank -> tank.drain(fluidStack, IFluidHandler.FluidAction.EXECUTE));
                }
            }

            for (int index = 3; index < 6; index++) {
                Direction direction = Direction.values()[index];

                Either3<ItemStack, FluidStack, FluidStack> output = recipe.getOutput(index - 3);
                IOType type = this.types[index];
                if (output == null) {
                    continue;
                }

                if (output.left().isPresent()) {
                    ItemStack outStack = output.left().get();
                    if (type != IOType.ITEM) {
                        continue;
                    }

                    getInventory().getCapabilityInstance().insertItem(index, outStack, false);
                    continue;
                }

                if (output.middle().isPresent()) {
                    FluidStack fluidStack = output.middle().get();
                    if (type != IOType.FLUID) {
                        continue;
                    }

                    getFluidInventory().getTank(direction)
                            .ifPresent(tank -> tank.fill(fluidStack, IFluidHandler.FluidAction.EXECUTE));
                }
            }
        }
    }

    public int getProgress() {
        return this.progress;
    }

    public int getMaxProgress() {
        return this.maxProgress;
    }

    public void addMenu(AgitatorMenu menu) {
        this.openMenus.add(menu);
    }

    public void removeMenu(AgitatorMenu menu) {
        this.openMenus.remove(menu);
    }

    public void updateFluids() {
        this.update();
        this.openMenus.forEach(AgitatorMenu::broadcastFluidChanges);
        PacketManager.sendToAllClients(new CAgitatorFluidUpdatePacket(this.worldPosition,
                this.fluidInventory.getFluidHandler().getFluidTanks().stream().map(MultiDirectionalFluidTank::getFluid)
                        .toList()));
    }

    public Consumer<List<FluidStack>> getBERFluidUpdateCallback() {
        return this.berFluidUpdateCallback;
    }

    public void setBERFluidUpdateCallback(Consumer<List<FluidStack>> berFluidUpdateCallback) {
        this.berFluidUpdateCallback = berFluidUpdateCallback;
    }

    public boolean hasBERFluidUpdateCallback() {
        return this.berFluidUpdateCallback != null;
    }
}
