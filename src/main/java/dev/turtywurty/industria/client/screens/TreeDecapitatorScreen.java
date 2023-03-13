package dev.turtywurty.industria.client.screens;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import dev.turtywurty.industria.Industria;
import dev.turtywurty.industria.menu.TreeDecapitatorMenu;
import dev.turtywurty.turtylib.client.ui.components.EnergyWidget;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;

public class TreeDecapitatorScreen extends AbstractContainerScreen<TreeDecapitatorMenu> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(Industria.MODID,
            "textures/gui/tree_decapitator.png");

    public TreeDecapitatorScreen(TreeDecapitatorMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
        this.imageWidth = 203;
        this.imageHeight = 166;
    }

    @Override
    protected void init() {
        super.init();
        addRenderableWidget(
                new EnergyWidget.Builder(this.leftPos + 8, this.topPos + 16, 16, 50).energyStorage(this.menu::getEnergy,
                        this.menu::getMaxEnergy).build(this));
    }

    @Override
    protected void renderBg(PoseStack pPoseStack, float pPartialTick, int pMouseX, int pMouseY) {
        renderBackground(pPoseStack);
        RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, TEXTURE);
        blit(pPoseStack, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight);
    }

    @Override
    public void render(PoseStack pPoseStack, int pMouseX, int pMouseY, float pPartialTick) {
        super.render(pPoseStack, pMouseX, pMouseY, pPartialTick);
        renderTooltip(pPoseStack, pMouseX, pMouseY);

        for (Slot slot : this.menu.slots) {
            String text = String.valueOf(slot.index);
            int x = this.leftPos + slot.x + 8 - this.font.width(text) / 2;
            int y = this.topPos + slot.y + 8 - this.font.lineHeight / 2;

            this.font.draw(pPoseStack, text, x, y, 0);
        }
    }
}
