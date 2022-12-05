package dev.turtywurty.industria.init;

import dev.turtywurty.industria.Industria;
import dev.turtywurty.industria.items.BasicResearchItem;
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

    // TODO: Delete all the items below after testing
    public static final RegistryObject<BasicResearchItem> STEEL = registerBasicResearchItem("steel",
            "You created steel!",
            "Steel is an alloy of iron and carbon. It is used to create stronger and more durable items.",
            Rarity.COMMON, () -> ItemInit.STEEL.get().getDefaultInstance());
    public static final RegistryObject<BasicResearchItem> ELECTRICITY = registerBasicResearchItem("electricity",
            "You created electricity!", "Electricity is a form of energy that can be used to power machines.",
            Rarity.COMMON, () -> ItemInit.ELECTRICITY.get().getDefaultInstance());
    public static final RegistryObject<BasicResearchItem> CRUSHING = registerBasicResearchItem("crushing",
            "You created crushing!", "Crushing is a process that can be used to break down items into smaller pieces.",
            Rarity.COMMON, () -> ItemInit.CRUSHING.get().getDefaultInstance());
    public static final RegistryObject<BasicResearchItem> BIOMASS_GENERATION = registerBasicResearchItem(
            "biomass_generation", "You created biomass generation!",
            "Biomass generation is a process that can be used to generate energy from biomass.", Rarity.COMMON,
            () -> ItemInit.BIOMASS_GENERATION.get().getDefaultInstance());
    public static final RegistryObject<BasicResearchItem> RESEARCH = registerBasicResearchItem("research",
            "You created research!", "Research is a process that can be used to unlock new technologies.",
            Rarity.COMMON, () -> ItemInit.RESEARCH.get().getDefaultInstance());

    public static final RegistryObject<BasicResearchItem> STEEL_CRUSHER = registerBasicResearchItem("steel_crusher",
            "You created a steel crusher!", "A steel crusher is a machine that can be used to crush items.",
            Rarity.COMMON, () -> ItemInit.STEEL_CRUSHER.get().getDefaultInstance());

    private static <T extends Item> RegistryObject<T> registerItem(String name, Supplier<T> item) {
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

    private static RegistryObject<BlockItem> registerBlockItem(String name, RegistryObject<? extends Block> block) {
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
