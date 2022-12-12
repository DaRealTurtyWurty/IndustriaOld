package dev.turtywurty.industria.init;

import dev.turtywurty.industria.Industria;
import dev.turtywurty.industria.world.tree.trunkplacer.RubberTreeTrunkPlacer;
import net.minecraft.core.Holder;
import net.minecraft.data.worldgen.features.FeatureUtils;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.level.levelgen.feature.featuresize.TwoLayersFeatureSize;
import net.minecraft.world.level.levelgen.feature.foliageplacers.BlobFoliagePlacer;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;

public class ConfiguredFeatureInit {
    public static final Holder<ConfiguredFeature<TreeConfiguration, ?>> RUBBER_TREE = register("rubber_tree",
            Feature.TREE, new TreeConfiguration.TreeConfigurationBuilder(
                    BlockStateProvider.simple(WoodSetInit.RUBBER_WOOD_SET.log.get()),
                    new RubberTreeTrunkPlacer(15, 2, 1, UniformInt.of(10, 13), UniformInt.of(3, 5)),
                    BlockStateProvider.simple(WoodSetInit.RUBBER_WOOD_SET.leaves.get()),
                    new BlobFoliagePlacer(UniformInt.of(2, 4), ConstantInt.of(2), 4),
                    new TwoLayersFeatureSize(1, 0, 1)).ignoreVines().build());

    private static <FC extends FeatureConfiguration, F extends Feature<FC>> Holder<ConfiguredFeature<FC, ?>> register(String name, F feature, FC featureConfiguration) {
        return FeatureUtils.register(Industria.MODID + ":" + name, feature, featureConfiguration);
    }
}
