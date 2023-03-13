package dev.turtywurty.industria.blockentity;

import dev.turtywurty.industria.Industria;
import dev.turtywurty.industria.block.CrusherBlock;
import dev.turtywurty.industria.init.BlockEntityInit;
import dev.turtywurty.industria.recipes.CrusherRecipe;
import dev.turtywurty.turtylib.common.blockentity.ModularBlockEntity;
import dev.turtywurty.turtylib.common.blockentity.module.EnergyModule;
import dev.turtywurty.turtylib.common.blockentity.module.SidedInventoryModule;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Containers;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.wrapper.RecipeWrapper;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.Consumer;

import static dev.turtywurty.turtylib.common.blockentity.module.SidedInventoryModule.SidedInventoryHandler.SidedInventoryHandlerBuilder;

public class CrusherBlockEntity extends ModularBlockEntity {
    public static final Component TITLE = Component.translatable("container." + Industria.MODID + ".crusher");

    private final SidedInventoryModule inventory;
    private final EnergyModule energy;

    private CrusherRecipe currentRecipe;
    private int currentRecipeTime;
    private int currentRecipeEnergy;

    private final ContainerData data = new ContainerData() {
        @Override
        public int get(int index) {
            return switch (index) {
                case 0 -> CrusherBlockEntity.this.currentRecipeTime;
                case 1 -> CrusherBlockEntity.this.currentRecipeEnergy;
                case 2 ->
                        CrusherBlockEntity.this.currentRecipe == null ? 0 : CrusherBlockEntity.this.currentRecipe.getProcessTime();
                case 3 ->
                        CrusherBlockEntity.this.currentRecipe == null ? 0 : CrusherBlockEntity.this.currentRecipe.getEnergyCost();
                case 4 -> CrusherBlockEntity.this.energy.getCapabilityInstance().getEnergyStored();
                case 5 -> CrusherBlockEntity.this.energy.getCapabilityInstance().getMaxEnergyStored();
                default -> 0;
            };
        }

        @Override
        public void set(int index, int value) {
            switch (index) {
                case 0 -> CrusherBlockEntity.this.currentRecipeTime = value;
                case 1 -> CrusherBlockEntity.this.currentRecipeEnergy = value;
                default -> {
                }
            }
        }

        @Override
        public int getCount() {
            return 6;
        }
    };

    public CrusherBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(BlockEntityInit.CRUSHER.get(), pPos, pBlockState);
        this.inventory = addModule(new SidedInventoryModule(
                new SidedInventoryHandlerBuilder(this).setSide(Direction.UP, 1).setSide(Direction.DOWN, 1)));
        this.energy = addModule(new EnergyModule(this));
    }

    public SidedInventoryModule getInventory() {
        return inventory;
    }

    public EnergyModule getEnergy() {
        return energy;
    }

    public CrusherRecipe getCurrentRecipe() {
        return currentRecipe;
    }

    public int getCurrentRecipeEnergy() {
        return currentRecipeEnergy;
    }

    public int getCurrentRecipeTime() {
        return currentRecipeTime;
    }

    public ContainerData getContainerData() {
        return this.data;
    }

    @Override
    public void tick() {
        super.tick();
        if (this.level == null || this.level.isClientSide) return;

        this.energy.getCapabilityInstance().receiveEnergy(1000, false);

        if (this.currentRecipe == null) {
            this.currentRecipe = getRecipeFor(this.level, this.inventory.getNullableInventory(Direction.UP),
                    this.inventory.getNullableInventory(Direction.DOWN));

            int currentRecipeTime = this.currentRecipeTime;
            int currentRecipeEnergy = this.currentRecipeEnergy;

            this.currentRecipeTime = 0;
            this.currentRecipeEnergy = 0;
            if (currentRecipeTime != this.currentRecipeTime || currentRecipeEnergy != this.currentRecipeEnergy)
                update();
        } else {
            if (this.currentRecipe.matches(this.inventory.getNullableInventory(Direction.UP),
                    this.inventory.getNullableInventory(Direction.DOWN), this.level)) {
                if (this.currentRecipeEnergy < this.currentRecipe.getEnergyCost()) {
                    this.currentRecipeEnergy += this.energy.getCapabilityInstance()
                            .extractEnergy(this.currentRecipe.getEnergyCost() - this.currentRecipeEnergy, false);
                } else {
                    this.currentRecipeTime++;
                    if (this.currentRecipeTime >= this.currentRecipe.getProcessTime()) {
                        this.currentRecipeTime = 0;
                        this.currentRecipeEnergy = 0;

                        ItemStack output = this.currentRecipe.assemble(
                                new RecipeWrapper(this.inventory.getNullableInventory(Direction.UP)));
                        if (this.inventory.getCapabilityInstance(Direction.DOWN).insertItem(0, output, true)
                                .isEmpty()) {
                            this.inventory.getCapabilityInstance(Direction.DOWN).insertItem(0, output, false);
                        } else {
                            Containers.dropItemStack(this.level, this.worldPosition.getX() + 0.5D,
                                    this.worldPosition.getY() + 1.0D, this.worldPosition.getZ() + 0.5D, output);
                        }

                        this.currentRecipe = null;
                    }
                }

                update();
            } else {
                int currentRecipeTime = this.currentRecipeTime;
                int currentRecipeEnergy = this.currentRecipeEnergy;

                this.currentRecipe = null;
                this.currentRecipeTime = 0;
                this.currentRecipeEnergy = 0;
                if (currentRecipeTime != this.currentRecipeTime || currentRecipeEnergy != this.currentRecipeEnergy)
                    update();
            }
        }

        List<ItemEntity> entities = this.level.getEntitiesOfClass(ItemEntity.class,
                CrusherBlock.PICKUP_AREA.move(this.worldPosition), entity -> {
                    ItemStack stack = entity.getItem().copy();
                    stack.setCount(1);
                    return this.inventory.getCapabilityInstance(Direction.UP).insertItem(0, stack, true).isEmpty();
                });
        if (entities.isEmpty()) return;

        for (ItemEntity entity : entities) {
            ItemStack stack = entity.getItem().copy();
            stack = this.inventory.getCapabilityInstance(Direction.UP).insertItem(0, stack, false);
            if (stack.isEmpty()) {
                entity.remove(Entity.RemovalReason.DISCARDED);
            } else {
                entity.setItem(stack);
            }
        }
    }

    @Override
    public void load(@NotNull CompoundTag nbt) {
        super.load(nbt);
        this.currentRecipeTime = nbt.getInt("CurrentRecipeTime");
        this.currentRecipeEnergy = nbt.getInt("CurrentRecipeEnergy");
        if (this.level != null && !this.level.isClientSide) {
            this.currentRecipe = getRecipeFor(this.level, this.inventory.getNullableInventory(Direction.UP),
                    this.inventory.getNullableInventory(Direction.DOWN));
        }
    }

    @Override
    protected void saveAdditional(CompoundTag nbt) {
        super.saveAdditional(nbt);
        nbt.putInt("CurrentRecipeTime", this.currentRecipeTime);
        nbt.putInt("CurrentRecipeEnergy", this.currentRecipeEnergy);
    }

    @Override
    protected List<Consumer<CompoundTag>> getReadSyncData() {
        List<Consumer<CompoundTag>> readSyncData = super.getReadSyncData();
        readSyncData.add(nbt -> {
            this.currentRecipeTime = nbt.getInt("CurrentRecipeTime");
            this.currentRecipeEnergy = nbt.getInt("CurrentRecipeEnergy");
        });

        return readSyncData;
    }

    @Override
    protected List<Consumer<CompoundTag>> getWriteSyncData() {
        List<Consumer<CompoundTag>> writeSyncData = super.getWriteSyncData();
        writeSyncData.add(nbt -> {
            nbt.putInt("CurrentRecipeTime", this.currentRecipeTime);
            nbt.putInt("CurrentRecipeEnergy", this.currentRecipeEnergy);
        });

        return writeSyncData;
    }

    private static CrusherRecipe getRecipeFor(Level level, ItemStackHandler input, ItemStackHandler output) {
        return level.getRecipeManager().getRecipes().stream()
                .filter(recipe -> recipe.getType() == CrusherRecipe.Type.INSTANCE).map(CrusherRecipe.class::cast)
                .filter(recipe -> recipe.matches(input, output, level)).findFirst().orElse(null);
    }
}
