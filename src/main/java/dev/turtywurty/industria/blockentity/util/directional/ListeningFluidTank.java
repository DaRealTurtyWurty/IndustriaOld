package dev.turtywurty.industria.blockentity.util.directional;

import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import org.jetbrains.annotations.NotNull;

import java.util.function.Predicate;

public class ListeningFluidTank extends FluidTank {
    private final Runnable updateCallback;

    public ListeningFluidTank(int capacity, Runnable updateCallback) {
        super(capacity);
        this.updateCallback = updateCallback;
    }

    public ListeningFluidTank(int capacity, Predicate<FluidStack> validator, Runnable updateCallback) {
        super(capacity, validator);
        this.updateCallback = updateCallback;
    }

    @Override
    protected void onContentsChanged() {
        super.onContentsChanged();
        this.updateCallback.run();
    }

    @Override
    public void setFluid(FluidStack stack) {
        super.setFluid(stack);
        onContentsChanged();
    }

    @Override
    public @NotNull FluidStack drain(int maxDrain, FluidAction action) {
        FluidStack drained = super.drain(maxDrain, action);
        if (!drained.isEmpty())
            onContentsChanged();

        return drained;
    }

    @Override
    public @NotNull FluidStack drain(FluidStack resource, FluidAction action) {
        FluidStack drained = super.drain(resource, action);
        if (!drained.isEmpty())
            onContentsChanged();

        return drained;
    }

    @Override
    public int fill(FluidStack resource, FluidAction action) {
        int filed = super.fill(resource, action);
        if (filed > 0)
            onContentsChanged();

        return filed;
    }
}
