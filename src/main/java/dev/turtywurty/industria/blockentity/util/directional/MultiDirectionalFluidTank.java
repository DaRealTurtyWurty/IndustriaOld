package dev.turtywurty.industria.blockentity.util.directional;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import org.jetbrains.annotations.NotNull;

import java.util.EnumSet;

public class MultiDirectionalFluidTank implements IFluidHandler, INBTSerializable<CompoundTag> {
    private final FluidTank tank;
    private final EnumSet<Direction> directions;
    private final LazyOptional<IFluidHandler> handler;

    public MultiDirectionalFluidTank(FluidTank tank, EnumSet<Direction> directions) {
        this.tank = tank;
        this.directions = directions;
        this.handler = LazyOptional.of(() -> this);
    }

    @Override
    public int getTanks() {
        return 1;
    }

    @Override
    public @NotNull FluidStack getFluidInTank(int tank) {
        return this.tank.getFluid();
    }

    @Override
    public int getTankCapacity(int tank) {
        return this.tank.getCapacity();
    }

    @Override
    public boolean isFluidValid(int tank, @NotNull FluidStack stack) {
        return this.tank.isFluidValid(stack);
    }

    @Override
    public int fill(FluidStack resource, FluidAction action) {
        return this.tank.fill(resource, action);
    }

    @Override
    public @NotNull FluidStack drain(FluidStack resource, FluidAction action) {
        return this.tank.drain(resource, action);
    }

    @Override
    public @NotNull FluidStack drain(int maxDrain, FluidAction action) {
        return this.tank.drain(maxDrain, action);
    }

    public int getCapacity() {
        return getTankCapacity(0);
    }

    public FluidStack getFluid() {
        return getFluidInTank(0).copy();
    }

    public boolean isFluidValid(FluidStack stack) {
        return isFluidValid(0, stack);
    }

    public EnumSet<Direction> getDirections() {
        return this.directions;
    }

    public FluidTank getTank() {
        return this.tank;
    }

    @Override
    public CompoundTag serializeNBT() {
        return this.tank.writeToNBT(new CompoundTag());
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        this.tank.readFromNBT(nbt);
    }

    public LazyOptional<IFluidHandler> getHandler() {
        return this.handler;
    }
}
