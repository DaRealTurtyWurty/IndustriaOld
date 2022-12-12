package dev.turtywurty.industria.init;

import dev.turtywurty.industria.block.RubberLeavesBlock;
import dev.turtywurty.industria.block.RubberLogBlock;
import dev.turtywurty.industria.init.util.WoodRegistrySet;
import dev.turtywurty.industria.world.tree.grower.RubberTree;

public class WoodSetInit {
    public static final WoodRegistrySet RUBBER_WOOD_SET = new WoodRegistrySet(
            new WoodRegistrySet.Builder("rubber", new RubberTree()).leaves(
                            BlockInit.BLOCKS.register("rubber_leaves", RubberLeavesBlock::new))
                    .log(BlockInit.BLOCKS.register("rubber_log", RubberLogBlock::new)));

    public static void init() {

    }
}
