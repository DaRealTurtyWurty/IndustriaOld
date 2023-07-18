package dev.turtywurty.industria.init;

import dev.turtywurty.industria.Industria;
import dev.turtywurty.industria.world.tree.trunkplacer.RubberTreeTrunkPlacer;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.feature.trunkplacers.TrunkPlacerType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class TrunkPlacerInit {
    public static final DeferredRegister<TrunkPlacerType<?>> TRUNK_PLACER_TYPES = DeferredRegister
            .create(Registries.TRUNK_PLACER_TYPE, Industria.MODID);

    public static final RegistryObject<TrunkPlacerType<RubberTreeTrunkPlacer>> RUBBER_TREE_TRUNK_PLACER = TRUNK_PLACER_TYPES
            .register("rubber_tree", () -> new TrunkPlacerType<>(RubberTreeTrunkPlacer.CODEC));
}
