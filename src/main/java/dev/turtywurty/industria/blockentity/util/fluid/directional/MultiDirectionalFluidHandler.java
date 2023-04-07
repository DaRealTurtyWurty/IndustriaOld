package dev.turtywurty.industria.blockentity.util.fluid.directional;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MultiDirectionalFluidHandler implements IFluidHandler, INBTSerializable<ListTag> {
    private final List<MultiDirectionalFluidTank> tanks = new ArrayList<>();

    @Override
    public ListTag serializeNBT() {
        var list = new ListTag();
        this.tanks.forEach(tank -> {
            var compound = new CompoundTag();
//            var directions = new ListTag();
//            tank.getDirections().forEach(direction -> directions.add(ByteTag.valueOf((byte) direction.ordinal())));
//            compound.put("Directions", directions);
            compound.put("Tank", tank.serializeNBT());
            list.add(compound);
        });
        return list;
    }

    @Override
    public void deserializeNBT(ListTag nbt) {
        for (int index = 0; index < nbt.size(); index++) {
            var compound = (CompoundTag) nbt.get(index);

//            if (!compound.contains("Directions"))
//                continue;
//
//            ListTag directions = compound.getList("Directions", Tag.TAG_BYTE);
//            EnumSet<Direction> directionSet = EnumSet.noneOf(Direction.class);
//            for (Tag direction : directions) {
//                var directionByte = (ByteTag) direction;
//                directionSet.add(Direction.values()[directionByte.getAsByte()]);
//            }

            if (!compound.contains("Tank"))
                continue;

            MultiDirectionalFluidTank tank = this.tanks.get(index);
            tank.deserializeNBT(compound.getCompound("Tank"));
        }
    }

    @Override
    public int getTanks() {
        return this.tanks.size();
    }

    @Override
    public @NotNull FluidStack getFluidInTank(int tank) {
        return getSide(Direction.values()[tank]).map(MultiDirectionalFluidTank::getFluid).orElse(FluidStack.EMPTY);
    }

    @Override
    public int getTankCapacity(int tank) {
        return getSide(Direction.values()[tank]).map(MultiDirectionalFluidTank::getCapacity).orElse(0);
    }

    @Override
    public boolean isFluidValid(int tank, @NotNull FluidStack stack) {
        return getSide(Direction.values()[tank]).map(t -> t.isFluidValid(stack)).orElse(false);
    }

    @Override
    public int fill(FluidStack resource, FluidAction action) {
        int filled = 0;
        FluidStack resourceCopy = resource.copy();
        for (MultiDirectionalFluidTank tank : this.tanks) {
            if (tank.isFluidValid(resourceCopy)) {
                if (tank.getFluid().isFluidEqual(resourceCopy)) {
                    filled += tank.fill(resourceCopy, action);
                    resourceCopy.setAmount(resourceCopy.getAmount() - filled);
                    if (filled >= resource.getAmount())
                        return filled;
                }
            } else {
                filled += tank.fill(resourceCopy, action);
                resourceCopy.setAmount(resourceCopy.getAmount() - filled);
                if (filled == resource.getAmount())
                    return filled;
            }
        }

        return filled;
    }

    @Override
    public @NotNull FluidStack drain(FluidStack resource, FluidAction action) {
        FluidStack copy = resource.copy();
        FluidStack drained = FluidStack.EMPTY;
        for (MultiDirectionalFluidTank tank : this.tanks) {
            if (tank.getFluid().isFluidEqual(copy)) {
                drained = tank.drain(copy, action);
                copy.setAmount(copy.getAmount() - drained.getAmount());
                if (drained.getAmount() >= resource.getAmount())
                    return drained;
            }
        }

        return drained;
    }

    @Override
    public @NotNull FluidStack drain(int maxDrain, FluidAction action) {
        FluidStack drained = FluidStack.EMPTY;
        for (MultiDirectionalFluidTank tank : this.tanks) {
            drained = tank.drain(maxDrain, action);
            if (drained.getAmount() >= maxDrain)
                return drained;
        }

        return drained;
    }

    public Optional<MultiDirectionalFluidTank> getSide(Direction direction) {
        return this.tanks.stream().filter(tank -> tank.getDirections().contains(direction)).findFirst();
    }

    public LazyOptional<IFluidHandler> getLazy(Direction direction) {
        return getSide(direction).map(MultiDirectionalFluidTank::getHandler).orElse(LazyOptional.empty());
    }

    public void addTank(MultiDirectionalFluidTank tank) {
        this.tanks.add(tank);
    }

    public void removeTank(MultiDirectionalFluidTank tank) {
        this.tanks.remove(tank);
        tank.getHandler().invalidate();
    }

    public void removeTank(Direction direction) {
        getSide(direction).ifPresent(this::removeTank);
    }

    public void clearTanks() {
        this.tanks.forEach(this::removeTank);
    }

    public List<MultiDirectionalFluidTank> getFluidTanks() {
        return this.tanks;
    }
}
