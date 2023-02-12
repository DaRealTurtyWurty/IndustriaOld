package dev.turtywurty.industria.client.screens;

import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;
import dev.turtywurty.industria.Industria;
import dev.turtywurty.industria.blockentity.EntityInteractorBlockEntity;
import dev.turtywurty.industria.menu.EntityInteractorMenu;
import io.github.darealturtywurty.turtylib.client.ui.components.EnergyWidget;
import io.github.darealturtywurty.turtylib.client.util.GuiUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.StateSwitchingButton;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.gui.widget.ForgeSlider;

public class EntityInteractorScreen extends AbstractContainerScreen<EntityInteractorMenu> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(Industria.MODID,
            "textures/gui/entity_interactor.png");
    private static final ResourceLocation WIDGETS = new ResourceLocation(Industria.MODID,
            "textures/gui/entity_interactor_widgets.png");

    private static final ResourceLocation STEVE_TEXTURE = new ResourceLocation("textures/entity/steve.png");

    private final double originalGuiScale;

    private boolean settingsOpen = false;

    private Button settingsButton;
    private ForgeSlider interactRateSlider;
    private CreativeSurvivalButton creativeSurvivalButton;
    private SelectedSlotButton selectedSlotButton;


    public EntityInteractorScreen(EntityInteractorMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
        this.imageWidth = 176;
        this.imageHeight = 256;

        this.titleLabelX = 79;
        this.titleLabelY = 7;
        this.inventoryLabelY = 162;

        Window window = Minecraft.getInstance().getWindow();
        this.originalGuiScale = window.getGuiScale();
        if (window.getGuiScaledHeight() <= this.imageHeight) {
            window.setGuiScale(this.originalGuiScale - 1);
        }
    }

    @Override
    protected void init() {
        super.init();

        addRenderableWidget(new EnergyWidget.Builder(this.leftPos + 77, this.topPos + 17, 16, 42).energyStorage(
                this.menu::getEnergy, this.menu::getMaxEnergy).build(this));

        this.settingsButton = addRenderableWidget(
                new Button(this.leftPos + 149, this.topPos + 60, 20, 20, Component.literal("S"),
                        this::onSettingsButtonClicked));

        this.interactRateSlider = addRenderableWidget(
                new ForgeSlider(this.leftPos + 97, this.topPos + 60, 50, 20, Component.empty(), Component.literal("/t"),
                        10, 100, 20, true));

        this.creativeSurvivalButton = addRenderableWidget(new CreativeSurvivalButton());
        this.selectedSlotButton = addRenderableWidget(new SelectedSlotButton());
    }

    private void onSettingsButtonClicked(Button pButton) {
        this.settingsOpen = !this.settingsOpen;
        this.creativeSurvivalButton.visible = this.settingsOpen;
        this.creativeSurvivalButton.active = this.settingsOpen;
        this.selectedSlotButton.visible = this.settingsOpen;
        this.selectedSlotButton.active = this.settingsOpen;
    }

    @Override
    public void resize(Minecraft pMinecraft, int pWidth, int pHeight) {
        if (pHeight <= this.imageHeight) {
            pMinecraft.getWindow().setGuiScale(pMinecraft.getWindow().getGuiScale() - 1);
        } else {
            pMinecraft.getWindow().setGuiScale(this.originalGuiScale);
        }

        super.resize(pMinecraft, pMinecraft.getWindow().getGuiScaledWidth(),
                pMinecraft.getWindow().getGuiScaledHeight());
    }

    @Override
    public void onClose() {
        Minecraft.getInstance().getWindow().setGuiScale(this.originalGuiScale);

        super.onClose();
    }

    @Override
    protected void renderBg(PoseStack pPoseStack, float pPartialTick, int pMouseX, int pMouseY) {
        renderBackground(pPoseStack);

        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, TEXTURE);
        if (this.settingsOpen) {
            blit(pPoseStack, this.leftPos - 80, this.topPos, this.imageWidth, 0, 80, 137);
        }

        blit(pPoseStack, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight);
    }

    @Override
    public void render(PoseStack pPoseStack, int pMouseX, int pMouseY, float pPartialTick) {
        super.render(pPoseStack, pMouseX, pMouseY, pPartialTick);

        if(Minecraft.getInstance().level.getBlockEntity(this.menu.getPos()) instanceof EntityInteractorBlockEntity blockEntity) {
            Player player = blockEntity.getPlayer();
            player.setInvisible(false);

            float mouseX = pMouseX - (this.leftPos + 50);
            float mouseY = pMouseY - (this.topPos + 75);
            float angleX = -(float) Math.atan(mouseX / 40f);
            float angleY = -(float) Math.atan(mouseY / 40f);

            float xRot = player.getXRot();
            float xRotO = player.xRotO;
            float yHeadRot = player.getYHeadRot();
            float yHeadRotO = player.yHeadRotO;
            player.setXRot(Mth.lerp(0.1f, xRot, -angleY * 40f));
            player.xRotO = Mth.lerp(0.1f, xRotO, -angleY * 40f);
            player.setYHeadRot(Mth.lerp(0.1f, yHeadRot, angleX * 40f));
            player.yHeadRotO = Mth.lerp(0.1f, yHeadRotO, angleX * 40f);

            GuiUtils.renderEntity(pPoseStack, player, new Vec3(-angleY * 10f, 180 + angleX * 40f, 0), new Vec3(30, 30, 30),
                    new Vec3(0, 0, 0), this.leftPos + 50, this.topPos + 75,
                    Minecraft.getInstance().getPartialTick());

            player.setInvisible(true);
        }

        renderTooltip(pPoseStack, pMouseX, pMouseY);
    }

    private class CreativeSurvivalButton extends StateSwitchingButton {
        private static final Component CREATIVE = Component.translatable("gui.entity_interactor.settings.creative");
        private static final Component SURVIVAL = Component.translatable("gui.entity_interactor.settings.survival");

        private CreativeSurvivalButton() {
            super(EntityInteractorScreen.this.leftPos - 70, EntityInteractorScreen.this.topPos + 10, 20, 20, false);
            initTextureValues(0, 0, 20, 20, WIDGETS);
            this.visible = EntityInteractorScreen.this.settingsOpen;
            this.active = EntityInteractorScreen.this.settingsOpen;
        }

        @Override
        protected boolean clicked(double pMouseX, double pMouseY) {
            if (isMouseOver(pMouseX, pMouseY)) {
                setStateTriggered(!isStateTriggered());
            }

            return super.clicked(pMouseX, pMouseY);
        }

        @Override
        public void render(PoseStack pPoseStack, int pMouseX, int pMouseY, float pPartialTick) {
            super.render(pPoseStack, pMouseX, pMouseY, pPartialTick);

            if (isMouseOver(pMouseX, pMouseY)) {
                renderToolTip(pPoseStack, pMouseX, pMouseY);
            }
        }

        @Override
        public void renderToolTip(PoseStack pPoseStack, int pMouseX, int pMouseY) {
            Component tooltip = isStateTriggered() ? CREATIVE : SURVIVAL;
            EntityInteractorScreen.this.renderTooltip(pPoseStack, tooltip, pMouseX, pMouseY);
        }
    }

    public class SelectedSlotButton extends Button {
        private int slot = 0;

        public SelectedSlotButton() {
            super(EntityInteractorScreen.this.leftPos - 71, EntityInteractorScreen.this.topPos + 32, 22, 22,
                    Component.empty(), ($) -> {
                    });
            this.visible = EntityInteractorScreen.this.settingsOpen;
            this.active = EntityInteractorScreen.this.settingsOpen;
        }

        @Override
        public void render(PoseStack pPoseStack, int pMouseX, int pMouseY, float pPartialTick) {
            if (!this.visible || !this.active) {
                this.isHovered = false;
                return;
            }

            this.isHovered = isMouseOver(pMouseX, pMouseY);

            int yOffset = (this.isHovered ? 1 : 0) * this.height;

            RenderSystem.setShader(GameRenderer::getPositionTexShader);
            RenderSystem.setShaderTexture(0, WIDGETS);
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            blit(pPoseStack, this.x, this.y, 0, 40 + yOffset, this.width, this.height);

            float xPos = this.x + (this.width / 2f) - (EntityInteractorScreen.this.font.width(
                    String.valueOf(this.slot + 1)) / 2f);
            float yPos = this.y + (this.height / 2f) - (EntityInteractorScreen.this.font.lineHeight / 2f);
            EntityInteractorScreen.this.font.draw(pPoseStack, String.valueOf(this.slot + 1), xPos, yPos, 0xFFFFFF);

            if (this.isHovered) {
                renderToolTip(pPoseStack, pMouseX, pMouseY);
            }
        }

        @Override
        protected boolean clicked(double pMouseX, double pMouseY) {
            if (this.isHovered) {
                this.slot = (this.slot + 1) % 9;
            }

            return super.clicked(pMouseX, pMouseY);
        }

        @Override
        public void renderToolTip(PoseStack pPoseStack, int pMouseX, int pMouseY) {
            if (this.isHovered) {
                EntityInteractorScreen.this.renderTooltip(pPoseStack,
                        Component.translatable("gui.entity_interactor.settings.selected_slot")
                                .append(": " + (this.slot + 1)), pMouseX, pMouseY);
            }
        }

        @Override
        public boolean mouseScrolled(double pMouseX, double pMouseY, double pDelta) {
            if (this.isHovered) {
                this.slot = (this.slot + (int) pDelta) % 9;
                if (this.slot < 0) {
                    this.slot = 8;
                }
            }

            return super.mouseScrolled(pMouseX, pMouseY, pDelta);
        }
    }
}
