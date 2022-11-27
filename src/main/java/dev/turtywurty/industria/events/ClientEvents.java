package dev.turtywurty.industria.events;

import dev.turtywurty.industria.Industria;
import dev.turtywurty.industria.client.screens.BiomassGeneratorScreen;
import dev.turtywurty.industria.client.screens.CrusherScreen;
import dev.turtywurty.industria.init.MenuInit;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

public class ClientEvents {
    @Mod.EventBusSubscriber(modid = Industria.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ModEvents {
        @SubscribeEvent
        public static void clientSetup(FMLClientSetupEvent event) {
            event.enqueueWork(() -> {
                Industria.LOGGER.info("Client setup");
                MenuScreens.register(MenuInit.CRUSHER.get(), CrusherScreen::new);
                MenuScreens.register(MenuInit.BIOMASS_GENERATOR.get(), BiomassGeneratorScreen::new);
            });
        }
    }
}
