package dev.turtywurty.industria.block;

import dev.turtywurty.industria.blockentity.BiomassGeneratorBlockEntity;
import dev.turtywurty.industria.init.BlockEntityInit;
import dev.turtywurty.industria.menu.BiomassGeneratorMenu;
import dev.turtywurty.turtylib.common.blockentity.TickableBlockEntity;
import dev.turtywurty.turtylib.core.util.MathUtils;
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
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.Nullable;

import static dev.turtywurty.turtylib.core.util.MathUtils.nextFloat;

public class BiomassGeneratorBlock extends Block implements EntityBlock {
    private static final VoxelShape SHAPE = makeShape();

    public BiomassGeneratorBlock() {
        super(Properties.of(Material.METAL).strength(3.5f, 3.5f).sound(SoundType.METAL).noOcclusion());
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return BlockEntityInit.BIOMASS_GENERATOR.get().create(pPos, pState);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return level.isClientSide() ? null : ($0, $1, $2, blockEntity) -> ((TickableBlockEntity) blockEntity).tick();
    }

    @Override
    public InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHit) {
        if (!pLevel.isClientSide() && pLevel.getBlockEntity(pPos) instanceof BiomassGeneratorBlockEntity blockEntity) {
            SimpleMenuProvider provider = new SimpleMenuProvider(BiomassGeneratorMenu.getServerMenu(blockEntity, pPos),
                    BiomassGeneratorBlockEntity.TITLE);
            NetworkHooks.openScreen((ServerPlayer) pPlayer, provider, pPos);
        }

        return InteractionResult.sidedSuccess(pLevel.isClientSide());
    }

    @Override
    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return SHAPE;
    }

    @Override
    public RenderShape getRenderShape(BlockState pState) {
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }

    @Override
    public void animateTick(BlockState pState, Level pLevel, BlockPos pPos, RandomSource pRandom) {
        BlockEntity blockEntity = pLevel.getBlockEntity(pPos);
        if (blockEntity instanceof BiomassGeneratorBlockEntity generator) {
            if (generator.getRunningTicks() > 0 && generator.isPistonExtending()) {
                double x = pPos.getX() + 0.5;
                double y = pPos.getY() + 0.25;
                double z = pPos.getZ() + 0.5;

                for (int i = 0; i < 3; i++) {
                    pLevel.addParticle(ParticleTypes.SMOKE, x, y, z, -0.1D, 0, 0);
                }

                if (pRandom.nextInt(10) == 0) {
                    pLevel.addParticle(ParticleTypes.FLAME, x, y + 0.1D, z, -0.01D, 0.001D, 0);
                }
            }

            ItemStack stack = generator.getInventory().getCapabilityInstance(Direction.UP).getStackInSlot(0);
            if (generator.getRunningTicks() > 0 && !stack.isEmpty()) {
                double x = pPos.getX() + nextFloat(pRandom, 0.25f, 0.75f);
                double z = pPos.getZ() + nextFloat(pRandom, 0.25f, 0.75f);

                double height = (pRandom.nextBoolean() ? pRandom.nextBoolean() ? 0.751D : 0.876D : 1.001D);
                double y = pPos.getY() + height;

                double ySpeed = MathUtils.mapNumber(height - 0.75D, 0.001D, 0.251D, 0.251D, 0.001D);
                pLevel.addParticle(new ItemParticleOption(ParticleTypes.ITEM, stack), x, y, z, 0, ySpeed, 0);
            }
        }
    }

    private static VoxelShape makeShape() {
        VoxelShape shape = Shapes.empty();
        shape = Shapes.join(shape, Shapes.box(0.0625, 0, 0.0625, 0.9375, 0.0625, 0.9375), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.1875, 0.0625, 0.1875, 0.8125, 1, 0.8125), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.125, 0.1875, 0.3125, 0.1875, 0.5, 0.6875), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.8125, 0.1875, 0.3125, 0.875, 0.5, 0.6875), BooleanOp.OR);

        return shape;
    }
}
