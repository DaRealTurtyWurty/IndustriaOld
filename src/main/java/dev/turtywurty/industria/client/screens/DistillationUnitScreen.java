package dev.turtywurty.industria.client.screens;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import dev.turtywurty.industria.Industria;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class DistillationUnitScreen extends Screen {
    private static final Component TITLE = Component.translatable("screen." + Industria.MODID + ".distillation_unit");
    private static final ResourceLocation TEXTURE = new ResourceLocation(Industria.MODID,
            "textures/gui/distillation_unit.png");

    private final int imageWidth = 176;
    private final int imageHeight = 166;
    private int leftPos, topPos;

    public DistillationUnitScreen() {
        super(TITLE);
    }

    @Override
    protected void init() {
        super.init();
        this.leftPos = (this.width - this.imageWidth) / 2;
        this.topPos = (this.height - this.imageHeight) / 2;
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public void render(PoseStack pPoseStack, int pMouseX, int pMouseY, float pPartialTick) {
        renderBackground(pPoseStack);

        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, TEXTURE);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        blit(pPoseStack, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight);

        super.render(pPoseStack, pMouseX, pMouseY, pPartialTick);
    }
}
