package dev.turtywurty.industria.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.common.IForgeShearable;

public class RubberLeavesBlock extends Block implements SimpleWaterloggedBlock, IForgeShearable {
    public static final int DECAY_DISTANCE = 13;
    public static final IntegerProperty DISTANCE = IntegerProperty.create("distance", 1, DECAY_DISTANCE);
    public static final BooleanProperty PERSISTENT = BlockStateProperties.PERSISTENT;
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    private static final int TICK_DELAY = 1;

    public RubberLeavesBlock() {
        super(BlockBehaviour.Properties.copy(Blocks.OAK_LEAVES));
        registerDefaultState(this.stateDefinition.any().setValue(DISTANCE, Integer.valueOf(13))
                .setValue(PERSISTENT, Boolean.valueOf(false)).setValue(WATERLOGGED, Boolean.valueOf(false)));
    }

    @Override
    public VoxelShape getBlockSupportShape(BlockState pState, BlockGetter pReader, BlockPos pPos) {
        return Shapes.empty();
    }

    @Override
    public int getLightBlock(BlockState pState, BlockGetter pLevel, BlockPos pPos) {
        return 1;
    }

    @Override
    public FluidState getFluidState(BlockState pState) {
        return pState.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(pState);
    }

    @Override
    public void animateTick(BlockState pState, Level pLevel, BlockPos pPos, RandomSource pRandom) {
        if (pLevel.isRainingAt(pPos.above()) && pRandom.nextInt(15) == 1) {
            BlockPos blockpos = pPos.below();
            BlockState blockstate = pLevel.getBlockState(blockpos);
            if (!blockstate.canOcclude() || !blockstate.isFaceSturdy(pLevel, blockpos, Direction.UP)) {
                double d0 = (double) pPos.getX() + pRandom.nextDouble();
                double d1 = (double) pPos.getY() - 0.05;
                double d2 = (double) pPos.getZ() + pRandom.nextDouble();
                pLevel.addParticle(ParticleTypes.DRIPPING_WATER, d0, d1, d2, 0.0, 0.0, 0.0);
            }
        }
    }

    @Override
    public boolean isRandomlyTicking(BlockState pState) {
        return pState.getValue(DISTANCE) == DECAY_DISTANCE && !pState.getValue(PERSISTENT);
    }

    @Override
    public void randomTick(BlockState pState, ServerLevel pLevel, BlockPos pPos, RandomSource pRandom) {
        if (this.decaying(pState)) {
            dropResources(pState, pLevel, pPos);
            pLevel.removeBlock(pPos, false);
        }
    }

    protected boolean decaying(BlockState state) {
        return !state.getValue(PERSISTENT) && state.getValue(DISTANCE) == DECAY_DISTANCE;
    }

    @Override
    public void tick(BlockState pState, ServerLevel pLevel, BlockPos pPos, RandomSource pRandom) {
        pLevel.setBlockAndUpdate(pPos, updateDistance(pState, pLevel, pPos));
    }

    @Override
    public BlockState updateShape(BlockState pState, Direction pFacing, BlockState pFacingState, LevelAccessor pLevel, BlockPos pCurrentPos, BlockPos pFacingPos) {
        if (pState.getValue(WATERLOGGED)) {
            pLevel.scheduleTick(pCurrentPos, Fluids.WATER, Fluids.WATER.getTickDelay(pLevel));
        }

        int i = getDistanceAt(pFacingState) + 1;
        if (i != 1 || pState.getValue(DISTANCE) != i) {
            pLevel.scheduleTick(pCurrentPos, this, TICK_DELAY);
        }

        return pState;
    }

    private static BlockState updateDistance(BlockState pState, LevelAccessor pLevel, BlockPos pPos) {
        int i = DECAY_DISTANCE;
        BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();

        for (Direction direction : Direction.values()) {
            blockpos$mutableblockpos.setWithOffset(pPos, direction);
            i = Math.min(i, getDistanceAt(pLevel.getBlockState(blockpos$mutableblockpos)) + 1);
            if (i == 1) {
                break;
            }
        }

        return pState.setValue(DISTANCE, Integer.valueOf(i));
    }

    private static int getDistanceAt(BlockState pNeighbor) {
        if (pNeighbor.is(BlockTags.LOGS)) {
            return 0;
        } else {
            return (pNeighbor.getBlock() instanceof LeavesBlock || pNeighbor.getBlock() instanceof RubberLeavesBlock) ? pNeighbor.getValue(
                    DISTANCE) : DECAY_DISTANCE;
        }
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        pBuilder.add(DISTANCE, PERSISTENT, WATERLOGGED);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext pContext) {
        FluidState fluidstate = pContext.getLevel().getFluidState(pContext.getClickedPos());
        BlockState blockstate = this.defaultBlockState().setValue(PERSISTENT, Boolean.TRUE)
                .setValue(WATERLOGGED, Boolean.valueOf(fluidstate.getType() == Fluids.WATER));
        return updateDistance(blockstate, pContext.getLevel(), pContext.getClickedPos());
    }
}