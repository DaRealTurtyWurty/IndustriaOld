package dev.turtywurty.industria.blockentity;

import dev.turtywurty.industria.Industria;
import dev.turtywurty.industria.init.BlockEntityInit;
import dev.turtywurty.industria.init.RecipeInit;
import dev.turtywurty.industria.recipes.BiomassGeneratorRecipe;
import io.github.darealturtywurty.turtylib.common.blockentity.ModularBlockEntity;
import io.github.darealturtywurty.turtylib.common.blockentity.module.EnergyModule;
import io.github.darealturtywurty.turtylib.common.blockentity.module.InventoryModule;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.items.wrapper.RecipeWrapper;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class BiomassGeneratorBlockEntity extends ModularBlockEntity {
    public static final Component TITLE = Component.translatable("container." + Industria.MODID + ".biomass_generator");

    private final InventoryModule inventory;
    private final EnergyModule energy;
    private final ContainerData data = new ContainerData() {
        @Override
        public int get(int pIndex) {
            return switch (pIndex) {
                case 0 -> BiomassGeneratorBlockEntity.this.energy.getCapabilityInstance().getEnergyStored();
                case 1 -> BiomassGeneratorBlockEntity.this.energy.getCapabilityInstance().getMaxEnergyStored();
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
                case 0 -> BiomassGeneratorBlockEntity.this.energy.getCapabilityInstance().setEnergy(pValue);
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

        // Disperse energy
        List<Direction> directions = new ArrayList<>();
        for (Direction direction : Direction.values()) {
            final BlockEntity blockEntity = this.level.getBlockEntity(this.worldPosition.relative(direction));
            if(blockEntity != null && blockEntity.getCapability(ForgeCapabilities.ENERGY, direction.getOpposite()).isPresent()) {
                IEnergyStorage energyStorage = blockEntity.getCapability(ForgeCapabilities.ENERGY, direction.getOpposite()).orElse(null);
                if(energyStorage != null && energyStorage.canReceive()) {
                    directions.add(direction);
                }
            }
        }

        if(!directions.isEmpty() && this.energy.getCapabilityInstance().getEnergyStored() > 0) {
            int energyToDisperse = this.energy.getCapabilityInstance().getEnergyStored() / directions.size();
            for (Direction direction : directions) {
                final BlockEntity blockEntity = this.level.getBlockEntity(this.worldPosition.relative(direction));
                IEnergyStorage energyStorage = blockEntity.getCapability(ForgeCapabilities.ENERGY,
                        direction.getOpposite()).orElse(null);
                int energyReceived = energyStorage.receiveEnergy(energyToDisperse, false);
                this.energy.getCapabilityInstance().extractEnergy(energyReceived, false);
            }

            directions.clear();
        }


        // Check that we are not at the max energy
        if (this.energy.getCapabilityInstance().getEnergyStored() >= this.energy.getCapabilityInstance().getMaxEnergyStored()) return;

        // Get the recipe if it doesn't already exist
        if (this.currentRecipe == null) {
            this.currentRecipe = this.level.getRecipeManager().getRecipeFor(RecipeInit.BIOMASS_GENERATOR_TYPE.get(),
                    new RecipeWrapper(this.inventory.getCapabilityInstance()), this.level).orElse(null);
            return;
        }

        // Check if we are already generating energy
        if (this.amountGenerated < this.currentRecipe.getEnergy()) {
            this.amountGenerated++;
            this.energy.getCapabilityInstance().setEnergy(this.energy.getCapabilityInstance().getEnergyStored() + 1);
            return;
        }

        // Reset the variables
        this.amountGenerated = 0;
        this.currentRecipe.assemble(new RecipeWrapper(this.inventory.getCapabilityInstance()));
        this.currentRecipe = null;
    }

    @Override
    public void load(@NotNull CompoundTag nbt) {
        super.load(nbt);
        this.amountGenerated = nbt.getInt("AmountGenerated");
        this.currentRecipe = this.level.getRecipeManager().getRecipeFor(RecipeInit.BIOMASS_GENERATOR_TYPE.get(),
                new RecipeWrapper(this.inventory.getCapabilityInstance()), this.level).orElse(null);
    }

    @Override
    protected void saveAdditional(CompoundTag nbt) {
        super.saveAdditional(nbt);
        nbt.putInt("AmountGenerated", this.amountGenerated);
    }
}
