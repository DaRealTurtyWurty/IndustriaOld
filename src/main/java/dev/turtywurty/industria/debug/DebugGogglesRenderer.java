package dev.turtywurty.industria.debug;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.turtywurty.industria.Industria;
import dev.turtywurty.turtylib.client.util.GuiUtils;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.energy.IEnergyStorage;

public class DebugGogglesRenderer {
    public static void renderEnergyStorage(IEnergyStorage energyStorage, BlockPos hitPos, PoseStack poseStack, Camera camera) {
        // render text above the position
        poseStack.pushPose();
        Vec3 offset = Vec3.atLowerCornerOf(hitPos).subtract(camera.getPosition());
        poseStack.translate(offset.x, offset.y, offset.z);
        poseStack.translate(0.5, 1.5, 0.5);
        poseStack.mulPose(Minecraft.getInstance().getEntityRenderDispatcher().cameraOrientation());
        poseStack.scale(-0.025F, -0.025F, 0.025F);

        Font font = Minecraft.getInstance().font;

        String text = "Energy: " + energyStorage.getEnergyStored() + " / " + energyStorage.getMaxEnergyStored();
        poseStack.translate(-font.width(text) / 2F, 0, 0);

        // draw the background
        poseStack.pushPose();
        poseStack.translate(0, 0, 0.01F);
        GuiUtils.drawQuad(poseStack, -2, -2, font.width(text) + 2, font.lineHeight + 1, 0x77FFFFFF);
        poseStack.popPose();

//        // draw a line between the text and the block
//        float height = 1.5f;
//        float width = font.width(text) / 2f;
//        int distance = (int) Math.sqrt(height * height + width * width);
//        float angle = (float) Math.acos(1.5f / distance);
//
//        poseStack.pushPose();
//        poseStack.mulPose(Vector3f.XP.rotation(angle));
//        GuiUtils.drawQuad(poseStack, -2, 9, 5, distance * 2, 0xFF0000FF);
//        poseStack.popPose();

        // draw the text
        poseStack.translate(0, 0, -0.01F);
        font.drawInBatch(text, 0, 0, 0xCBCBCB, false, poseStack.last().pose(),
                Minecraft.getInstance().renderBuffers().bufferSource(), Font.DisplayMode.NORMAL, 0xCC000000, 15728880);
        poseStack.popPose();
    }
}
