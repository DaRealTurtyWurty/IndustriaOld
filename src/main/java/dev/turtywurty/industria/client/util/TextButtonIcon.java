package dev.turtywurty.industria.client.util;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.network.chat.Component;

public class TextButtonIcon implements RenderableButtonIcon {
    private final SimpleButton self;
    private final Component text;
    private final int x, y;
    private final boolean centered, shadow;
    private final int color, shadowColor;

    private TextButtonIcon(SimpleButton self, Component text, int x, int y, boolean centered, boolean shadow, int color, int shadowColor) {
        this.self = self;
        this.text = text;
        this.x = x;
        this.y = y;
        this.centered = centered;
        this.shadow = shadow;
        this.color = color;
        this.shadowColor = shadowColor;
    }

    @Override
    public void render(PoseStack poseStack, double mouseX, double mouseY, float partialTicks) {
        if (this.centered) {
            GuiComponent.drawCenteredString(poseStack, Minecraft.getInstance().font, this.text, this.x, this.y,
                    this.color);

            if (this.shadow) {
                GuiComponent.drawCenteredString(poseStack, Minecraft.getInstance().font, this.text, this.x, this.y,
                        this.shadowColor);
            }
        } else {
            GuiComponent.drawString(poseStack, Minecraft.getInstance().font, this.text, this.x, this.y, this.color);

            if (this.shadow) {
                GuiComponent.drawString(poseStack, Minecraft.getInstance().font, this.text, this.x, this.y,
                        this.shadowColor);
            }
        }
    }

    public static class Builder {
        private final SimpleButton self;
        private Component text;
        private int x, y;
        private boolean centered, shadow;
        private int color = 0x404040;
        private int shadowColor = 0;

        public Builder(SimpleButton self) {
            this.self = self;
        }

        public Builder text(Component text) {
            this.text = text;
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

        public Builder center() {
            this.centered = true;
            return this;
        }

        public Builder shadow() {
            this.shadow = true;
            return this;
        }

        public Builder color(int color) {
            this.color = color;
            return this;
        }

        public Builder shadowColor(int shadowColor) {
            this.shadowColor = shadowColor;
            return this;
        }

        public TextButtonIcon build() {
            return new TextButtonIcon(this.self, this.text, this.x, this.y, this.centered, this.shadow, this.color,
                    this.shadowColor);
        }
    }
}
