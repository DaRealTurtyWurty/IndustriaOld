package dev.turtywurty.industria.world.tree.trunkplacer;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.turtywurty.industria.block.RubberLogBlock;
import dev.turtywurty.industria.init.TrunkPlacerInit;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.level.LevelSimulatedReader;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacer;
import net.minecraft.world.level.levelgen.feature.trunkplacers.TrunkPlacer;
import net.minecraft.world.level.levelgen.feature.trunkplacers.TrunkPlacerType;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.BiConsumer;

public class RubberTreeTrunkPlacer extends TrunkPlacer {
    public static final Codec<RubberTreeTrunkPlacer> CODEC = RecordCodecBuilder.create(
            (codec) -> trunkPlacerParts(codec).and(codec.group(IntProvider.POSITIVE_CODEC.fieldOf("branch_start_height")
                                    .forGetter((placer) -> placer.branchStartHeight),
                            IntProvider.NON_NEGATIVE_CODEC.fieldOf("branch_length").forGetter((placer) -> placer.branchLength)))
                    .apply(codec, RubberTreeTrunkPlacer::new));

    private final IntProvider branchStartHeight;
    private final IntProvider branchLength;

    public RubberTreeTrunkPlacer(int baseHeight, int firstRandomHeight, int secondRandomHeight, IntProvider branchStartHeight, IntProvider branchLength) {
        super(baseHeight, firstRandomHeight, secondRandomHeight);
        this.branchStartHeight = branchStartHeight;
        this.branchLength = branchLength;
    }

    @Override
    protected @NotNull TrunkPlacerType<?> type() {
        return TrunkPlacerInit.RUBBER_TREE_TRUNK_PLACER.get();
    }

    @Override
    public List<FoliagePlacer.FoliageAttachment> placeTrunk(LevelSimulatedReader pLevel, BiConsumer<BlockPos, BlockState> pBlockSetter, RandomSource pRandom, int pFreeTreeHeight, BlockPos pPos, TreeConfiguration pConfig) {
        List<FoliagePlacer.FoliageAttachment> list = new ArrayList<>();
        setDirtAt(pLevel, pBlockSetter, pRandom, pPos.below(), pConfig);
        list.add(new FoliagePlacer.FoliageAttachment(pPos.above(pFreeTreeHeight), 0, false));

        int height = this.branchStartHeight.sample(pRandom);
        float branchingPossibility = 0.8F;
        Direction branchingDirection = null;
        for (int yPos = 0; yPos < pFreeTreeHeight; ++yPos) {
            placeLog(pLevel, pBlockSetter, pRandom, pPos.above(yPos), pConfig);

            if (yPos >= height - 1) {
                if (pRandom.nextFloat() < branchingPossibility) {
                    branchingPossibility = branchingPossibility * branchingPossibility;

                    Direction direction;
                    do {
                        direction = Direction.Plane.HORIZONTAL.getRandomDirection(pRandom);
                    } while (direction == branchingDirection);

                    branchingDirection = direction;

                    BlockPos pos = pPos.above(yPos)
                            .offset(branchingDirection.getStepX(), 0, branchingDirection.getStepZ()).mutable();
                    BlockPos last = pos;
                    int xOffset = 0;
                    int zOffset = 0;
                    int length = this.branchLength.sample(pRandom);
                    for (int hPos = 0; hPos < length; ++hPos) {
                        BlockPos offset = pos.offset(xOffset, hPos, zOffset);
                        placeLog(pLevel, pBlockSetter, pRandom, offset, pConfig);

                        if (hPos == 0 || pRandom.nextFloat() < 0.8F) {
                            xOffset += branchingDirection.getStepX();
                            zOffset += branchingDirection.getStepZ();
                        }

                        last = offset;
                    }

                    list.add(new FoliagePlacer.FoliageAttachment(last.above(1), 0, false));
                }
            }
        }

        return list;
    }

    @Override
    protected boolean placeLog(LevelSimulatedReader pLevel, BiConsumer<BlockPos, BlockState> pBlockSetter, RandomSource pRandom, BlockPos pPos, TreeConfiguration pConfig) {
        return placeLog(pLevel, pBlockSetter, pRandom, pPos, pConfig,
                blockState -> blockState.setValue(RubberLogBlock.LATEX_LEVEL,
                        ThreadLocalRandom.current().nextInt(1, 6)));
    }
}
