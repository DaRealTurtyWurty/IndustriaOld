package dev.turtywurty.industria.world.tree.grower;

import dev.turtywurty.industria.Industria;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.grower.AbstractTreeGrower;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import org.jetbrains.annotations.Nullable;

public class RubberTree extends AbstractTreeGrower {
    private static final ResourceKey<ConfiguredFeature<?, ?>> RUBBER_TREE = ResourceKey.create(
            Registries.CONFIGURED_FEATURE, new ResourceLocation(Industria.MODID, "rubber_tree"));

    @Nullable
    @Override
    protected ResourceKey<ConfiguredFeature<?, ?>> getConfiguredFeature(RandomSource pRandom, boolean pBees) {
        return RUBBER_TREE;
    }
}