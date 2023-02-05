package dev.turtywurty.industria.init;

import dev.turtywurty.industria.Industria;
import dev.turtywurty.industria.items.BasicResearchItem;
import dev.turtywurty.industria.items.MachineUpgradeItem;
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

    public static final RegistryObject<BlockItem> TREE_DECAPITATOR = registerBlockItem("tree_decapitator",
            BlockInit.TREE_DECAPITATOR);

    public static final RegistryObject<MachineUpgradeItem> COLLECTION_UPGRADE = registerItem("collection_upgrade",
            () -> new MachineUpgradeItem(MachineUpgradeInit.COLLECTION));

    public static final RegistryObject<MachineUpgradeItem> SPEED_1_UPGRADE = registerItem("speed_1_upgrade",
            () -> new MachineUpgradeItem(MachineUpgradeInit.SPEED_1));

    public static final RegistryObject<MachineUpgradeItem> SPEED_2_UPGRADE = registerItem("speed_2_upgrade",
            () -> new MachineUpgradeItem(MachineUpgradeInit.SPEED_2));

    public static final RegistryObject<MachineUpgradeItem> SPEED_3_UPGRADE = registerItem("speed_3_upgrade",
            () -> new MachineUpgradeItem(MachineUpgradeInit.SPEED_3));

    public static final RegistryObject<MachineUpgradeItem> SPEED_ULTIMATE_UPGRADE = registerItem(
            "speed_ultimate_upgrade", () -> new MachineUpgradeItem(MachineUpgradeInit.SPEED_ULTIMATE));

    public static final RegistryObject<MachineUpgradeItem> RANGE_1_UPGRADE = registerItem("range_1_upgrade",
            () -> new MachineUpgradeItem(MachineUpgradeInit.RANGE_1));

    public static final RegistryObject<MachineUpgradeItem> RANGE_2_UPGRADE = registerItem("range_2_upgrade",
            () -> new MachineUpgradeItem(MachineUpgradeInit.RANGE_2));

    public static final RegistryObject<MachineUpgradeItem> RANGE_3_UPGRADE = registerItem("range_3_upgrade",
            () -> new MachineUpgradeItem(MachineUpgradeInit.RANGE_3));

    public static final RegistryObject<MachineUpgradeItem> RANGE_ULTIMATE_UPGRADE = registerItem(
            "range_ultimate_upgrade", () -> new MachineUpgradeItem(MachineUpgradeInit.RANGE_ULTIMATE));

    public static final RegistryObject<MachineUpgradeItem> EFFICIENCY_1_UPGRADE = registerItem("efficiency_1_upgrade",
            () -> new MachineUpgradeItem(MachineUpgradeInit.EFFICIENCY_1));

    public static final RegistryObject<MachineUpgradeItem> EFFICIENCY_2_UPGRADE = registerItem("efficiency_2_upgrade",
            () -> new MachineUpgradeItem(MachineUpgradeInit.EFFICIENCY_2));

    public static final RegistryObject<MachineUpgradeItem> EFFICIENCY_3_UPGRADE = registerItem("efficiency_3_upgrade",
            () -> new MachineUpgradeItem(MachineUpgradeInit.EFFICIENCY_3));

    public static final RegistryObject<MachineUpgradeItem> EFFICIENCY_ULTIMATE_UPGRADE = registerItem(
            "efficiency_ultimate_upgrade", () -> new MachineUpgradeItem(MachineUpgradeInit.EFFICIENCY_ULTIMATE));

    public static final RegistryObject<MachineUpgradeItem> SILK_TOUCH_UPGRADE = registerItem("silk_touch_upgrade",
            () -> new MachineUpgradeItem(MachineUpgradeInit.SILK_TOUCH));

    public static final RegistryObject<MachineUpgradeItem> COUNT_1_UPGRADE = registerItem("count_1_upgrade",
            () -> new MachineUpgradeItem(MachineUpgradeInit.COUNT_1));

    public static final RegistryObject<MachineUpgradeItem> COUNT_2_UPGRADE = registerItem("count_2_upgrade",
            () -> new MachineUpgradeItem(MachineUpgradeInit.COUNT_2));

    public static final RegistryObject<MachineUpgradeItem> COUNT_3_UPGRADE = registerItem("count_3_upgrade",
            () -> new MachineUpgradeItem(MachineUpgradeInit.COUNT_3));

    public static final RegistryObject<MachineUpgradeItem> COUNT_ULTIMATE_UPGRADE = registerItem(
            "count_ultimate_upgrade", () -> new MachineUpgradeItem(MachineUpgradeInit.COUNT_ULTIMATE));

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
