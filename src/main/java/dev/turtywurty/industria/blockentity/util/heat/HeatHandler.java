package dev.turtywurty.industria.blockentity.util.heat;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.util.Mth;

public class HeatHandler implements IHeatHandler {
    private float heat;
    protected final float maxHeat;
    protected final float heatTransferRate;

    public HeatHandler(float maxHeat, float heatTransferRate) {
        this.maxHeat = maxHeat;
        this.heatTransferRate = heatTransferRate;
    }

    public HeatHandler(float maxHeat) {
        this(maxHeat, 0);
    }

    public HeatHandler(float maxHeat, float heatTransferRate, float heat) {
        this(maxHeat, heatTransferRate);
        setHeat(heat);
    }

    @Override
    public float getHeat() {
        return this.heat;
    }

    @Override
    public float getMaxHeat() {
        return this.maxHeat;
    }

    @Override
    public float getHeatTransferRate() {
        return this.heatTransferRate;
    }

    @Override
    public float receiveHeat(float heat, boolean simulate) {
        return receiveHeat(heat, this.maxHeat, simulate);
    }

    @Override
    public float receiveHeat(float heat, float maxTransfer, boolean simulate) {
        final float heatReceived = Mth.clamp(heat, 0, maxTransfer);
        if (!simulate) {
            setHeat(this.heat + heatReceived);
        }

        return heat - heatReceived;
    }

    @Override
    public float extractHeat(float heat, boolean simulate) {
        return extractHeat(heat, this.maxHeat, simulate);
    }

    @Override
    public float extractHeat(float heat, float maxTransfer, boolean simulate) {
        final float heatExtracted = Mth.clamp(heat, 0, maxTransfer);
        if (!simulate) {
            setHeat(this.heat - heatExtracted);
        }

        return heat - heatExtracted;
    }

    @Override
    public void setHeat(float heat) {
        float oldHeat = this.heat;
        this.heat = Mth.clamp(heat, 0, this.maxHeat);

        onChanged(oldHeat, this.heat);
    }

    @Override
    public CompoundTag serializeNBT() {
        var nbt = new CompoundTag();
        nbt.putFloat("Value", getHeat());
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        if (nbt.contains("Heat", Tag.TAG_COMPOUND)) {
            this.heat = Mth.clamp(nbt.getCompound("Heat").getFloat("Value"), 0, this.maxHeat);
        }
    }
}
