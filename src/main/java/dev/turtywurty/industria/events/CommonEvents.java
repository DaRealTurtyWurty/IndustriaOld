package dev.turtywurty.industria.events;

import dev.turtywurty.industria.Industria;
import dev.turtywurty.industria.capability.Research;
import dev.turtywurty.industria.capability.ResearchCapability;
import dev.turtywurty.industria.capability.ResearchProvider;
import dev.turtywurty.industria.events.industria.BeeStingEvent;
import dev.turtywurty.industria.events.industria.InventoryChangedEvent;
import dev.turtywurty.industria.init.ItemInit;
import dev.turtywurty.industria.init.ReloadListenerInit;
import dev.turtywurty.industria.init.WoodSetInit;
import dev.turtywurty.industria.init.util.WoodRegistrySet;
import dev.turtywurty.industria.items.ResearchAdvancer;
import dev.turtywurty.industria.network.PacketManager;
import dev.turtywurty.industria.network.clientbound.CSyncNewResearchPacket;
import dev.turtywurty.industria.network.clientbound.CSyncResearchPacket;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FireBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.furnace.FurnaceFuelBurnTimeEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

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

        @SubscribeEvent
        public static void furnaceFuelBurnTime(FurnaceFuelBurnTimeEvent event) {
            Item item = event.getItemStack().getItem();
            if (item == WoodSetInit.RUBBER_WOOD_SET.fenceItem.get() || item == WoodSetInit.RUBBER_WOOD_SET.fenceGateItem.get()) {
                event.setBurnTime(300);
            }
        }

        @SubscribeEvent
        public static void beeSting(BeeStingEvent event) {
            if (event.getEntity() instanceof LivingEntity living) {
                if (living.getMainHandItem().getItem() != Items.GLASS_BOTTLE)
                    return;

                living.getCapability(ForgeCapabilities.ITEM_HANDLER).ifPresent(inventory -> {
                    int slot = 0;
                    for (int index = 0; index < inventory.getSlots(); index++) {
                        ItemStack stack = inventory.getStackInSlot(index);
                        if (stack != living.getMainHandItem()) continue;

                        slot = index;
                        break;
                    }

                    living.getMainHandItem().shrink(1);

                    //ItemStack toInsert = inventory.insertItem(slot,
                    //        ItemInit.IMPURE_FORMIC_ACID.get().getDefaultInstance(), false);
                    //if (!toInsert.isEmpty()) {
                    //    living.spawnAtLocation(toInsert);
                    //}
                });
            }
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

            Industria.LOGGER.info("Adding sign valid blocks");
            Set<Block> newValidBlocks = new HashSet<>(BlockEntityType.SIGN.validBlocks);
            for (WoodRegistrySet woodSet : WoodRegistrySet.getWoodSets()) {
                newValidBlocks.add(woodSet.sign.get());
                newValidBlocks.add(woodSet.wallSign.get());
            }

            BlockEntityType.SIGN.validBlocks = newValidBlocks;

            Industria.LOGGER.info("Registering block flammability");
            FireBlock fireBlock = (FireBlock) Blocks.FIRE;
            for (WoodRegistrySet woodSet : WoodRegistrySet.getWoodSets()) {
                Industria.LOGGER.info("Making '" + woodSet.getName() + " wood set' flammable");
                fireBlock.setFlammable(woodSet.log.get(), 5, 20);
                fireBlock.setFlammable(woodSet.wood.get(), 5, 20);
                fireBlock.setFlammable(woodSet.strippedLog.get(), 5, 20);
                fireBlock.setFlammable(woodSet.strippedWood.get(), 5, 20);
                fireBlock.setFlammable(woodSet.planks.get(), 5, 20);
                fireBlock.setFlammable(woodSet.stairs.get(), 5, 20);
                fireBlock.setFlammable(woodSet.slab.get(), 5, 20);
                fireBlock.setFlammable(woodSet.fence.get(), 5, 20);
                fireBlock.setFlammable(woodSet.fenceGate.get(), 5, 20);
                fireBlock.setFlammable(woodSet.leaves.get(), 30, 60);
            }

            Industria.LOGGER.info("Registering log stripping");
            for (WoodRegistrySet woodSet : WoodRegistrySet.getWoodSets()) {
                Industria.LOGGER.info("Registering '" + woodSet.getName() + " wood set' log stripping");
                Map<Block, Block> strippables = new HashMap<>(AxeItem.STRIPPABLES);
                strippables.put(woodSet.wood.get(), woodSet.strippedWood.get());
                AxeItem.STRIPPABLES = Map.copyOf(strippables);
            }
        }
    }
}
