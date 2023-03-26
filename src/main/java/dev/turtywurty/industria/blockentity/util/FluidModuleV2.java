package dev.turtywurty.industria.blockentity.util;

import dev.turtywurty.turtylib.common.blockentity.ModularBlockEntity;
import dev.turtywurty.turtylib.common.blockentity.module.CapabilityModule;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.capability.IFluidHandler;

public class FluidModuleV2 implements CapabilityModule<IFluidHandler> {
    protected final ModularBlockEntity blockEntity;
    protected final MultiFluidTank tank;
    protected final LazyOptional<IFluidHandler> handler;

    public FluidModuleV2(ModularBlockEntity blockEntity, MultiFluidTank tank) {
        this.blockEntity = blockEntity;

        this.tank = tank;
        this.handler = LazyOptional.of(() -> this.tank);
    }

    @Override
    public IFluidHandler getCapabilityInstance() {
        return this.tank;
    }

    @Override
    public Capability<IFluidHandler> getCapability() {
        return ForgeCapabilities.FLUID_HANDLER;
    }

    @Override
    public void invalidate() {
        this.handler.invalidate();
    }

    @Override
    public void deserialize(ModularBlockEntity modularBlockEntity, CompoundTag compoundTag) {
        this.tank.deserializeNBT(compoundTag.getList("tank", Tag.TAG_COMPOUND));
    }

    @Override
    public void serialize(ModularBlockEntity modularBlockEntity, CompoundTag compoundTag) {
        compoundTag.put("tank", this.tank.serializeNBT());
    }
}
