package dev.turtywurty.industria.blockentity;

import dev.turtywurty.industria.Industria;
import dev.turtywurty.industria.client.screens.AgitatorScreen.SwitchingWidget.IOType;
import dev.turtywurty.industria.init.BlockEntityInit;
import dev.turtywurty.industria.init.RecipeInit;
import dev.turtywurty.industria.network.PacketManager;
import dev.turtywurty.industria.network.serverbound.SSwitchAgitatorIOTypePacket;
import dev.turtywurty.industria.recipes.AgitatorRecipe;
import io.github.darealturtywurty.turtylib.common.blockentity.ModularBlockEntity;
import io.github.darealturtywurty.turtylib.common.blockentity.module.EnergyModule;
import io.github.darealturtywurty.turtylib.common.blockentity.module.FluidModule;
import io.github.darealturtywurty.turtylib.common.blockentity.module.InventoryModule;
import io.github.darealturtywurty.turtylib.core.util.Either3;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.wrapper.RecipeWrapper;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.Consumer;

public class AgitatorBlockEntity extends ModularBlockEntity {
    public static final Component TITLE = Component.translatable("container." + Industria.MODID + ".agitator");

    private final InventoryModule itemInventory;
    private final FluidModule[] fluidInventories = new FluidModule[6];
    private final EnergyModule energy;

    private final IOType[] types = new IOType[]{IOType.FLUID, IOType.FLUID, IOType.FLUID, IOType.FLUID, IOType.FLUID, IOType.FLUID};

    private int progress = 0;
    private int maxProgress = 100;

    private final ContainerData containerData = new ContainerData() {
        @Override
        public int get(int pIndex) {
            return switch (pIndex) {
                case 0 -> AgitatorBlockEntity.this.progress;
                case 1 -> AgitatorBlockEntity.this.maxProgress;
                case 2 -> AgitatorBlockEntity.this.energy.getCapability().getEnergyStored();
                case 3 -> AgitatorBlockEntity.this.energy.getCapability().getMaxEnergyStored();
                default -> 0;
            };
        }

        @Override
        public void set(int pIndex, int pValue) {
            switch (pIndex) {
                case 0 -> AgitatorBlockEntity.this.progress = pValue;
                case 1 -> AgitatorBlockEntity.this.maxProgress = pValue;
                case 2 -> AgitatorBlockEntity.this.energy.getCapability().setEnergy(pValue);
            }
        }

        @Override
        public int getCount() {
            return 4;
        }
    };

    public AgitatorBlockEntity(BlockPos pos, BlockState state) {
        super(BlockEntityInit.AGITATOR.get(), pos, state);

        this.itemInventory = addModule(new InventoryModule(this, 6));

        for (int index = 0; index < 6; index++) {
            this.fluidInventories[index] = addModule(new FluidModule(this, 10000));
        }

        this.energy = addModule(new EnergyModule(this, new EnergyModule.Builder().capacity(10000).maxReceive(1000)));
    }

    public InventoryModule getInventory() {
        return this.itemInventory;
    }

    public FluidModule[] getFluidInventories() {
        return this.fluidInventories;
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
                new int[]{this.types[0].ordinal(), this.types[1].ordinal(), this.types[2].ordinal(), this.types[3].ordinal(), this.types[4].ordinal(), this.types[5].ordinal()}));
    }

    @Override
    protected List<Consumer<CompoundTag>> getReadSyncData() {
        return List.of(tag -> {
            int[] types = tag.getIntArray("types");
            for (int index = 0; index < 6; index++) {
                this.types[index] = IOType.values()[types[index]];
            }
        });
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

        return this.level.getRecipeManager()
                .getRecipeFor(RecipeInit.AGITATOR_TYPE.get(), new RecipeWrapper(this.getInventory().getCapability()),
                        this.level).orElse(null);
    }

    private boolean isValidRecipe(AgitatorRecipe recipe) {
        if (recipe == null) {
            return false;
        }

        for (int index = 0; index < 3; index++) {
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

                if (!ingredient.ingredient().test(getInventory().getCapability().getStackInSlot(index))) {
                    return false;
                }

                ItemStack stack = getInventory().getCapability().getStackInSlot(index);
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

                if (!fluidStack.isFluidEqual(getFluidInventories()[index].getCapability().getFluid())) {
                    return false;
                }
            }
        }

        return true;
    }

    public boolean hasEnergy() {
        return this.energy.getCapability().getEnergyStored() > 0;
    }

    @Override
    public void tick() {
        super.tick();

        if (this.level == null || this.level.isClientSide())
            return;

        getEnergy().getCapability().receiveEnergy(1000, false);
        getFluidInventories()[0].getCapability().fill(new FluidStack(Fluids.WATER, 1000),
                IFluidHandler.FluidAction.EXECUTE);
        if (!hasEnergy())
            return;

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

                    ItemStack stack = getInventory().getCapability().getStackInSlot(index);
                    stack.shrink(ingredient.count());
                    getInventory().getCapability().setStackInSlot(index, stack);
                    continue;
                }

                if (input.middle().isPresent()) {
                    FluidStack fluidStack = input.middle().get();
                    if (type != IOType.FLUID) {
                        continue;
                    }

                    getFluidInventories()[index].getCapability().drain(fluidStack, IFluidHandler.FluidAction.EXECUTE);
                }
            }

            for (int index = 3; index < 6; index++) {
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

                    getInventory().getCapability().insertItem(index, outStack, false);
                    continue;
                }

                if (output.middle().isPresent()) {
                    FluidStack fluidStack = output.middle().get();
                    if (type != IOType.FLUID) {
                        continue;
                    }

                    getFluidInventories()[index].getCapability().fill(fluidStack, IFluidHandler.FluidAction.EXECUTE);
                }
            }
        }
    }
}
