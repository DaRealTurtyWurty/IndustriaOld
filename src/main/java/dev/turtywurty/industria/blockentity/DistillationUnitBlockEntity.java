package dev.turtywurty.industria.blockentity;

import dev.turtywurty.industria.blockentity.util.fluid.FluidModuleV2;
import dev.turtywurty.industria.blockentity.util.fluid.MultiFluidTank;
import dev.turtywurty.industria.init.BlockEntityInit;
import dev.turtywurty.industria.init.MultiblockInit;
import dev.turtywurty.turtylib.common.blockentity.ModularBlockEntity;
import dev.turtywurty.turtylib.common.blockentity.module.MultiblockModule;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;
import java.util.function.Consumer;

public class DistillationUnitBlockEntity extends ModularBlockEntity {
    private final FluidModuleV2 fluidModule;
    private final MultiblockModule multiblockModule;

    public DistillationUnitBlockEntity(BlockPos pos, BlockState state) {
        super(BlockEntityInit.DISTILLATION_UNIT.get(), pos, state);

        this.multiblockModule = new MultiblockModule(MultiblockInit.DISTILLATION_UNIT);
        this.fluidModule = new FluidModuleV2(this, new MultiFluidTank.Builder().withTanks(9, 10000).build());
    }

    public FluidModuleV2 getFluidModule() {
        return this.fluidModule;
    }

    public MultiblockModule getMultiblockModule() {
        return this.multiblockModule;
    }

    @Override
    protected List<Consumer<CompoundTag>> getReadSyncData() {
        return List.of(compoundTag -> this.fluidModule.deserialize(this, compoundTag));
    }

    @Override
    protected List<Consumer<CompoundTag>> getWriteSyncData() {
        return List.of(compoundTag -> this.fluidModule.serialize(this, compoundTag));
    }

    @Override
    public void tick() {
        super.tick();

        if (this.level == null || this.level.isClientSide) return;


    }
}