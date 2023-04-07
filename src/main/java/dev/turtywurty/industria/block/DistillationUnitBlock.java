package dev.turtywurty.industria.block;

import dev.turtywurty.industria.blockentity.DistillationUnitBlockEntity;
import dev.turtywurty.industria.client.screens.DistillationUnitScreen;
import dev.turtywurty.industria.init.BlockEntityInit;
import dev.turtywurty.turtylib.common.blockentity.TickableBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import org.jetbrains.annotations.Nullable;

public class DistillationUnitBlock extends Block implements EntityBlock {
    public DistillationUnitBlock() {
        super(Properties.of(Material.METAL).strength(3.5f, 3.5f).sound(SoundType.METAL).noOcclusion());
    }

    public static InteractionResult use(Level level, BlockPos blockPos, Player player) {
        if (level.isClientSide()) {
            if (level.getBlockEntity(blockPos) instanceof DistillationUnitBlockEntity blockEntity) {
                DistExecutor.unsafeRunWhenOn(Dist.CLIENT,
                        () -> () -> Minecraft.getInstance().setScreen(new DistillationUnitScreen()));
            }
        }

        return InteractionResult.sidedSuccess(level.isClientSide());
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return BlockEntityInit.DISTILLATION_UNIT.get().create(pPos, pState);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> pBlockEntityType) {
        return pLevel.isClientSide() ? null : ($0, $1, $2, blockEntity) -> ((TickableBlockEntity) blockEntity).tick();
    }
}
