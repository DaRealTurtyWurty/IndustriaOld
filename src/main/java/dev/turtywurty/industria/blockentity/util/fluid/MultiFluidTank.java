package dev.turtywurty.industria.blockentity.util.fluid;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

/**
 * A MultiFluidTank is a {@link IMultiTank} that can hold multiple {@link FluidTank}s.
 */
public class MultiFluidTank implements IMultiTank, INBTSerializable<ListTag> {
    private final FluidTank[] tanks;

    /**
     * Creates a new MultiFluidTank with the given FluidTanks.
     *
     * @apiNote Use the {@link MultiFluidTank.Builder} for an easy way to create a new MultiFluidTank.
     *
     * @param tanks The FluidTanks to use.
     */
    public MultiFluidTank(FluidTank... tanks) {
        this.tanks = tanks;
    }

    @Override
    public int fill(int tank, FluidStack resource, FluidAction action) {
        FluidTank fluidTank = this.tanks[tank];
        return fluidTank.fill(resource, action);
    }

    @Override
    public FluidStack drain(int tank, FluidStack resource, FluidAction action) {
        FluidTank fluidTank = this.tanks[tank];
        return fluidTank.drain(resource, action);
    }

    @Override
    public FluidStack drain(int tank, int maxDrain, FluidAction action) {
        FluidTank fluidTank = this.tanks[tank];
        return fluidTank.drain(maxDrain, action);
    }

    @Override
    public int getTanks() {
        return this.tanks.length;
    }

    @Override
    public @NotNull FluidStack getFluidInTank(int tank) {
        return this.tanks[tank].getFluid();
    }

    @Override
    public int getTankCapacity(int tank) {
        return this.tanks[tank].getCapacity();
    }

    @Override
    public boolean isFluidValid(int tank, @NotNull FluidStack stack) {
        return this.tanks[tank].isFluidValid(stack);
    }

    @Override
    public int fill(FluidStack resource, FluidAction action) {
        int filled = 0;
        FluidStack remaining = resource.copy();
        for (FluidTank tank : this.tanks) {
            if (remaining.isEmpty()) break;
            filled += tank.fill(remaining, action);
            remaining.setAmount(resource.getAmount() - filled);
        }

        return filled;
    }

    @Override
    public @NotNull FluidStack drain(FluidStack resource, FluidAction action) {
        int drained = 0;
        FluidStack remaining = resource.copy();
        for (FluidTank tank : this.tanks) {
            if (remaining.isEmpty()) break;
            drained += tank.drain(remaining, action).getAmount();
            remaining.setAmount(resource.getAmount() - drained);
        }

        return new FluidStack(resource, drained);
    }

    @Override
    public @NotNull FluidStack drain(int maxDrain, FluidAction action) {
        int drained = 0;
        FluidStack remaining = new FluidStack(this.tanks[0].getFluid(), maxDrain);
        for (FluidTank tank : this.tanks) {
            if (remaining.isEmpty()) break;
            drained += tank.drain(remaining, action).getAmount();
            remaining.setAmount(maxDrain - drained);
        }

        return new FluidStack(this.tanks[0].getFluid(), drained);
    }

    @Override
    public ListTag serializeNBT() {
        var list = new ListTag();
        for (int index = 0; index < this.tanks.length; index++) {
            list.add(index, this.tanks[index].writeToNBT(new CompoundTag()));
        }

        return list;
    }

    @Override
    public void deserializeNBT(ListTag nbt) {
        for (int index = 0; index < this.tanks.length; index++) {
            this.tanks[index].readFromNBT(nbt.getCompound(index));
        }
    }

    public static class Builder {
        private final List<FluidTank> tanks = new ArrayList<>();

        public Builder withTank(int capacity) {
            this.tanks.add(new FluidTank(capacity));
            return this;
        }

        public Builder withTank(FluidTank tank) {
            this.tanks.add(tank);
            return this;
        }

        public Builder withTank(int capacity, Predicate<FluidStack> validator) {
            this.tanks.add(new FluidTank(capacity, validator));
            return this;
        }

        public Builder withTank(FluidStack fluid, int capacity) {
            var tank = new FluidTank(capacity);
            tank.setFluid(fluid);
            this.tanks.add(tank);
            return this;
        }

        public Builder withTank(FluidStack fluid, int capacity, Predicate<FluidStack> validator) {
            var tank = new FluidTank(capacity, validator);
            tank.setFluid(fluid);
            this.tanks.add(tank);
            return this;
        }

        public Builder withTanks(int tankCount, int tankCapacity) {
            for (int index = 0; index < tankCount; index++) {
                this.tanks.add(new FluidTank(tankCapacity));
            }

            return this;
        }

        public Builder withTanks(int tankCount, int tankCapacity, Predicate<FluidStack> validator) {
            for (int index = 0; index < tankCount; index++) {
                this.tanks.add(new FluidTank(tankCapacity, validator));
            }

            return this;
        }

        public Builder withTanks(FluidTank... tanks) {
            this.tanks.addAll(Arrays.asList(tanks));

            return this;
        }

        public Builder withTanks(FluidStack fluid, int tankCount, int tankCapacity) {
            for (int index = 0; index < tankCount; index++) {
                var tank = new FluidTank(tankCapacity);
                tank.setFluid(fluid);
                this.tanks.add(tank);
            }

            return this;
        }

        public Builder withTanks(FluidStack fluid, int tankCount, int tankCapacity, Predicate<FluidStack> validator) {
            for (int index = 0; index < tankCount; index++) {
                var tank = new FluidTank(tankCapacity, validator);
                tank.setFluid(fluid);
                this.tanks.add(tank);
            }

            return this;
        }

        public MultiFluidTank build() {
            return new MultiFluidTank(this.tanks.toArray(new FluidTank[0]));
        }
    }
}
