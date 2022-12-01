package dev.turtywurty.industria.init;

import dev.turtywurty.industria.Industria;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
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


    public static final RegistryObject<Item> BIOMASS = registerItem("biomass");

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
}
