package dev.turtywurty.industria.client.screens;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import dev.turtywurty.industria.Industria;
import dev.turtywurty.industria.blockentity.ElectricHeaterBlockEntity;
import dev.turtywurty.industria.blockentity.util.heat.HeatModule;
import dev.turtywurty.industria.network.PacketManager;
import dev.turtywurty.industria.network.serverbound.SUpdateTargetTemperaturePacket;
import dev.turtywurty.turtylib.client.ui.components.EnergyWidget;
import dev.turtywurty.turtylib.client.ui.components.NumberInput;
import dev.turtywurty.turtylib.client.util.GuiUtils;
import dev.turtywurty.turtylib.core.util.MathUtils;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Supplier;

public class ElectricHeaterScreen extends Screen {
    private static final Component TITLE = Component.translatable("screen." + Industria.MODID + ".electric_heater");
    private static final ResourceLocation TEXTURE = new ResourceLocation(Industria.MODID,
            "textures/gui/electric_heater.png");

    private final ElectricHeaterBlockEntity blockEntity;
    private final int imageWidth = 176;
    private final int imageHeight = 166;
    private int leftPos, topPos;

    private NumberInput targetTemperatureInput;

    public ElectricHeaterScreen(ElectricHeaterBlockEntity blockEntity) {
        super(TITLE);

        this.blockEntity = blockEntity;
    }

    @Override
    protected void init() {
        super.init();

        this.leftPos = (this.width - this.imageWidth) / 2;
        this.topPos = (this.height - this.imageHeight) / 2;

        addRenderableWidget(new EnergyWidget.Builder(this.leftPos + 8, this.topPos + 12, 16, 60).energyStorage(
                this.blockEntity.getEnergyStorage()).build(this));
        addRenderableWidget(
                new HeatWidget(this.leftPos + 32, this.topPos + 12, 16, 60, this.blockEntity::getHeatModule));

//        this.targetTemperatureInput = addRenderableWidget(new NumberInput(this.leftPos + 8, this.topPos + 80, 80, 20, 0,
//                (int) this.blockEntity.getHeatModule().getCapabilityInstance().getMaxHeat(), Component.empty()));
//        this.targetTemperatureInput.setValue(String.valueOf(this.blockEntity.getTargetTemperature()));
//        targetTemperatureInput.setUnits("K");
//        targetTemperatureInput.setBordered(true);
//        targetTemperatureInput.setVisible(true);
//        targetTemperatureInput.setOnChanged(value -> PacketManager.sendToAllClients(
//                new SUpdateTargetTemperaturePacket(this.blockEntity.getBlockPos(), value)));
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

//        float beTargetTemperature = this.blockEntity.getTargetTemperature();
//        float currentTargetTemperature = Float.parseFloat(this.targetTemperatureInput.getValue()
//                .substring(0, this.targetTemperatureInput.getValue().length() - 2));
//
//        if (beTargetTemperature != currentTargetTemperature)
//            this.targetTemperatureInput.setValue(beTargetTemperature + this.targetTemperatureInput.getUnits());

        super.render(pPoseStack, pMouseX, pMouseY, pPartialTick);
    }

    public static class HeatWidget extends AbstractWidget {
        private final Supplier<HeatModule> heatModuleSupplier;
        private final boolean isHorizontal;

        public HeatWidget(int pX, int pY, int pWidth, int pHeight, Supplier<HeatModule> heatModuleSupplier) {
            super(pX, pY, pWidth, pHeight, Component.empty());
            this.isHorizontal = false;
            this.heatModuleSupplier = heatModuleSupplier;
        }

        public HeatWidget(int pX, int pY, int pWidth, int pHeight, boolean isHorizontal, Supplier<HeatModule> heatModuleSupplier) {
            super(pX, pY, pWidth, pHeight, Component.empty());
            this.isHorizontal = isHorizontal;
            this.heatModuleSupplier = heatModuleSupplier;
        }

        @Override
        public void updateNarration(NarrationElementOutput pNarrationElementOutput) {
            defaultButtonNarrationText(pNarrationElementOutput);
        }

        @Override
        public void render(PoseStack pPoseStack, int pMouseX, int pMouseY, float pPartialTick) {
            if (!this.visible) return;

            this.isHovered = MathUtils.isWithinArea(pMouseX, pMouseY, this.x, this.y, this.width, this.height);
            final HeatModule heatModule = this.heatModuleSupplier.get();
            final float heat = heatModule.getCapabilityInstance().getHeat();
            final float maxHeat = heatModule.getCapabilityInstance().getMaxHeat();
            final float heatPercentage = heat / maxHeat;

            if (this.isHorizontal) {
                final int heatWidth = (int) (this.width * heatPercentage);
                fill(pPoseStack, this.x, this.y, this.x + heatWidth, this.y + this.height, 0xFF00FF00);
            } else {
                final int heatHeight = (int) (this.height * heatPercentage);
                fill(pPoseStack, this.x, this.y + this.height - heatHeight, this.x + this.width, this.y + this.height,
                        0xFF00FF00);
            }

            GuiUtils.drawDebugOutline(pPoseStack, this.x, this.y, this.width, this.height, 1, 0xFF000000);
        }
    }
}
