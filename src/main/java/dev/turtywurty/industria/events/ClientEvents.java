package dev.turtywurty.industria.events;

import dev.turtywurty.industria.Industria;
import dev.turtywurty.industria.client.blockentityrenderers.ResearcherBlockEntityRenderer;
import dev.turtywurty.industria.client.entityrenderers.WoodBoatRenderer;
import dev.turtywurty.industria.client.model.ResearcherModel;
import dev.turtywurty.industria.client.model.WoodBoatModel;
import dev.turtywurty.industria.client.screens.BiomassGeneratorScreen;
import dev.turtywurty.industria.client.screens.CrusherScreen;
import dev.turtywurty.industria.client.screens.ResearcherScreen;
import dev.turtywurty.industria.entity.WoodBoat;
import dev.turtywurty.industria.init.BlockEntityInit;
import dev.turtywurty.industria.init.EntityInit;
import dev.turtywurty.industria.init.MenuInit;
import dev.turtywurty.industria.init.WoodSetInit;
import dev.turtywurty.industria.init.util.WoodRegistrySet;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Sheets;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.RegisterColorHandlersEvent;
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
                    Sheets.addWoodType(woodSet.woodType);
                }
            });
        }

        @SubscribeEvent
        public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
            Industria.LOGGER.info("Registering renderers");
            event.registerBlockEntityRenderer(BlockEntityInit.RESEARCHER.get(), ResearcherBlockEntityRenderer::new);
            event.registerEntityRenderer(EntityInit.BOAT.get(), context -> new WoodBoatRenderer(context, false));
            event.registerEntityRenderer(EntityInit.CHEST_BOAT.get(), context -> new WoodBoatRenderer(context, true));
        }

        @SubscribeEvent
        public static void registerLayerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event) {
            Industria.LOGGER.info("Registering layer definitions");
            event.registerLayerDefinition(ResearcherModel.LAYER_LOCATION, ResearcherModel::createBodyLayer);

            for (WoodBoat.Type type : WoodBoat.Type.values()) {
                event.registerLayerDefinition(WoodBoatRenderer.createBoatModelName(type),
                        () -> WoodBoatModel.createBodyModel(false));
                event.registerLayerDefinition(WoodBoatRenderer.createChestBoatModelName(type),
                        () -> WoodBoatModel.createBodyModel(true));
            }
        }

        @SubscribeEvent
        public static void registerBlockColorHandlers(RegisterColorHandlersEvent.Block event) {
            Industria.LOGGER.info("Registering block color handlers");
            event.register((state, level, pos, tintIndex) -> {
                if (level != null && pos != null) {
                    return BiomeColors.getAverageFoliageColor(level, pos) + 0x00220A;
                }

                return 0x00BB0A;
            }, WoodSetInit.RUBBER_WOOD_SET.leaves.get());
        }

        @SubscribeEvent
        public static void registerItemColorHandlers(RegisterColorHandlersEvent.Item event) {
            Industria.LOGGER.info("Registering item color handlers");
            final BlockColors blockColors = event.getBlockColors();
            event.register((stack, tintIndex) -> blockColors.getColor(
                            WoodSetInit.RUBBER_WOOD_SET.leaves.get().defaultBlockState(), null, null, tintIndex),
                    WoodSetInit.RUBBER_WOOD_SET.leaves.get());
        }
    }
}
