package dev.turtywurty.industria.blockentity.util;

import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;

public interface IMultiTank extends IFluidHandler {
    int fill(int tank, FluidStack resource, FluidAction action);
    FluidStack drain(int tank, FluidStack resource, FluidAction action);
    FluidStack drain(int tank, int maxDrain, FluidAction action);
}
