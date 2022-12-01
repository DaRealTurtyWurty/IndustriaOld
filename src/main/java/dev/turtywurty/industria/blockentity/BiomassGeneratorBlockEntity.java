package dev.turtywurty.industria.blockentity;

import dev.turtywurty.industria.Industria;
import dev.turtywurty.industria.init.BlockEntityInit;
import dev.turtywurty.industria.init.RecipeInit;
import dev.turtywurty.industria.recipes.BiomassGeneratorRecipe;
import io.github.darealturtywurty.turtylib.common.blockentity.ModularBlockEntity;
import io.github.darealturtywurty.turtylib.common.blockentity.module.EnergyModule;
import io.github.darealturtywurty.turtylib.common.blockentity.module.InventoryModule;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.items.wrapper.RecipeWrapper;
import org.jetbrains.annotations.NotNull;

public class BiomassGeneratorBlockEntity extends ModularBlockEntity {
    public static final Component TITLE = Component.translatable("container." + Industria.MODID + ".biomass_generator");

    private final InventoryModule inventory;
    private final EnergyModule energy;
    private final ContainerData data = new ContainerData() {
        @Override
        public int get(int pIndex) {
            return switch (pIndex) {
                case 0 -> BiomassGeneratorBlockEntity.this.energy.getCapability().getEnergyStored();
                case 1 -> BiomassGeneratorBlockEntity.this.energy.getCapability().getMaxEnergyStored();
                case 2 -> BiomassGeneratorBlockEntity.this.amountGenerated;
                case 3 ->
                        BiomassGeneratorBlockEntity.this.currentRecipe != null ? BiomassGeneratorBlockEntity.this.currentRecipe.getEnergy() : 0;
                default -> {
                    Industria.LOGGER.error("Invalid index for BiomassGeneratorBlockEntity data: " + pIndex);
                    yield 0;
                }
            };
        }

        @Override
        public void set(int pIndex, int pValue) {
            switch (pIndex) {
                case 0 -> BiomassGeneratorBlockEntity.this.energy.getCapability().setEnergy(pValue);
                case 2 -> BiomassGeneratorBlockEntity.this.amountGenerated = pValue;
                default -> {
                }
            }
        }

        @Override
        public int getCount() {
            return 4;
        }
    };

    private int amountGenerated = 0;
    private BiomassGeneratorRecipe currentRecipe;

    public BiomassGeneratorBlockEntity(BlockPos pos, BlockState state) {
        super(BlockEntityInit.BIOMASS_GENERATOR.get(), pos, state);

        this.inventory = new InventoryModule(this, 1);
        this.energy = new EnergyModule(this, new EnergyModule.Builder().capacity(10000).maxExtract(100).maxReceive(0));
    }

    public InventoryModule getInventory() {
        return this.inventory;
    }

    public EnergyModule getEnergy() {
        return this.energy;
    }

    public ContainerData getContainerData() {
        return this.data;
    }

    @Override
    public void tick() {
        super.tick();

        // Check that we are in a server level
        if (this.level == null || this.level.isClientSide) return;

        // Check that we are not at the max energy
        if (this.energy.getCapability().getEnergyStored() >= this.energy.getCapability().getMaxEnergyStored()) return;

        // Get the recipe if it doesn't already exist
        if (this.currentRecipe == null) {
            this.currentRecipe = this.level.getRecipeManager().getRecipeFor(RecipeInit.BIOMASS_GENERATOR_TYPE.get(),
                    new RecipeWrapper(this.inventory.getCapability()), this.level).orElse(null);
            return;
        }

        // Check if we are already generating energy
        if (this.amountGenerated < this.currentRecipe.getEnergy()) {
            this.amountGenerated++;
            this.energy.getCapability().setEnergy(this.energy.getCapability().getEnergyStored() + 1);
            return;
        }

        // Reset the variables
        this.amountGenerated = 0;
        this.currentRecipe.assemble(new RecipeWrapper(this.inventory.getCapability()));
        this.currentRecipe = null;
    }

    @Override
    public void load(@NotNull CompoundTag nbt) {
        super.load(nbt);
        this.amountGenerated = nbt.getInt("AmountGenerated");
        this.currentRecipe = this.level.getRecipeManager().getRecipeFor(RecipeInit.BIOMASS_GENERATOR_TYPE.get(),
                new RecipeWrapper(this.inventory.getCapability()), this.level).orElse(null);
    }

    @Override
    protected void saveAdditional(CompoundTag nbt) {
        super.saveAdditional(nbt);
        nbt.putInt("AmountGenerated", this.amountGenerated);
    }
}
