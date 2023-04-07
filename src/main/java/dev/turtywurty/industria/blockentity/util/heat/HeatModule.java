package dev.turtywurty.industria.blockentity.util.heat;

import dev.turtywurty.turtylib.common.blockentity.ModularBlockEntity;
import dev.turtywurty.turtylib.common.blockentity.module.CapabilityModule;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;

import java.util.function.BiConsumer;

public class HeatModule implements CapabilityModule<IHeatHandler> {
    protected final ModularBlockEntity blockEntity;
    protected final IHeatHandler handler;
    protected final LazyOptional<IHeatHandler> lazy;
    protected final BiConsumer<Float, Float> onHeatChanged;

    public HeatModule(ModularBlockEntity blockEntity, IHeatHandler handler, BiConsumer<Float, Float> onHeatChanged) {
        this.blockEntity = blockEntity;
        this.handler = handler;
        this.lazy = LazyOptional.of(() -> this.handler);
        this.onHeatChanged = onHeatChanged;
    }

    public HeatModule(ModularBlockEntity blockEntity, Builder builder) {
        this(blockEntity, createHandler(blockEntity, builder), builder.onHeatChanged);
    }

    public HeatModule(ModularBlockEntity blockEntity, IHeatHandler handler) {
        this(blockEntity, handler, ($0, $1) -> {
        });
    }

    @Override
    public IHeatHandler getCapabilityInstance() {
        return this.handler;
    }

    @Override
    public Capability<IHeatHandler> getCapability() {
        return HeatHandler.CAPABILITY;
    }

    @Override
    public void invalidate() {
        this.lazy.invalidate();
    }

    @Override
    public void deserialize(ModularBlockEntity modularBlockEntity, CompoundTag compoundTag) {
        this.handler.deserializeNBT(compoundTag);
    }

    @Override
    public void serialize(ModularBlockEntity modularBlockEntity, CompoundTag compoundTag) {
        compoundTag.put("Heat", this.handler.serializeNBT());
    }

    public IHeatHandler getHandler() {
        return this.handler;
    }

    public LazyOptional<IHeatHandler> getLazy() {
        return this.lazy;
    }

    private static IHeatHandler createHandler(ModularBlockEntity blockEntity, Builder builder) {
        return new HeatHandler(builder.maxHeat, builder.heatTransferRate, builder.defaultHeat) {
            @Override
            public void onChanged(float oldHeat, float newHeat) {
                blockEntity.update();
                builder.onHeatChanged.accept(oldHeat, newHeat);
            }
        };
    }

    public static final class Builder {
        private int defaultHeat;
        private int maxHeat;
        private int heatTransferRate;
        private BiConsumer<Float, Float> onHeatChanged = ($0, $1) -> {
        };

        public Builder defaultHeat(int defaultHeat) {
            this.defaultHeat = defaultHeat;
            return this;
        }

        public Builder maxHeat(int maxHeat) {
            this.maxHeat = maxHeat;
            return this;
        }

        public Builder heatTransferRate(int heatTransferRate) {
            this.heatTransferRate = heatTransferRate;
            return this;
        }

        public Builder onHeatChanged(BiConsumer<Float, Float> onHeatChanged) {
            this.onHeatChanged = onHeatChanged;
            return this;
        }
    }
}
