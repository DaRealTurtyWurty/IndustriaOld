package dev.turtywurty.industria.block;

import dev.turtywurty.industria.blockentity.CrusherBlockEntity;
import dev.turtywurty.industria.init.BlockEntityInit;
import dev.turtywurty.industria.menu.CrusherMenu;
import dev.turtywurty.turtylib.common.blockentity.TickableBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.Nullable;

import static dev.turtywurty.turtylib.core.util.MathUtils.nextFloat;

public class CrusherBlock extends Block implements EntityBlock {
    private static final VoxelShape SHAPE = Shapes.box(0, 0, 0, 1, 0.625, 1);
    public static final AABB PICKUP_AREA = new AABB(0, 0, 0, 1, 0.7, 1);

    public CrusherBlock() {
        super(Properties.copy(Blocks.IRON_BLOCK).noOcclusion());
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return BlockEntityInit.CRUSHER.get().create(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return level.isClientSide() ? null : ($0, $1, $2, blockEntity) -> ((TickableBlockEntity) blockEntity).tick();
    }

    @Override
    public InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHit) {
        if (!pLevel.isClientSide() && pLevel.getBlockEntity(pPos) instanceof CrusherBlockEntity blockEntity) {
            var provider = new SimpleMenuProvider(CrusherMenu.getServerMenu(blockEntity, pPos),
                    CrusherBlockEntity.TITLE);
            NetworkHooks.openScreen((ServerPlayer) pPlayer, provider, pPos);
        }

        return InteractionResult.sidedSuccess(pLevel.isClientSide());
    }

    @Override
    public RenderShape getRenderShape(BlockState pState) {
        return RenderShape.INVISIBLE;
    }

    @Override
    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return SHAPE;
    }

    @Override
    public void animateTick(BlockState pState, Level pLevel, BlockPos pPos, RandomSource pRandom) {
        if (pLevel.getBlockEntity(pPos) instanceof CrusherBlockEntity blockEntity) {
            if (blockEntity.getCurrentRecipeTime() > 0) {
                ItemStack stack = blockEntity.getInventory().getCapabilityInstance(Direction.UP).getStackInSlot(0);
                var particle = new ItemParticleOption(ParticleTypes.ITEM, stack);
                for (int i = 0; i < pRandom.nextInt(2) + 1; i++) {
                    pLevel.addParticle(particle, pPos.getX() + 0.25 + nextFloat(pRandom, -0.1f, 0.1f),
                            pPos.getY() + 0.55 + nextFloat(pRandom, -0.1f, 0.1f),
                            pPos.getZ() + 0.25 + nextFloat(pRandom, -0.1f, 0.1f), 0, 0, 0);
                }

                for (int i = 0; i < pRandom.nextInt(2) + 1; i++) {
                    pLevel.addParticle(particle, pPos.getX() + 0.75 + nextFloat(pRandom, -0.1f, 0.1f),
                            pPos.getY() + 0.55 + nextFloat(pRandom, -0.1f, 0.1f),
                            pPos.getZ() + 0.25 + nextFloat(pRandom, -0.1f, 0.1f), 0, 0, 0);
                }

                for (int i = 0; i < pRandom.nextInt(2) + 1; i++) {
                    pLevel.addParticle(particle, pPos.getX() + 0.25 + nextFloat(pRandom, -0.1f, 0.1f),
                            pPos.getY() + 0.55 + nextFloat(pRandom, -0.1f, 0.1f),
                            pPos.getZ() + 0.75 + nextFloat(pRandom, -0.1f, 0.1f), 0, 0, 0);
                }

                for (int i = 0; i < pRandom.nextInt(2) + 1; i++) {
                    pLevel.addParticle(particle, pPos.getX() + 0.75 + nextFloat(pRandom, -0.1f, 0.1f),
                            pPos.getY() + 0.55 + nextFloat(pRandom, -0.1f, 0.1f),
                            pPos.getZ() + 0.75 + nextFloat(pRandom, -0.1f, 0.1f), 0, 0, 0);
                }
            }
        }
    }
}
