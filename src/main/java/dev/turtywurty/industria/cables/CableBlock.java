package dev.turtywurty.industria.cables;

import dev.turtywurty.turtylib.common.blockentity.TickableBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.PipeBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class CableBlock extends PipeBlock implements EntityBlock {
    public CableBlock() {
        super(0.3125F, Properties.of(Material.HEAVY_METAL));
        registerDefaultState(this.stateDefinition.any().setValue(NORTH, Boolean.FALSE).setValue(EAST, Boolean.FALSE)
                .setValue(SOUTH, Boolean.FALSE).setValue(WEST, Boolean.FALSE).setValue(UP, Boolean.FALSE)
                .setValue(DOWN, Boolean.FALSE));
    }

    protected boolean canConnectTo(BlockGetter level, BlockPos pos, Direction facing) {
        BlockEntity blockEntity = level.getBlockEntity(pos.relative(facing));
        return blockEntity != null && blockEntity.getCapability(ForgeCapabilities.ENERGY, facing.getOpposite())
                .isPresent();
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> pBlockEntityType) {
        return pLevel.isClientSide() ? null : ($0, $1, $2, blockEntity) -> ((TickableBlockEntity) blockEntity).tick();
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext pContext) {
        Level level = pContext.getLevel();
        BlockPos pos = pContext.getClickedPos();

        return this.defaultBlockState().setValue(DOWN, canConnectTo(level, pos, Direction.DOWN))
                .setValue(UP, canConnectTo(level, pos, Direction.UP))
                .setValue(NORTH, canConnectTo(level, pos, Direction.NORTH))
                .setValue(SOUTH, canConnectTo(level, pos, Direction.SOUTH))
                .setValue(EAST, canConnectTo(level, pos, Direction.EAST))
                .setValue(WEST, canConnectTo(level, pos, Direction.WEST));
    }

    @Override
    public BlockState updateShape(BlockState pState, Direction pDirection, BlockState pNeighborState, LevelAccessor pLevel, BlockPos pCurrentPos, BlockPos pNeighborPos) {
        return pState.setValue(PROPERTY_BY_DIRECTION.get(pDirection), canConnectTo(pLevel, pCurrentPos, pDirection));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        super.createBlockStateDefinition(pBuilder);
        pBuilder.add(NORTH, EAST, SOUTH, WEST, UP, DOWN);
    }

    @Override
    public boolean isPathfindable(BlockState pState, BlockGetter pLevel, BlockPos pPos, PathComputationType pType) {
        return false;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return Initialization.CABLE_BLOCK_ENTITY.get().create(pPos, pState);
    }

    @Override
    public void onRemove(BlockState pState, Level pLevel, BlockPos pPos, BlockState pNewState, boolean pIsMoving) {
        if (!pLevel.isClientSide() && pNewState.getBlock() != this) {
            EnergyGraph network = getNetwork(pLevel, pPos);
            if (network != null) {
                List<EnergyGraph> subNetworks = network.splitNetwork(pLevel, pPos);
                //updateConnections(pLevel, network);

                for (EnergyGraph subNetwork : subNetworks) {
                    //updateConnections(pLevel, subNetwork);
                    updateEndpoints(pLevel, subNetwork);
                }
            }
        }

        super.onRemove(pState, pLevel, pPos, pNewState, pIsMoving);
    }

    @Override
    public void onPlace(BlockState pState, Level pLevel, BlockPos pPos, BlockState pOldState, boolean pIsMoving) {
        if (!pLevel.isClientSide()) {
            EnergyGraph network = getNetwork(pLevel, pPos);
            if (network == null) {
                network = new EnergyGraph(pPos);
            }

            for (Direction direction : Direction.values()) {
                BlockPos neighborPos = pPos.relative(direction);
                BlockState neighborState = pLevel.getBlockState(neighborPos);
                if (neighborState.getBlock() instanceof CableBlock) {
                    network.addEdge(pPos, neighborPos);
                }
            }

            updateConnections(pLevel, network);
            updateEndpoints(pLevel, network);
        }

        super.onPlace(pState, pLevel, pPos, pOldState, pIsMoving);
    }

    private EnergyGraph getNetwork(BlockGetter level, BlockPos pos) {
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (blockEntity instanceof CableBlockEntity cable) {
            return cable.getNetwork();
        }

        return null;
    }

    private static void updateConnections(LevelReader level, EnergyGraph network) {
        for (BlockPos pos : network.getGraph().keySet()) {
            if (!(level.getBlockEntity(pos) instanceof CableBlockEntity cableEntity)) continue;

            EnergyGraph graph = cableEntity.getNetwork();
            graph.insertEdges(network);
            cableEntity.update();
        }
    }

    private static void updateEndpoints(LevelReader level, EnergyGraph network) {
        List<BlockPos> endpoints = new ArrayList<>();
        for (BlockPos pos : network.getGraph().keySet()) {
            Set<BlockPos> neighbors = network.getGraph().get(pos);
            if (neighbors.size() == 1) {
                endpoints.add(pos);
            }
        }

        if (endpoints.size() == 1) {
            BlockPos endpoint = endpoints.get(0);
            CableBlockEntity endpointEntity = (CableBlockEntity) level.getBlockEntity(endpoint);
            if (endpointEntity != null) {
                endpointEntity.setEndpoint(true);
            }
        } else if (endpoints.size() > 1) {
            for (BlockPos endpoint : endpoints) {
                CableBlockEntity endpointEntity = (CableBlockEntity) level.getBlockEntity(endpoint);
                if (endpointEntity != null) {
                    endpointEntity.setEndpoint(false);
                }
            }
        }
    }

    @Override
    public RenderShape getRenderShape(BlockState pState) {
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }
}