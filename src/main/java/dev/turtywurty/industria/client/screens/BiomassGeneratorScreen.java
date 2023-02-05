package dev.turtywurty.industria.client.screens;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import dev.turtywurty.industria.Industria;
import dev.turtywurty.industria.menu.AgitatorMenu;
import dev.turtywurty.industria.menu.BiomassGeneratorMenu;
import io.github.darealturtywurty.turtylib.client.ui.components.EnergyWidget;
import io.github.darealturtywurty.turtylib.client.util.Gradient;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class BiomassGeneratorScreen extends AbstractContainerScreen<BiomassGeneratorMenu> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(Industria.MODID,
            "textures/gui/biomass_generator.png");

    public BiomassGeneratorScreen(BiomassGeneratorMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
        this.imageWidth = 176;
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
        RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, TEXTURE);
        blit(pPoseStack, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight);

        int energyGenerated = this.menu.getAmountGenerated();
        int maxEnergyGenerated = this.menu.getMaxEnergyGenerated();
        int generatedHeight = (int) (energyGenerated / (float) maxEnergyGenerated * 14f);
        blit(pPoseStack, this.leftPos + 81, this.topPos + 60 - generatedHeight, 176, 14 - generatedHeight, 14,
                generatedHeight);
    }

    @Override
    public void render(PoseStack pPoseStack, int pMouseX, int pMouseY, float pPartialTick) {
        super.render(pPoseStack, pMouseX, pMouseY, pPartialTick);
        renderTooltip(pPoseStack, pMouseX, pMouseY);
    }
}
