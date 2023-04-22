package dev.turtywurty.industria.debug;

import dev.turtywurty.industria.Industria;
import dev.turtywurty.industria.init.ItemInit;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Industria.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class ClientDebugEvents {
    @SubscribeEvent
    public static void renderLevelStage(RenderLevelStageEvent event) {
        if (event.getStage() == RenderLevelStageEvent.Stage.AFTER_SOLID_BLOCKS) {
            LocalPlayer player = Minecraft.getInstance().player;
            if (player == null) return;

            // check to see if the player is wearing the debug goggles
            if (player.getItemBySlot(EquipmentSlot.HEAD).getItem() != ItemInit.DEBUG_GOGGLES.get()) return;

            // get the block that the player is looking at
            HitResult hitResult = player.pick(player.getAttributeBaseValue(ForgeMod.REACH_DISTANCE.get()),
                    event.getPartialTick(), false);

            if (hitResult.getType() != HitResult.Type.BLOCK) return;

            var hitPos = new BlockPos(hitResult.getLocation());
            BlockEntity blockEntity = player.level.getBlockEntity(hitPos);
            if (blockEntity == null) return;

            // check to see if the block entity has an energy storage
            blockEntity.getCapability(ForgeCapabilities.ENERGY).ifPresent(energyStorage -> {
                // render a debug overlay
                DebugGogglesRenderer.renderEnergyStorage(energyStorage, hitPos, event.getPoseStack(),
                        event.getCamera());
            });
        }
    }
}
