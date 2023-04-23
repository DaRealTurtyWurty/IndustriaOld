package dev.turtywurty.industria.block;

import dev.turtywurty.industria.blockentity.DistillationTowerBlockEntity;
import dev.turtywurty.industria.client.screens.DistillationTowerScreen;
import dev.turtywurty.industria.init.BlockEntityInit;
import dev.turtywurty.turtylib.common.blockentity.TickableBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import org.jetbrains.annotations.Nullable;

public class DistillationTowerBlock extends Block implements EntityBlock {
    private static final VoxelShape SHAPE = makeShape();

    public DistillationTowerBlock() {
        super(Properties.of(Material.METAL).strength(3.5f, 3.5f).sound(SoundType.METAL).noOcclusion());
    }

    public static InteractionResult use(Level level, BlockPos blockPos, Player player) {
        if (level.isClientSide()) {
            if (level.getBlockEntity(blockPos) instanceof DistillationTowerBlockEntity blockEntity) {
                DistExecutor.unsafeRunWhenOn(Dist.CLIENT,
                        () -> () -> Minecraft.getInstance().setScreen(new DistillationTowerScreen(blockEntity)));
            }
        }

        return InteractionResult.sidedSuccess(level.isClientSide());
    }

    @Override
    public InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHit) {
        return use(pLevel, pPos, pPlayer);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return BlockEntityInit.DISTILLATION_TOWER.get().create(pPos, pState);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> pBlockEntityType) {
        return pLevel.isClientSide() ? null : ($0, $1, $2, blockEntity) -> ((TickableBlockEntity) blockEntity).tick();
    }

    @Override
    public RenderShape getRenderShape(BlockState pState) {
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }

    @Override
    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return SHAPE;
    }

    public static VoxelShape makeShape() {
        VoxelShape shape = Shapes.empty();
        shape = Shapes.join(shape, Shapes.box(0, 0, 0, 4, 0.5, 4), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(1.625, 0.5, 1.625, 2.375, 0.625, 2.375), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(1.5625, 0.625, 1.5625, 2.4375, 0.75, 2.4375), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(1.5, 0.75, 1.5, 2.5, 3.75, 2.5), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(1.5625, 3.75, 1.5625, 2.4375, 3.875, 2.4375), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(1.625, 3.875, 1.625, 2.375, 4, 2.375), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(1.25, 2, 2.625, 2.75, 2.25, 2.75), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(1.25, 2, 1.25, 2.75, 2.25, 1.375), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(2.625, 2, 1.375, 2.75, 2.25, 2.625), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(1.25, 2, 1.375, 1.375, 2.25, 2.625), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(1.9375, 2, 1.375, 2.0625, 2.25, 1.5), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(1.9375, 2, 2.5, 2.0625, 2.25, 2.625), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(1.375, 2, 1.9375, 1.5, 2.25, 2.0625), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(2.5, 2, 1.9375, 2.625, 2.25, 2.0625), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(1.25, 0.5, 2.625, 1.375, 2, 2.75), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(1.25, 0.5, 1.25, 1.375, 2, 1.375), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(2.625, 0.5, 2.625, 2.75, 2, 2.75), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(2.625, 0.5, 1.25, 2.75, 2, 1.375), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(2.5, 2.25, 1.875, 4, 2.5, 2.125), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0, 0.75, 1.875, 1.5, 1, 2.125), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0, 1.75, 1.875, 1.5, 2, 2.125), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0, 3.5, 1.875, 1.5, 3.75, 2.125), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.5625, 0.625, 1.875, 0.6875, 0.75, 2.125), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.5625, 1, 1.875, 0.6875, 1.125, 2.125), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.5625, 1.625, 1.875, 0.6875, 1.75, 2.125), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.5625, 2, 1.875, 0.6875, 2.125, 2.125), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.5625, 3.375, 1.875, 0.6875, 3.5, 2.125), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.5625, 3.75, 1.875, 0.6875, 3.875, 2.125), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(3.3125, 2.125, 1.875, 3.4375, 2.25, 2.125), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(3.3125, 2.5, 1.875, 3.4375, 2.625, 2.125), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.5625, 0.5, 1.75, 0.6875, 3.875, 1.875), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.5625, 0.5, 2.125, 0.6875, 3.875, 2.25), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(3.3125, 0.5, 1.75, 3.4375, 2.625, 1.875), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(3.3125, 0.5, 2.125, 3.4375, 2.625, 2.25), BooleanOp.OR);

        return shape;
    }
}
