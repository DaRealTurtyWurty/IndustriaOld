package dev.turtywurty.industria.init;

import dev.turtywurty.industria.Industria;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public class TagInit {
    public static class Blocks {
        public static final TagKey<Block> TREE_SAP_SURVIVES = createBlockTag("tree_sap_survives");

        public static void init() {
        }
    }

    public static class Items {
        public static void init() {
        }
    }

    private static TagKey<Block> createBlockTag(String name) {
        return BlockTags.create(new ResourceLocation(Industria.MODID, name));
    }

    private static TagKey<Item> createItemTag(String name) {
        return ItemTags.create(new ResourceLocation(Industria.MODID, name));
    }
}