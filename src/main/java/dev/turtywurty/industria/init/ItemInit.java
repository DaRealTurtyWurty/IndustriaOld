package dev.turtywurty.industria.init;

import dev.turtywurty.industria.Industria;
import dev.turtywurty.industria.items.BasicResearchItem;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public class ItemInit {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, Industria.MODID);

    public static final RegistryObject<BlockItem> CRUSHER = registerBlockItem("crusher", BlockInit.CRUSHER);
    public static final RegistryObject<BlockItem> BIOMASS_GENERATOR = registerBlockItem("biomass_generator",
            BlockInit.BIOMASS_GENERATOR);
    public static final RegistryObject<BlockItem> RESEARCHER = registerBlockItem("researcher", BlockInit.RESEARCHER);

    public static final RegistryObject<BasicResearchItem> BIOMASS = registerBasicResearchItem("biomass",
            "You created biomass!",
            "Biomass is an organic matter that comes from plants and animals. With combustion, it can be used to generate energy.",
            Rarity.COMMON, () -> ItemInit.BIOMASS.get().getDefaultInstance());

    public static final RegistryObject<BlockItem> PROJECTOR = registerBlockItem("projector", BlockInit.PROJECTOR);

    public static final RegistryObject<BlockItem> TREE_TAP = registerBlockItem("tree_tap", BlockInit.TREE_TAP);

    public static <T extends Item> RegistryObject<T> registerItem(String name, Supplier<T> item) {
        return ITEMS.register(name, item);
    }

    private static RegistryObject<Item> registerItem(String name, Item.Properties properties) {
        return registerItem(name, () -> new Item(properties.tab(Industria.TAB)));
    }

    private static RegistryObject<Item> registerItem(String name) {
        return registerItem(name, properties());
    }

    private static Item.Properties properties() {
        return new Item.Properties().tab(Industria.TAB);
    }

    public static RegistryObject<BlockItem> registerBlockItem(String name, RegistryObject<? extends Block> block) {
        return registerItem(name, () -> new BlockItem(block.get(), properties()));
    }

    private static RegistryObject<BasicResearchItem> registerBasicResearchItem(String name, Item.Properties properties, String title, String description, Rarity rarity, Supplier<ItemStack> icon) {
        return registerItem(name, () -> new BasicResearchItem(properties, title, description, rarity, icon));
    }

    private static RegistryObject<BasicResearchItem> registerBasicResearchItem(String name, Item.Properties properties, String title, String description, Rarity rarity, ResourceLocation icon) {
        return registerItem(name, () -> new BasicResearchItem(properties, title, description, rarity, icon));
    }

    private static RegistryObject<BasicResearchItem> registerBasicResearchItem(String name, String title, String description, Rarity rarity, Supplier<ItemStack> icon) {
        return registerItem(name, () -> new BasicResearchItem(properties(), title, description, rarity, icon));
    }

    private static RegistryObject<BasicResearchItem> registerBasicResearchItem(String name, String title, String description, Rarity rarity, ResourceLocation icon) {
        return registerItem(name, () -> new BasicResearchItem(properties(), title, description, rarity, icon));
    }
}
