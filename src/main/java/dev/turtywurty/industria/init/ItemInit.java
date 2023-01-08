package dev.turtywurty.industria.init;

import dev.turtywurty.industria.Industria;
import dev.turtywurty.industria.items.BasicResearchItem;
import dev.turtywurty.industria.items.RopeItem;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
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

    public static final RegistryObject<RopeItem> ROPE = registerItem("rope", RopeItem::new);

    public static final RegistryObject<BlockItem> AGITATOR = registerBlockItem("agitator", BlockInit.AGITATOR);

    public static final RegistryObject<Item> DRIED_LATEX_BUCKET = registerItem("dried_latex_bucket");
    public static final RegistryObject<Item> COAGULATED_LATEX_BUCKET = registerItem("coagulated_latex_bucket");
    public static final RegistryObject<Item> AMMONIUM_HYDROXIDE = registerItem("ammonium_hydroxide");
    public static final RegistryObject<Item> CLEAN_LATEX_BUCKET = registerItem("clean_latex_bucket");
    public static final RegistryObject<Item> UNTREATED_RUBBER = registerItem("untreated_rubber");
    public static final RegistryObject<Item> SULFUR = registerItem("sulfur");
    public static final RegistryObject<Item> RUBBER = registerItem("rubber");

    public static final RegistryObject<Item> FORMIC_ACID = registerItem("formic_acid");
    public static final RegistryObject<Item> METHANOL = registerItem("methanol");
    public static final RegistryObject<Item> DILUTED_FORMIC_ACID = registerItem("diluted_formic_acid");
    public static final RegistryObject<Item> IMPURE_FORMIC_ACID = registerItem("impure_formic_acid");

    public static final RegistryObject<Item> SODIUM_BISULFITE = registerItem("sodium_bisulfite");
    public static final RegistryObject<Item> SODIUM_CARBONATE = registerItem("sodium_carbonate");
    public static final RegistryObject<Item> SULFURIC_ACID = registerItem("sulfuric_acid");

    // TODO: Make this a fluid
    public static final RegistryObject<Item> DILUTED_SODIUM_BISULFITE = registerItem("diluted_sodium_bisulfite");
    public static final RegistryObject<Item> DIRTY_SODIUM_BISULFITE = registerItem("dirty_sodium_bisulfite");
    public static final RegistryObject<Item> CLEAN_SODIUM_BISULFITE = registerItem("clean_sodium_bisulfite");

    public static final RegistryObject<Item> SODIUM_CHLORIDE = registerItem("sodium_chloride");
    public static final RegistryObject<Item> AMMONIA = registerItem("ammonia");
    public static final RegistryObject<Item> CALCIUM_CHLORIDE = registerItem("calcium_chloride");
    public static final RegistryObject<Item> DIRTY_SODIUM_CARBONATE = registerItem("dirty_sodium_chloride");
    public static final RegistryObject<Item> CLEANED_SODIUM_CARBONATE = registerItem("cleaned_sodium_chloride");

    public static final RegistryObject<BlockItem> TRONA_ORE = registerBlockItem("trona_ore", BlockInit.TRONA_ORE);
    public static final RegistryObject<BlockItem> NAHCOLITE_ORE = registerBlockItem("nahcolite_ore",
            BlockInit.NAHCOLITE_ORE);
    public static final RegistryObject<Item> CRUSHED_TRONA = registerItem("crushed_trona");
    public static final RegistryObject<Item> CRUSHED_NAHCOLITE = registerItem("crushed_nahcolite");
    public static final RegistryObject<Item> TRONA_INGOT = registerItem("trona_ingot");
    public static final RegistryObject<Item> NAHCOLITE_INGOT = registerItem("nahcolite_ingot");
    public static final RegistryObject<Item> TRONA_NUGGET = registerItem("trona_nugget");
    public static final RegistryObject<Item> NAHCOLITE_NUGGET = registerItem("nahcolite_nugget");
    public static final RegistryObject<BlockItem> TRONA_BLOCK = registerBlockItem("trona_block", BlockInit.TRONA_BLOCK);
    public static final RegistryObject<BlockItem> NAHCOLITE_BLOCK = registerBlockItem("nahcolite_block",
            BlockInit.NAHCOLITE_BLOCK);
    public static final RegistryObject<Item> CRYSTALLIZED_TRONA = registerItem("crystallized_trona");
    public static final RegistryObject<Item> CRYSTALLIZED_NAHCOLITE = registerItem("crystallized_nahcolite");

    public static final RegistryObject<Item> ACTIVATED_CARBON = registerItem("activated_carbon");
    public static final RegistryObject<Item> ZEOLITE = registerItem("zeolite");
    public static final RegistryObject<Item> MONOETHANOLAMINE = registerItem("monoethanolamine");

    public static final RegistryObject<Item> PHOSPHORIC_ACID = registerItem("phosphoric_acid");
    public static final RegistryObject<Item> ZINC_CHLORIDE = registerItem("zinc_chloride");

    public static final RegistryObject<BlockItem> PHOSPHATE_ORE = registerBlockItem("phosphate_ore",
            BlockInit.PHOSPHATE_ORE);
    public static final RegistryObject<Item> CRUSHED_PHOSPHATE = registerItem("crushed_phosphate");
    public static final RegistryObject<Item> PHOSPHORUS = registerItem("phosphorus");
    public static final RegistryObject<Item> IMPURE_PHOSPHORIC_ACID = registerItem("impure_phosphoric_acid");

    public static final RegistryObject<BlockItem> ZINC_ORE = registerBlockItem("zinc_ore", BlockInit.ZINC_ORE);
    public static final RegistryObject<Item> CRUSHED_ZINC = registerItem("crushed_zinc");
    public static final RegistryObject<Item> ZINC_INGOT = registerItem("zinc_ingot");
    public static final RegistryObject<Item> ZINC_NUGGET = registerItem("zinc_nugget");
    public static final RegistryObject<BlockItem> ZINC_BLOCK = registerBlockItem("zinc_block", BlockInit.ZINC_BLOCK);
    public static final RegistryObject<Item> HYDROCHLORIC_ACID = registerItem("hydrochloric_acid");
    public static final RegistryObject<Item> IMPURE_ZINC_CHLORIDE = registerItem("impure_zinc_chloride");
    public static final RegistryObject<Item> CRYSTALLIZED_ZINC_CHLORIDE = registerItem("crystallized_zinc_chloride");

    public static final RegistryObject<Item> CLINOPTILOLITE = registerItem("clinoptilolite");
    public static final RegistryObject<Item> CHABAZITE = registerItem("chabazite");
    public static final RegistryObject<Item> MORDENITE = registerItem("mordenite");

    public static final RegistryObject<Item> IMPURE_MONOETHANOLAMINE = registerItem("impure_monoethanolamine");

    public static final RegistryObject<BlockItem> SILVER_ORE = registerBlockItem("silver_ore", BlockInit.SILVER_ORE);
    public static final RegistryObject<Item> CRUSHED_SILVER = registerItem("crushed_silver");
    public static final RegistryObject<Item> SILVER_INGOT = registerItem("silver_ingot");
    public static final RegistryObject<Item> SILVER_NUGGET = registerItem("silver_nugget");
    public static final RegistryObject<BlockItem> SILVER_BLOCK = registerBlockItem("silver_block",
            BlockInit.SILVER_BLOCK);

    public static final RegistryObject<BlockItem> PLATINUM_ORE = registerBlockItem("platinum_ore",
            BlockInit.PLATINUM_ORE);
    public static final RegistryObject<Item> CRUSHED_PLATINUM = registerItem("crushed_platinum");
    public static final RegistryObject<Item> PLATINUM_INGOT = registerItem("platinum_ingot");
    public static final RegistryObject<Item> PLATINUM_NUGGET = registerItem("platinum_nugget");
    public static final RegistryObject<BlockItem> PLATINUM_BLOCK = registerBlockItem("platinum_block",
            BlockInit.PLATINUM_BLOCK);

    public static final RegistryObject<Item> VANADIUM_PENTOXIDE = registerItem("vanadium_pentoxide");
    public static final RegistryObject<Item> SULFUR_TRIOXIDE = registerItem("sulfur_trioxide");
    public static final RegistryObject<BlockItem> PYRITE_ORE = registerBlockItem("pyrite_ore", BlockInit.PYRITE_ORE);
    public static final RegistryObject<Item> CRUSHED_PYRITE = registerItem("crushed_pyrite");
    public static final RegistryObject<BlockItem> VANADINITE_ORE = registerBlockItem("vanadinite_ore",
            BlockInit.VANADINITE_ORE);
    public static final RegistryObject<Item> CRUSHED_VANADINITE = registerItem("crushed_vanadinite");
    public static final RegistryObject<BlockItem> CARNOTITE_ORE = registerBlockItem("carnotite_ore",
            BlockInit.CARNOTITE_ORE);
    public static final RegistryObject<Item> CRUSHED_CARNOTITE = registerItem("crushed_carnotite");
    public static final RegistryObject<BlockItem> PATRONITE_ORE = registerBlockItem("patronite_ore",
            BlockInit.PATRONITE_ORE);
    public static final RegistryObject<Item> CRUSHED_PATRONITE = registerItem("crushed_patronite");
    public static final RegistryObject<Item> IMPURE_VANADIUM_PENTOXIDE = registerItem("impure_vanadium_pentoxide");


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
