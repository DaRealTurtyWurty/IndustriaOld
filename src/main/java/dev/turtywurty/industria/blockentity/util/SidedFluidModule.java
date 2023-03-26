package dev.turtywurty.industria.blockentity.util;

import dev.turtywurty.turtylib.common.blockentity.ModularBlockEntity;
import dev.turtywurty.turtylib.common.blockentity.module.SidedCapabilityModule;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import org.jetbrains.annotations.NotNull;

import java.util.*;

// TODO: Just fucking delete and rewrite
public class SidedFluidModule extends SidedCapabilityModule<IFluidHandler> {
    private final SidedFluidHandler fluidHandler;
    private final ModularBlockEntity blockEntity;

    /**
     * Creates a new SidedFluidModule with the given SidedFluidHandler and ModularBlockEntity.
     *
     * @param fluidHandler The SidedFluidHandler to use.
     * @param blockEntity  The ModularBlockEntity to use.
     * @apiNote Use {@link SidedFluidModule.Builder} to easily create a new SidedFluidModule.
     */
    private SidedFluidModule(SidedFluidHandler fluidHandler, ModularBlockEntity blockEntity) {
        this.fluidHandler = fluidHandler;
        this.blockEntity = blockEntity;
    }

    @Override
    public Capability<IFluidHandler> getCapability() {
        return ForgeCapabilities.FLUID_HANDLER;
    }

    @Override
    public void deserialize(ModularBlockEntity modularBlockEntity, CompoundTag compoundTag) {
        this.fluidHandler.deserializeNBT(compoundTag.getList("tank", Tag.TAG_COMPOUND));
    }

    @Override
    public void serialize(ModularBlockEntity modularBlockEntity, CompoundTag compoundTag) {
        compoundTag.put("tank", this.fluidHandler.serializeNBT());
    }

    public static class SidedFluidHandler implements IMultiTank, INBTSerializable<ListTag> {
        private final List<FluidTankWrapper> tanks = new ArrayList<>();

        private SidedFluidHandler(Collection<FluidTankWrapper> tanks) {
            this.tanks.addAll(tanks);
        }

        private Optional<FluidTankWrapper> getOptionalTank(Direction direction) {
            return this.tanks.stream().filter(tank -> Arrays.asList(tank.directions).contains(direction)).findFirst();
        }

        public FluidTankWrapper getTank(Direction direction) {
            return this.getOptionalTank(direction).orElse(null);
        }

        public int fill(Direction direction, FluidStack resource, FluidAction action) {
            return this.getOptionalTank(direction).map(tank -> tank.getTank().fill(resource, action)).orElse(0);
        }

        public FluidStack drain(Direction direction, FluidStack resource, FluidAction action) {
            return this.getOptionalTank(direction).map(tank -> tank.getTank().drain(resource, action))
                    .orElse(FluidStack.EMPTY);
        }

        public FluidStack drain(Direction direction, int maxDrain, FluidAction action) {
            return this.getOptionalTank(direction).map(tank -> tank.getTank().drain(maxDrain, action))
                    .orElse(FluidStack.EMPTY);
        }

        @Override
        public int fill(int tank, FluidStack resource, FluidAction action) {
            return fill(Direction.UP, resource, action);
        }

        @Override
        public FluidStack drain(int tank, FluidStack resource, FluidAction action) {
            return drain(Direction.UP, resource, action);
        }

        @Override
        public FluidStack drain(int tank, int maxDrain, FluidAction action) {
            return drain(Direction.UP, maxDrain, action);
        }

        @Override
        public ListTag serializeNBT() {
            var list = new ListTag();
            this.tanks.forEach(tank -> list.add(tank.getTank().writeToNBT(new CompoundTag())));
            return list;
        }

        @Override
        public void deserializeNBT(ListTag nbt) {
            for (int tank = 0; tank < nbt.size(); tank++) {
                this.tanks.get(tank).getTank().readFromNBT(nbt.getCompound(tank));
            }
        }

        @Override
        public int getTanks() {
            return this.tanks.size();
        }

        @Override
        public @NotNull FluidStack getFluidInTank(int tank) {
            return this.tanks.get(tank).getTank().getFluid();
        }

        @Override
        public int getTankCapacity(int tank) {
            return this.tanks.get(tank).getTank().getCapacity();
        }

        @Override
        public boolean isFluidValid(int tank, @NotNull FluidStack stack) {
            return this.tanks.get(tank).getTank().isFluidValid(stack);
        }

        @Override
        public int fill(FluidStack resource, FluidAction action) {
            return fill(Direction.UP, resource, action);
        }

        @Override
        public @NotNull FluidStack drain(FluidStack resource, FluidAction action) {
            return drain(Direction.UP, resource, action);
        }

        @Override
        public @NotNull FluidStack drain(int maxDrain, FluidAction action) {
            return drain(Direction.UP, maxDrain, action);
        }

        public static class Builder {
            private final List<FluidTankWrapper> wrappers = new ArrayList<>();
            private Set<Direction> usedDirections = new HashSet<>();

            public Builder addTank(FluidTank tank, Direction direction, Direction... directions) {
                if (this.usedDirections.contains(direction))
                    throw new IllegalArgumentException("Direction " + direction + " is already used!");
                this.usedDirections.add(direction);

                for (Direction dir : directions) {
                    if (this.usedDirections.contains(dir))
                        throw new IllegalArgumentException("Direction " + dir + " is already used!");
                    this.usedDirections.add(dir);
                }

                Direction[] dirs = new Direction[directions.length + 1];
                dirs[0] = direction;
                System.arraycopy(directions, 0, dirs, 1, directions.length);
                this.wrappers.add(new FluidTankWrapper(tank, dirs));

                return this;
            }

            private List<FluidTankWrapper> build() {
                return this.wrappers;
            }
        }
    }

    public static class FluidTankWrapper {
        private final FluidTank tank;
        private final Direction[] directions;
        private final LazyOptional<FluidTank> lazy;

        private FluidTankWrapper(FluidTank tank, Direction... directions) {
            this.tank = tank;
            this.directions = directions;
            this.lazy = LazyOptional.of(() -> this.tank);
        }

        public FluidTank getTank() {
            return this.tank;
        }

        public Direction[] getDirections() {
            return this.directions;
        }

        public LazyOptional<FluidTank> getLazy() {
            return this.lazy;
        }
    }

    public static class Builder {
        private final SidedFluidHandler fluidHandler;
        private final ModularBlockEntity blockEntity;

        public Builder(ModularBlockEntity blockEntity, SidedFluidHandler.Builder builder) {
            this.blockEntity = blockEntity;
            this.fluidHandler = new SidedFluidHandler(builder.build());
        }

        public Builder(ModularBlockEntity blockEntity, SidedFluidHandler fluidHandler) {
            this.blockEntity = blockEntity;
            this.fluidHandler = fluidHandler;
        }

        public SidedFluidModule build() {
            return new SidedFluidModule(this.fluidHandler, this.blockEntity);
        }
    }
}
