package dev.turtywurty.industria.client.util;

import com.mojang.blaze3d.vertex.PoseStack;

public interface RenderableButtonIcon {
    void render(PoseStack poseStack, double mouseX, double mouseY, float partialTicks);
}
