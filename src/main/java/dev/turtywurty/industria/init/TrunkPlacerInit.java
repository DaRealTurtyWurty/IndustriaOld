package dev.turtywurty.industria.init;

import com.mojang.serialization.Codec;
import dev.turtywurty.industria.Industria;
import dev.turtywurty.industria.world.tree.trunkplacer.RubberTreeTrunkPlacer;
import net.minecraft.core.Registry;
import net.minecraft.world.level.levelgen.feature.trunkplacers.TrunkPlacer;
import net.minecraft.world.level.levelgen.feature.trunkplacers.TrunkPlacerType;

public class TrunkPlacerInit {
    public static final TrunkPlacerType<RubberTreeTrunkPlacer> RUBBER_TREE_TRUNK_PLACER = register(
            "rubber_tree_trunk_placer", RubberTreeTrunkPlacer.CODEC);

    public static <T extends TrunkPlacer> TrunkPlacerType<T> register(String name, Codec<T> codec) {
        return Registry.register(Registry.TRUNK_PLACER_TYPES, Industria.MODID + ":" + name,
                new TrunkPlacerType<>(codec));
    }
}
