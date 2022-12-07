package dev.turtywurty.industria.events;

import dev.turtywurty.industria.Industria;
import dev.turtywurty.industria.client.blockentityrenderers.ResearcherBlockEntityRenderer;
import dev.turtywurty.industria.client.model.ResearcherModel;
import dev.turtywurty.industria.client.screens.BiomassGeneratorScreen;
import dev.turtywurty.industria.client.screens.CrusherScreen;
import dev.turtywurty.industria.client.screens.ResearcherScreen;
import dev.turtywurty.industria.init.BlockEntityInit;
import dev.turtywurty.industria.init.MenuInit;
import dev.turtywurty.industria.init.util.WoodRegistrySet;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Sheets;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

public class ClientEvents {
    @Mod.EventBusSubscriber(modid = Industria.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ModEvents {
        @SubscribeEvent
        public static void clientSetup(FMLClientSetupEvent event) {
            event.enqueueWork(() -> {
                Industria.LOGGER.info("Registering screens");
                MenuScreens.register(MenuInit.CRUSHER.get(), CrusherScreen::new);
                MenuScreens.register(MenuInit.BIOMASS_GENERATOR.get(), BiomassGeneratorScreen::new);
                MenuScreens.register(MenuInit.RESEARCHER.get(), ResearcherScreen::new);

                Industria.LOGGER.info("Adding wood types to atlas");
                for (WoodRegistrySet woodSet : WoodRegistrySet.getWoodSets()) {
                    Sheets.addWoodType(woodSet.getWoodType());
                    ItemBlockRenderTypes.setRenderLayer(woodSet.getSapling().get(), RenderType.cutout());
                    ItemBlockRenderTypes.setRenderLayer(woodSet.getLeaves().get(), RenderType.cutout());
                    ItemBlockRenderTypes.setRenderLayer(woodSet.getDoor().get(), RenderType.cutout());
                    ItemBlockRenderTypes.setRenderLayer(woodSet.getTrapDoor().get(), RenderType.cutout());
                }
            });
        }

        @SubscribeEvent
        public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
            Industria.LOGGER.info("Registering renderers");
            event.registerBlockEntityRenderer(BlockEntityInit.RESEARCHER.get(), ResearcherBlockEntityRenderer::new);
        }

        @SubscribeEvent
        public static void registerLayerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event) {
            Industria.LOGGER.info("Registering layer definitions");
            event.registerLayerDefinition(ResearcherModel.LAYER_LOCATION, ResearcherModel::createBodyLayer);
        }
    }
}
