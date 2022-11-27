package dev.turtywurty.industria.blockentity;

import dev.turtywurty.industria.Industria;
import dev.turtywurty.industria.init.BlockEntityInit;
import dev.turtywurty.industria.recipes.CrusherRecipe;
import io.github.darealturtywurty.turtylib.common.blockentity.ModularBlockEntity;
import io.github.darealturtywurty.turtylib.common.blockentity.module.EnergyModule;
import io.github.darealturtywurty.turtylib.common.blockentity.module.InventoryModule;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Containers;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.items.wrapper.RecipeWrapper;

import java.util.Optional;

public class CrusherBlockEntity extends ModularBlockEntity {
    public static final Component TITLE = Component.translatable("container." + Industria.MODID + ".crusher");

    private final InventoryModule inventory;
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
                case 4 -> CrusherBlockEntity.this.energy.getCapability().getEnergyStored();
                case 5 -> CrusherBlockEntity.this.energy.getCapability().getMaxEnergyStored();
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
        this.inventory = addModule(new InventoryModule(this, 2));
        this.energy = addModule(new EnergyModule(this));
    }

    public InventoryModule getInventory() {
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
        if (this.level.isClientSide) return;

        this.energy.getCapability().receiveEnergy(1000, false);

        if (this.currentRecipe == null) {
            Optional<CrusherRecipe> recipe = this.level.getRecipeManager()
                    .getRecipeFor(CrusherRecipe.Type.INSTANCE, new RecipeWrapper(this.inventory.getCapability()),
                            this.level);
            this.currentRecipe = recipe.orElse(null);
            this.currentRecipeTime = 0;
            this.currentRecipeEnergy = 0;
        } else {
            if (this.currentRecipe.matches(this.inventory.getCapability(), this.level)) {
                if (this.currentRecipeEnergy < this.currentRecipe.getEnergyCost()) {
                    this.currentRecipeEnergy += this.energy.getCapability()
                            .extractEnergy(this.currentRecipe.getEnergyCost() - this.currentRecipeEnergy, false);
                } else {
                    this.currentRecipeTime++;
                    if (this.currentRecipeTime >= this.currentRecipe.getProcessTime()) {
                        this.currentRecipeTime = 0;
                        this.currentRecipeEnergy = 0;

                        ItemStack output = this.currentRecipe.assemble(
                                new RecipeWrapper(this.inventory.getCapability()));
                        if (this.inventory.getCapability().insertItem(1, output, true).isEmpty()) {
                            this.inventory.getCapability().insertItem(1, output, false);
                        } else {
                            Containers.dropItemStack(this.level, this.worldPosition.getX() + 0.5D,
                                    this.worldPosition.getY() + 1.0D, this.worldPosition.getZ() + 0.5D, output);
                        }

                        this.currentRecipe = null;
                    }
                }
            } else {
                this.currentRecipe = null;
                this.currentRecipeTime = 0;
                this.currentRecipeEnergy = 0;
            }
        }
    }
}
