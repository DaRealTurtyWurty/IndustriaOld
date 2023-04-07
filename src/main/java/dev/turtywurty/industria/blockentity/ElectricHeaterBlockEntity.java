package dev.turtywurty.industria.blockentity;

import dev.turtywurty.industria.blockentity.util.heat.HeatModule;
import dev.turtywurty.industria.init.BlockEntityInit;
import dev.turtywurty.turtylib.common.blockentity.ModularBlockEntity;
import dev.turtywurty.turtylib.common.blockentity.module.EnergyModule;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.energy.EnergyStorage;

import java.util.List;
import java.util.function.Consumer;

public class ElectricHeaterBlockEntity extends ModularBlockEntity {
    private final EnergyModule energyModule;
    private final HeatModule heatModule;

    public ElectricHeaterBlockEntity(BlockPos pos, BlockState state) {
        super(BlockEntityInit.ELECTRIC_HEATER.get(), pos, state);

        this.energyModule = new EnergyModule(this, new EnergyModule.Builder().capacity(10000).maxReceive(100));
        this.heatModule = new HeatModule(this,
                new HeatModule.Builder().maxHeat(2273).defaultHeat(300).heatTransferRate(100));
    }

    @Override
    protected List<Consumer<CompoundTag>> getReadSyncData() {
        return List.of(compoundTag -> this.energyModule.deserialize(this, compoundTag),
                compoundTag -> this.heatModule.deserialize(this, compoundTag));
    }

    @Override
    protected List<Consumer<CompoundTag>> getWriteSyncData() {
        return List.of(compoundTag -> this.energyModule.serialize(this, compoundTag),
                compoundTag -> this.heatModule.serialize(this, compoundTag));
    }

    public EnergyStorage getEnergyStorage() {
        return this.energyModule.getCapabilityInstance();
    }

    public HeatModule getHeatModule() {
        return this.heatModule;
    }

    @Override
    public void tick() {
        super.tick();

        if (this.level == null || this.level.isClientSide) return;

        int energy = this.energyModule.getCapabilityInstance().getEnergyStored();
        if (energy <= 0) return;

        float transferRate = Math.min(this.heatModule.getCapabilityInstance().getHeatTransferRate() * 4.13f,
                this.energyModule.getCapabilityInstance().extractEnergy(Integer.MAX_VALUE, true));

        float energyConsumed = this.energyModule.getCapabilityInstance().extractEnergy((int) transferRate, false);
        float heatAdded = energyConsumed / 4.13f;
        this.heatModule.getCapabilityInstance().receiveHeat(heatAdded, false);
    }
}
