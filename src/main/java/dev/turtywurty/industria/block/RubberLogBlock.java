package dev.turtywurty.industria.block;

import dev.turtywurty.industria.init.WoodSetInit;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.common.ToolAction;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.ThreadLocalRandom;

public class RubberLogBlock extends RotatedPillarBlock {
    public static final BooleanProperty STRIPPED = BooleanProperty.create("stripped");
    public static final IntegerProperty LATEX_LEVEL = IntegerProperty.create("latex_level", 0, 5);
    public static final BooleanProperty TAPPED = BooleanProperty.create("tapped");

    public RubberLogBlock() {
        super(Properties.copy(Blocks.OAK_LOG).randomTicks());
        registerDefaultState(
                this.stateDefinition.any().setValue(STRIPPED, false).setValue(LATEX_LEVEL, 0).setValue(TAPPED, false));
    }

    @Override
    public @Nullable BlockState getToolModifiedState(BlockState state, UseOnContext context, ToolAction toolAction, boolean simulate) {
        ItemStack handItem = context.getItemInHand();
        if (!(handItem.getItem() instanceof AxeItem)) return null;

        if (context.getPlayer().isShiftKeyDown() && canStrip(state)) {
            return state.setValue(STRIPPED, true);
        } else {
            return WoodSetInit.RUBBER_WOOD_SET.strippedLog.get().defaultBlockState();
        }
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext pContext) {
        return super.getStateForPlacement(pContext).setValue(LATEX_LEVEL, ThreadLocalRandom.current().nextInt(1, 6));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        super.createBlockStateDefinition(pBuilder);
        pBuilder.add(STRIPPED, LATEX_LEVEL, TAPPED);
    }

    @Override
    public void randomTick(BlockState pState, ServerLevel pLevel, BlockPos pPos, RandomSource pRandom) {
        super.randomTick(pState, pLevel, pPos, pRandom);
        if (pRandom.nextInt(100) == 0) {
            pLevel.setBlockAndUpdate(pPos, pState.setValue(LATEX_LEVEL, pState.getValue(LATEX_LEVEL) - 1));
        }
    }

    @Override
    public boolean isRandomlyTicking(BlockState pState) {
        return canDrain(pState);
    }

    public static boolean canDrain(BlockState pState) {
        return pState.getValue(STRIPPED) && pState.getValue(LATEX_LEVEL) > 0 && !pState.getValue(TAPPED);
    }

    public static boolean canStrip(BlockState pState) {
        return !pState.getValue(STRIPPED) && pState.getValue(LATEX_LEVEL) > 0 && !pState.getValue(TAPPED);
    }

    public static boolean canTap(BlockState pState) {
        return pState.getValue(STRIPPED) && pState.getValue(LATEX_LEVEL) > 0 && pState.getValue(TAPPED);
    }
}
