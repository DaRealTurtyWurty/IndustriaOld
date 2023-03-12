package dev.turtywurty.industria.client.util;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;

public class TextureButtonIcon implements RenderableButtonIcon {
    private final SimpleButton self;
    private final ResourceLocation texture;
    private final int x, y, u, v, width, height, textureWidth, textureHeight;

    private TextureButtonIcon(SimpleButton self, ResourceLocation texture, int x, int y, int u, int v, int width,
            int height, int textureWidth, int textureHeight) {
        this.self = self;
        this.texture = texture;
        this.x = x;
        this.y = y;
        this.u = u;
        this.v = v;
        this.width = width;
        this.height = height;
        this.textureWidth = textureWidth;
        this.textureHeight = textureHeight;
    }

    @Override
    public void render(PoseStack poseStack, double mouseX, double mouseY, float partialTicks) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, this.texture);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        GuiComponent.blit(poseStack, this.x, this.y, this.u, this.v, this.width, this.height, this.textureWidth,
                this.textureHeight);
    }

    public static class Builder {
        private final SimpleButton self;
        private ResourceLocation texture;
        private int x, y, u, v, width, height, textureWidth = 256, textureHeight = 256;

        public Builder(SimpleButton self) {
            this.self = self;
            this.x = self.x;
            this.y = self.y;
            this.width = self.getWidth();
            this.height = self.getHeight();
        }

        public Builder texture(ResourceLocation texture) {
            this.texture = texture;
            return this;
        }

        public Builder x(int x) {
            this.x = x;
            return this;
        }

        public Builder y(int y) {
            this.y = y;
            return this;
        }

        public Builder u(int u) {
            this.u = u;
            return this;
        }

        public Builder v(int v) {
            this.v = v;
            return this;
        }

        public Builder width(int width) {
            this.width = width;
            return this;
        }

        public Builder height(int height) {
            this.height = height;
            return this;
        }

        public Builder textureWidth(int textureWidth) {
            this.textureWidth = textureWidth;
            return this;
        }

        public Builder textureHeight(int textureHeight) {
            this.textureHeight = textureHeight;
            return this;
        }

        public TextureButtonIcon build() {
            return new TextureButtonIcon(this.self, this.texture, this.x, this.y, this.u, this.v, this.width,
                    this.height, this.textureWidth, this.textureHeight);
        }
    }
}
