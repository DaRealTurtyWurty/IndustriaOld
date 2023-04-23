package dev.turtywurty.industria.blockentity;

import dev.turtywurty.industria.blockentity.util.heat.HeatModule;
import dev.turtywurty.industria.init.BlockEntityInit;
import dev.turtywurty.turtylib.common.blockentity.ModularBlockEntity;
import dev.turtywurty.turtylib.common.blockentity.module.EnergyModule;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.energy.EnergyStorage;

public class ElectricHeaterBlockEntity extends ModularBlockEntity {
    private static final float ROOM_TEMPERATURE = 294.15F;
    private static final float SHC_WATER = 4.13F;

    private final EnergyModule energyModule;
    private final HeatModule heatModule;

    private float targetTemperature = 573.15F;

    public ElectricHeaterBlockEntity(BlockPos pos, BlockState state) {
        super(BlockEntityInit.ELECTRIC_HEATER.get(), pos, state);

        this.energyModule = addModule(
                new EnergyModule(this, new EnergyModule.Builder().capacity(10000).maxReceive(10000)));
        this.heatModule = addModule(new HeatModule(this,
                new HeatModule.Builder().maxHeat(2273).defaultHeat(ROOM_TEMPERATURE).heatTransferRate(2273)));
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

        this.targetTemperature = 1500;

        if (this.level == null || this.level.isClientSide) return;

        int energy = this.energyModule.getCapabilityInstance().getEnergyStored();
        if (energy <= 0) return;

        // 400K. Room Temp ~300K. 100K. 100K * 4.13 -> 413 RF/T
        // 2173K - 300K -> 1873K * 4.13 -> 7735 RF/T
        // 673 - 300 -> 373 * 4.13 -> 1540 RF/T
        // 573.15K - 294.15K -> 279 * 4.13 -> 1152.27 -> 1153 RF/T
        float tempMinusRoom = this.targetTemperature - ROOM_TEMPERATURE;

        int energyRequired = (int) Math.ceil(tempMinusRoom * SHC_WATER);
        int energyUsable = this.energyModule.getCapabilityInstance().extractEnergyInternal(energyRequired, true);
        float resultTemperature = energyUsable / SHC_WATER;

        this.energyModule.getCapabilityInstance().extractEnergyInternal(energyUsable, false);
        this.heatModule.getCapabilityInstance().setHeat(ROOM_TEMPERATURE + resultTemperature);
    }

    public void setTargetTemperature(float targetTemperature) {
        this.targetTemperature = Mth.clamp(targetTemperature, ROOM_TEMPERATURE,
                this.heatModule.getCapabilityInstance().getMaxHeat());
    }

    public float getTargetTemperature() {
        return this.targetTemperature;
    }
}
