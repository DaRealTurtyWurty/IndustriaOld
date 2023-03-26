package dev.turtywurty.industria.blockentity.util.directional;

import dev.turtywurty.turtylib.common.blockentity.ModularBlockEntity;
import dev.turtywurty.turtylib.common.blockentity.module.SidedCapabilityModule;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;
import java.util.Optional;
import java.util.function.Predicate;

public class MultiDirectionalFluidModule extends SidedCapabilityModule<IFluidHandler> {
    private final ModularBlockEntity blockEntity;
    private final MultiDirectionalFluidHandler fluidHandler;

    public MultiDirectionalFluidModule(Builder builder) {
        this.blockEntity = builder.blockEntity;
        this.fluidHandler = builder.fluidHandler;
    }

    public Optional<MultiDirectionalFluidTank> getTank(Direction direction) {
        return this.fluidHandler.getSide(direction);
    }

    public @Nullable MultiDirectionalFluidTank getNullableTank(Direction direction) {
        return getTank(direction).orElse(null);
    }

    @Override
    public LazyOptional<IFluidHandler> getLazy(Direction direction) {
        return this.fluidHandler.getLazy(direction).cast();
    }

    @Override
    public IFluidHandler getCapabilityInstance(Direction direction) {
        return getNullableTank(direction);
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

    public MultiDirectionalFluidHandler getFluidHandler() {
        return this.fluidHandler;
    }

    public ModularBlockEntity getBlockEntity() {
        return this.blockEntity;
    }

    public static class Builder {
        private final ModularBlockEntity blockEntity;
        private final MultiDirectionalFluidHandler fluidHandler;
        private final EnumSet<Direction> usedDirections = EnumSet.noneOf(Direction.class);
        private Runnable updateCallback;

        public Builder(ModularBlockEntity blockEntity) {
            this.blockEntity = blockEntity;
            this.fluidHandler = new MultiDirectionalFluidHandler();
            this.updateCallback = this.blockEntity::update;
        }

        public Builder addTank(int capacity, Direction direction, Direction... directions) {
            validateDirections(direction, directions);

            var tank = new ListeningFluidTank(capacity, this.updateCallback);
            var wrapper = new MultiDirectionalFluidTank(tank, EnumSet.of(direction, directions));
            this.fluidHandler.addTank(wrapper);

            return this;
        }

        public Builder addTank(int capacity, Predicate<FluidStack> validator, Direction direction, Direction... directions) {
            validateDirections(direction, directions);

            var tank = new ListeningFluidTank(capacity, validator, this.updateCallback);
            var wrapper = new MultiDirectionalFluidTank(tank, EnumSet.of(direction, directions));
            this.fluidHandler.addTank(wrapper);

            return this;
        }

        public Builder addTank(int capacity, FluidStack defaultFluid, Direction direction, Direction... directions) {
            validateDirections(direction, directions);

            var tank = new ListeningFluidTank(capacity, this.updateCallback);
            tank.setFluid(defaultFluid);
            var wrapper = new MultiDirectionalFluidTank(tank, EnumSet.of(direction, directions));
            this.fluidHandler.addTank(wrapper);

            return this;
        }

        public Builder addTank(int capacity, Predicate<FluidStack> validator, FluidStack defaultFluid, Direction direction, Direction... directions) {
            validateDirections(direction, directions);

            var tank = new ListeningFluidTank(capacity, validator, this.updateCallback);
            tank.setFluid(defaultFluid);
            var wrapper = new MultiDirectionalFluidTank(tank, EnumSet.of(direction, directions));
            this.fluidHandler.addTank(wrapper);

            return this;
        }

        private void validateDirections(Direction direction, Direction... directions) {
            if (this.usedDirections.contains(direction))
                throw new IllegalArgumentException("Direction " + direction + " is already used!");
            this.usedDirections.add(direction);
            for (Direction dir : directions) {
                if (this.usedDirections.contains(dir))
                    throw new IllegalArgumentException("Direction " + dir + " is already used!");
                this.usedDirections.add(dir);
            }
        }

        public Builder updateCallback(Runnable updateCallback) {
            this.updateCallback = updateCallback;
            return this;
        }
    }
}
