package dev.turtywurty.industria.block;

import dev.turtywurty.industria.init.FluidInit;
import dev.turtywurty.industria.init.TagInit;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;

import javax.annotation.Nullable;

// TODO: Fix block still surviving when log is broken
public class TreeTapBlock extends Block {
    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
    public static final IntegerProperty LATEX_LEVEL = IntegerProperty.create("latex_level", 0, 5);
    public static final BooleanProperty IS_DONE = BooleanProperty.create("is_done");

    public TreeTapBlock() {
        super(Properties.copy(Blocks.ANVIL).randomTicks());
        registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(LATEX_LEVEL, 0)
                .setValue(IS_DONE, false));
    }

    @Override
    public boolean canSurvive(BlockState pState, LevelReader pLevel, BlockPos pPos) {
        BlockState state = pLevel.getBlockState(pPos.relative(pState.getValue(FACING).getOpposite()));
        return state.is(TagInit.Blocks.TREE_SAP_SURVIVES) && RubberLogBlock.canDrain(state);
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext pContext) {
        BlockState state = defaultBlockState();
        Level level = pContext.getLevel();
        BlockPos pos = pContext.getClickedPos();

        for (Direction direction : pContext.getNearestLookingDirections()) {
            if (direction.getAxis().isHorizontal()) {
                Direction opposite = direction.getOpposite();
                state = state.setValue(FACING, opposite);
                if (state.canSurvive(level, pos)) {
                    return state;
                }
            }
        }

        return null;
    }

    @Override
    public void onPlace(BlockState pState, Level pLevel, BlockPos pPos, BlockState pOldState, boolean pIsMoving) {
        super.onPlace(pState, pLevel, pPos, pOldState, pIsMoving);
        if(pOldState.getBlock() != pState.getBlock()) {
            BlockPos logPos = pPos.relative(pState.getValue(FACING).getOpposite());
            BlockState logState = pLevel.getBlockState(logPos);
            pLevel.setBlockAndUpdate(logPos, logState.setValue(RubberLogBlock.TAPPED, true));
        }
    }

    @Override
    public BlockState rotate(BlockState pState, Rotation pRotation) {
        return pState.setValue(FACING, pRotation.rotate(pState.getValue(FACING)));
    }

    @Override
    public BlockState mirror(BlockState pState, Mirror pMirror) {
        return pState.rotate(pMirror.getRotation(pState.getValue(FACING)));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        pBuilder.add(FACING, LATEX_LEVEL, IS_DONE);
    }

    @Override
    public void randomTick(BlockState pState, ServerLevel pLevel, BlockPos pPos, RandomSource pRandom) {
        super.randomTick(pState, pLevel, pPos, pRandom);
        if (pRandom.nextInt(20) == 0) {
            BlockPos pos = pPos.relative(pState.getValue(FACING).getOpposite());
            BlockState state = pLevel.getBlockState(pos);
            if (RubberLogBlock.canTap(state)) {
                BlockState newTapState = pState.setValue(LATEX_LEVEL, pState.getValue(LATEX_LEVEL) + 1);
                pLevel.setBlockAndUpdate(pPos, newTapState);

                BlockState newLogState = state.setValue(RubberLogBlock.LATEX_LEVEL,
                        state.getValue(RubberLogBlock.LATEX_LEVEL) - 1);
                pLevel.setBlockAndUpdate(pos, newLogState);

                if (newLogState.getValue(RubberLogBlock.LATEX_LEVEL) <= 0) {
                    pLevel.setBlockAndUpdate(pPos, newTapState.setValue(IS_DONE, true));
                }
            }
        }
    }

    @Override
    public InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHit) {
        ItemStack stack = pPlayer.getItemInHand(pHand);
        if (!stack.is(Items.BUCKET)) return InteractionResult.PASS;
        if (pState.getValue(LATEX_LEVEL) <= 0) return InteractionResult.PASS;

        if(!pPlayer.isCreative()) {
            stack.shrink(1);
        }

        pLevel.setBlockAndUpdate(pPos, pState.setValue(LATEX_LEVEL, pState.getValue(LATEX_LEVEL) - 1));

        ItemStack toGive = FluidInit.LATEX.bucket.get().getDefaultInstance();
        if (!pPlayer.addItem(toGive)) {
            pPlayer.drop(toGive, false);
        }

        return InteractionResult.CONSUME;
    }

    @Override
    public boolean isRandomlyTicking(BlockState pState) {
        return !pState.getValue(IS_DONE) && pState.getValue(LATEX_LEVEL) < 5;
    }
}
