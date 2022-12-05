package dev.turtywurty.industria.events;

import dev.turtywurty.industria.Industria;
import dev.turtywurty.industria.capability.Research;
import dev.turtywurty.industria.capability.ResearchCapability;
import dev.turtywurty.industria.capability.ResearchProvider;
import dev.turtywurty.industria.events.industria.InventoryChangedEvent;
import dev.turtywurty.industria.init.ReloadListenerInit;
import dev.turtywurty.industria.items.ResearchAdvancer;
import dev.turtywurty.industria.network.PacketManager;
import dev.turtywurty.industria.network.clientbound.CSyncNewResearchPacket;
import dev.turtywurty.industria.network.clientbound.CSyncResearchPacket;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

public class CommonEvents {
    @Mod.EventBusSubscriber(modid = Industria.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
    public static class ForgeEvents {
        @SubscribeEvent
        public static void onInventoryChanged(InventoryChangedEvent event) {
            if (event.getStack().getItem() instanceof ResearchAdvancer researchAdvancer) {
                final Player player = event.getEntity();
                final Research research = player.getCapability(ResearchCapability.INSTANCE)
                        .orElseThrow(() -> new IllegalStateException("Player does not have research capability!"));
                if (research.hasAdvancer(researchAdvancer)) return;

                research.addAdvancer(researchAdvancer);
                PacketManager.sendToClient(new CSyncNewResearchPacket(researchAdvancer), player);
            }
        }

        @SubscribeEvent
        public static void attachCapabilities(AttachCapabilitiesEvent<Entity> event) {
            if (event.getObject() instanceof Player) {
                ResearchProvider.attach(event);
            }
        }

        @SubscribeEvent
        public static void playerClone(PlayerEvent.Clone event) {
            if (event.isWasDeath()) {
                event.getOriginal().getCapability(ResearchCapability.INSTANCE).ifPresent(
                        oldResearch -> event.getEntity().getCapability(ResearchCapability.INSTANCE).ifPresent(
                                newResearch -> oldResearch.getAdvancers().forEach(newResearch::addAdvancer)));
            }
        }

        @SubscribeEvent
        public static void playerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
            if (!event.getEntity().level.isClientSide()) {
                event.getEntity().getCapability(ResearchCapability.INSTANCE).ifPresent(
                        research -> PacketManager.sendToClient(new CSyncResearchPacket(research.getAdvancers()),
                                event.getEntity()));
            }
        }

        @SubscribeEvent
        public static void addReloadListeners(AddReloadListenerEvent event) {
            Industria.LOGGER.info("Adding reload listeners");
            event.addListener(ReloadListenerInit.RESEARCH_DATA);
        }
    }

    @Mod.EventBusSubscriber(modid = Industria.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class ModEvents {
        @SubscribeEvent
        public static void commonSetup(FMLCommonSetupEvent event) {
            event.enqueueWork(() -> {
                Industria.LOGGER.info("Entering common setup phase");
                PacketManager.init();
            });
        }
    }
}
