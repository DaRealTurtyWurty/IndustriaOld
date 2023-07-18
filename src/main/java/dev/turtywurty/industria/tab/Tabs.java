package dev.turtywurty.industria.tab;

import dev.turtywurty.industria.Industria;
import dev.turtywurty.industria.init.BlockInit;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.event.CreativeModeTabEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, modid = Industria.MODID)
public class Tabs {
    private static final List<Supplier<? extends ItemLike>> ADD_TO_TAB = new ArrayList<>();

    private static CreativeModeTab tab;

    @SubscribeEvent
    public static void registerTabs(CreativeModeTabEvent.Register event) {
        tab = event.registerCreativeModeTab(
                new ResourceLocation(Industria.MODID, "tab"),
                builder -> builder.icon(() -> BlockInit.CRUSHER.get().asItem().getDefaultInstance())
                        .withSearchBar()
                        .build());

    }

    @SubscribeEvent
    public static void buildTabContents(CreativeModeTabEvent.BuildContents event) {
        if(event.getTab() == Tabs.tab) {
            ADD_TO_TAB.forEach(event::accept);
        }
    }

    public static void addToTab(Supplier<? extends ItemLike> item) {
        ADD_TO_TAB.add(item);
    }
}
