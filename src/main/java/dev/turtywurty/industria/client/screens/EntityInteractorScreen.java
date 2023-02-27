package dev.turtywurty.industria.client.screens;

import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import dev.turtywurty.industria.Industria;
import dev.turtywurty.industria.blockentity.EntityInteractorBlockEntity;
import dev.turtywurty.industria.menu.EntityInteractorMenu;
import dev.turtywurty.industria.network.PacketManager;
import dev.turtywurty.industria.network.serverbound.SChangeSelectedSlotPacket;
import dev.turtywurty.industria.network.serverbound.SExperienceButtonPacket;
import dev.turtywurty.industria.network.serverbound.SRequestPlayerGameModePacket;
import dev.turtywurty.industria.network.serverbound.SSwitchGameModePacket;
import io.github.darealturtywurty.turtylib.client.ui.components.EnergyWidget;
import io.github.darealturtywurty.turtylib.client.util.GuiUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameType;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.gui.widget.ForgeSlider;
import net.minecraftforge.client.gui.widget.ScrollPanel;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

public class EntityInteractorScreen extends AbstractContainerScreen<EntityInteractorMenu> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(Industria.MODID,
            "textures/gui/entity_interactor.png");
    private static final ResourceLocation WIDGETS = new ResourceLocation(Industria.MODID,
            "textures/gui/entity_interactor_widgets.png");

    private final double originalGuiScale;

    private boolean settingsOpen = false;

    private Button settingsButton;
    private ForgeSlider interactRateSlider;
    private GameModeButton gameModeButton;
    private SelectedSlotButton selectedSlotButton;
    private ExperienceWidget experienceWidget;
    private EffectsWidget effectsWidget;

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

        this.gameModeButton = addRenderableWidget(new GameModeButton());
        PacketManager.sendToServer(new SRequestPlayerGameModePacket(this.menu.getPos()));

        this.selectedSlotButton = addRenderableWidget(new SelectedSlotButton());
        if (Minecraft.getInstance().level != null) {
            if (Minecraft.getInstance().level.getBlockEntity(
                    this.getMenu().getPos()) instanceof EntityInteractorBlockEntity blockEntity) {
                this.selectedSlotButton.setSlot(blockEntity.getPlayer().getInventory().selected);
            }
        }

        this.experienceWidget = new ExperienceWidget(this.leftPos - 70, this.topPos + 56, 62);

        this.effectsWidget = new EffectsWidget(this.leftPos - 70, this.topPos + 80, 62);
    }

    public void receivePlayerGameMode(GameType type) {
        this.gameModeButton.setGameMode(type);
    }

    private void onSettingsButtonClicked(Button pButton) {
        this.settingsOpen = !this.settingsOpen;
        this.gameModeButton.visible = this.settingsOpen;
        this.gameModeButton.active = this.settingsOpen;
        this.selectedSlotButton.visible = this.settingsOpen;
        this.selectedSlotButton.active = this.settingsOpen;
        this.experienceWidget.setVisible(this.settingsOpen);
        this.effectsWidget.setVisible(this.settingsOpen);
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
        this.experienceWidget.render(pPoseStack, pMouseX, pMouseY, pPartialTick);
        this.effectsWidget.render(pPoseStack, pMouseX, pMouseY, pPartialTick);
        renderPlayer(pPoseStack, pMouseX, pMouseY);
        renderTooltip(pPoseStack, pMouseX, pMouseY);
    }

    private void renderPlayer(PoseStack pPoseStack, float pMouseX, float pMouseY) {
        if (this.minecraft == null || this.minecraft.level == null) return;

        if (this.minecraft.level.getBlockEntity(
                this.menu.getPos()) instanceof EntityInteractorBlockEntity blockEntity) {
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

            GuiUtils.renderEntity(pPoseStack, player, new Vec3(-angleY * 10f, 180 + angleX * 40f, 0),
                    new Vec3(30, 30, 30), new Vec3(0, 0, 0), this.leftPos + 50, this.topPos + 75,
                    this.minecraft.getPartialTick());

            player.setInvisible(true);
        }
    }

    private void openEffectAdder() {
        Minecraft.getInstance().pushGuiLayer(new EffectAdderScreen());
    }

    private class GameModeButton extends AbstractWidget {
        private GameType gameType = GameType.SURVIVAL;

        private GameModeButton() {
            super(EntityInteractorScreen.this.leftPos - 70, EntityInteractorScreen.this.topPos + 10, 20, 20,
                    Component.empty());
            this.visible = EntityInteractorScreen.this.settingsOpen;
            this.active = EntityInteractorScreen.this.settingsOpen;
        }

        @Override
        protected boolean clicked(double pMouseX, double pMouseY) {
            if (isMouseOver(pMouseX, pMouseY)) {
                setGameMode(GameType.values()[(this.gameType.ordinal() + 1) % GameType.values().length]);
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
            Component tooltip = this.gameType.getShortDisplayName();
            EntityInteractorScreen.this.renderTooltip(pPoseStack, tooltip, pMouseX, pMouseY);
        }

        @Override
        public void updateNarration(NarrationElementOutput pNarrationElementOutput) {
            defaultButtonNarrationText(pNarrationElementOutput);
        }

        public void setGameMode(GameType pGameType) {
            GameType oldGameType = this.gameType;
            this.gameType = pGameType;
            if (oldGameType != this.gameType) {
                PacketManager.sendToServer(
                        new SSwitchGameModePacket(this.gameType, EntityInteractorScreen.this.getMenu().getPos()));
            }
        }

        @Override
        public void renderButton(PoseStack pPoseStack, int pMouseX, int pMouseY, float pPartialTick) {
            RenderSystem.setShader(GameRenderer::getPositionTexShader);
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            RenderSystem.setShaderTexture(0, WIDGETS);

            int xOffset = this.gameType.ordinal() * this.width;
            int yOffset = (this.isHovered ? 1 : 0) * this.height;
            blit(pPoseStack, this.x, this.y, xOffset, yOffset, this.width, this.height);
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
                PacketManager.sendToServer(
                        new SChangeSelectedSlotPacket(this.slot, EntityInteractorScreen.this.getMenu().getPos()));
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

        public void setSlot(int slot) {
            if (slot < 0) slot = 0;
            else if (slot > 8) slot = 8;

            this.slot = slot;
        }
    }

    public class ExperienceWidget {
        private final ExperienceBar experienceBar;
        private final Button addLevelButton, removeLevelButton, addExpButton, removeExpButton;

        public ExperienceWidget(int x, int y, int width) {
            AtomicReference<Player> player = new AtomicReference<>();
            if (Minecraft.getInstance().level == null)
                throw new IllegalStateException("EntityInteractorScreen's level is null!");

            if (Minecraft.getInstance().level.getBlockEntity(EntityInteractorScreen.this.getMenu()
                                                                                        .getPos()) instanceof EntityInteractorBlockEntity blockEntity) {
                player.set(blockEntity.getPlayer());
            }

            if (player.get() == null) {
                throw new IllegalStateException("EntityInteractorScreen's player is null!");
            }

            this.experienceBar = new ExperienceBar(x - 1, y + 14, width);

            this.addLevelButton = new ExperienceButton(x + 36, y, SExperienceButtonPacket.Type.ADD_LEVEL);
            this.removeLevelButton = new ExperienceButton(x, y, SExperienceButtonPacket.Type.REMOVE_LEVEL);
            this.addExpButton = new ExperienceButton(x + 36, y + 20, SExperienceButtonPacket.Type.ADD_EXP);
            this.removeExpButton = new ExperienceButton(x, y + 20, SExperienceButtonPacket.Type.REMOVE_EXP);

            setVisible(EntityInteractorScreen.this.settingsOpen);
            addWidgets();
        }

        public void render(PoseStack pPoseStack, int pMouseX, int pMouseY, float pPartialTick) {
            this.experienceBar.render(pPoseStack, pMouseX, pMouseY, pPartialTick);
            this.addLevelButton.render(pPoseStack, pMouseX, pMouseY, pPartialTick);
            this.removeLevelButton.render(pPoseStack, pMouseX, pMouseY, pPartialTick);
            this.addExpButton.render(pPoseStack, pMouseX, pMouseY, pPartialTick);
            this.removeExpButton.render(pPoseStack, pMouseX, pMouseY, pPartialTick);
        }

        public void setVisible(boolean settingsOpen) {
            this.experienceBar.visible = settingsOpen;
            this.addLevelButton.visible = settingsOpen;
            this.removeLevelButton.visible = settingsOpen;
            this.addExpButton.visible = settingsOpen;
            this.removeExpButton.visible = settingsOpen;

            this.experienceBar.active = settingsOpen;
            this.addLevelButton.active = settingsOpen;
            this.removeLevelButton.active = settingsOpen;
            this.addExpButton.active = settingsOpen;
            this.removeExpButton.active = settingsOpen;
        }

        private void addWidgets() {
            EntityInteractorScreen.this.addWidget(this.experienceBar);
            EntityInteractorScreen.this.addWidget(this.addLevelButton);
            EntityInteractorScreen.this.addWidget(this.removeLevelButton);
            EntityInteractorScreen.this.addWidget(this.addExpButton);
            EntityInteractorScreen.this.addWidget(this.removeExpButton);
        }
    }

    public class ExperienceButton extends Button {
        private final SExperienceButtonPacket.Type type;

        public ExperienceButton(int pX, int pY, SExperienceButtonPacket.Type type) {
            super(pX, pY, 23, 12, Component.literal(type.getText()), ($) -> {
            });

            this.type = type;
        }

        @Override
        public void render(PoseStack pPoseStack, int pMouseX, int pMouseY, float pPartialTick) {
            if (!this.visible || !this.active) return;

            this.isHovered =
                    pMouseX >= this.x && pMouseY >= this.y && pMouseX < this.x + this.width && pMouseY < this.y + this.height;

            RenderSystem.setShader(GameRenderer::getPositionTexShader);
            RenderSystem.setShaderTexture(0, WIDGETS);
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

            int i = getYImage(this.isHovered) - 1;
            blit(pPoseStack, this.x, this.y, 0, 84 + (i * this.height), this.width, this.height);

            drawString(pPoseStack, Minecraft.getInstance().font, this.getMessage(), this.x + 2, this.y + 2, 0xFFFFFF);
        }

        @Override
        protected boolean clicked(double pMouseX, double pMouseY) {
            if (this.isHovered) {
                PacketManager.sendToServer(
                        new SExperienceButtonPacket(this.type, EntityInteractorScreen.this.getMenu().getPos()));
            }

            return super.clicked(pMouseX, pMouseY);
        }
    }

    public class ExperienceBar extends AbstractWidget {
        public ExperienceBar(int pX, int pY, int pWidth) {
            super(pX, pY, pWidth, 5, Component.empty());
        }

        @Override
        public void updateNarration(NarrationElementOutput pNarrationElementOutput) {
            defaultButtonNarrationText(pNarrationElementOutput);
        }

        @Override
        public void render(PoseStack pPoseStack, int pMouseX, int pMouseY, float pPartialTick) {
            renderExperienceBar(pPoseStack, this.x);
        }

        public void renderExperienceBar(PoseStack pPoseStack, int pXPos) {
            if (!this.visible || !this.active) return;

            if (Minecraft.getInstance().level == null) return;

            AtomicReference<Player> player = new AtomicReference<>();
            if (Minecraft.getInstance().level.getBlockEntity(EntityInteractorScreen.this.getMenu()
                                                                                        .getPos()) instanceof EntityInteractorBlockEntity blockEntity) {
                player.set(blockEntity.getPlayer());
            }

            if (player.get() == null) {
                Industria.LOGGER.error("Player is null",
                        new IllegalStateException("EntityInteractorScreen's player is null!"));
                return;
            }

            RenderSystem.setShader(GameRenderer::getPositionTexShader);
            RenderSystem.setShaderTexture(0, GuiComponent.GUI_ICONS_LOCATION);
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

            int forNextLevel = player.get().getXpNeededForNextLevel();
            if (forNextLevel > 0) {
                int progress = (int) (player.get().experienceProgress * 64f);
                blit(pPoseStack, pXPos, y, 0, 64, this.width / 2, 5);
                blit(pPoseStack, pXPos + this.width / 2, y, 183 - this.width / 2, 64, this.width / 2, 5);
                if (progress > 0) {
                    if (progress < 61) {
                        blit(pPoseStack, pXPos, y, 0, 69, progress, 5);
                    } else {
                        blit(pPoseStack, pXPos, y, 0, 69, progress / 2, 5);
                        blit(pPoseStack, pXPos + progress / 2, y, 183 - progress / 2, 69, progress / 2, 5);
                    }
                }
            }

            if (player.get().experienceLevel > 0) {
                Font font = EntityInteractorScreen.this.font;
                String xpLevel = "" + player.get().experienceLevel;
                float x = this.x + (this.width / 2f) - font.width(xpLevel) / 2f;
                float y = this.y - 10;
                font.draw(pPoseStack, xpLevel, x + 1, y, 0);
                font.draw(pPoseStack, xpLevel, x - 1, y, 0);
                font.draw(pPoseStack, xpLevel, x, y + 1, 0);
                font.draw(pPoseStack, xpLevel, x, y - 1, 0);
                font.draw(pPoseStack, xpLevel, x, y, 0x80FF20);
            }
        }

        @Override
        protected boolean clicked(double pMouseX, double pMouseY) {
            return false;
        }
    }

    public class EffectsWidget {
        private final ScrollPanel effectsPanel;
        private final AddEffectButton addEffectButton;
        private final RemoveEffectButton removeEffectButton;
        private boolean isDeleteMode = false;

        public EffectsWidget(int pX, int pY, int pWidth) {
            this.effectsPanel = new ScrollPanel(EntityInteractorScreen.this.minecraft, pWidth, 100, pY, pX) {

                @Override
                public void updateNarration(NarrationElementOutput pNarrationElementOutput) {
                }

                @Override
                public NarrationPriority narrationPriority() {
                    return NarrationPriority.NONE;
                }

                @Override
                protected int getContentHeight() {
                    return 0;
                }

                @Override
                protected void drawPanel(PoseStack poseStack, int entryRight, int relativeY, Tesselator tess,
                                         int mouseX, int mouseY) {

                }
            };

            this.addEffectButton = new AddEffectButton(pX + pWidth - 20, pY + 100);
            this.removeEffectButton = new RemoveEffectButton(this, pX + pWidth - 40, pY + 100);

            setVisible(EntityInteractorScreen.this.settingsOpen);
            addWidgets();
        }

        public void render(PoseStack pPoseStack, int pMouseX, int pMouseY, float pPartialTick) {
            if (EntityInteractorScreen.this.settingsOpen) {
                this.effectsPanel.render(pPoseStack, pMouseX, pMouseY, pPartialTick);
            }

            this.addEffectButton.render(pPoseStack, pMouseX, pMouseY, pPartialTick);
            this.removeEffectButton.render(pPoseStack, pMouseX, pMouseY, pPartialTick);
        }

        public void setVisible(boolean visible) {
            this.addEffectButton.visible = visible;
            this.removeEffectButton.visible = visible;
            this.addEffectButton.active = visible;
            this.removeEffectButton.active = visible;

            if (EntityInteractorScreen.this.children().contains(this.effectsPanel)) {
                EntityInteractorScreen.this.removeWidget(this.effectsPanel);
            } else {
                EntityInteractorScreen.this.addWidget(this.effectsPanel);
            }
        }

        private void addWidgets() {
            EntityInteractorScreen.this.addWidget(this.addEffectButton);
            EntityInteractorScreen.this.addWidget(this.removeEffectButton);
        }

        public void setDeleteMode(boolean deleteMode) {
            this.isDeleteMode = deleteMode;
            // TODO: Add overlay to effect buttons
        }

        public boolean isDeleteMode() {
            return this.isDeleteMode;
        }
    }

    public class AddEffectButton extends AbstractWidget {
        public AddEffectButton(int pX, int pY) {
            super(pX, pY, 20, 20, Component.empty());
        }

        @Override
        public void updateNarration(NarrationElementOutput pNarrationElementOutput) {
            defaultButtonNarrationText(pNarrationElementOutput);
        }

        @Override
        protected boolean clicked(double pMouseX, double pMouseY) {
            if (!this.active || !this.isHovered) return false;

            EntityInteractorScreen.this.openEffectAdder();
            return true;
        }

        @Override
        public void render(PoseStack pPoseStack, int pMouseX, int pMouseY, float pPartialTick) {
            if (!this.visible) return;

            this.isHovered =
                    pMouseX >= this.x && pMouseY >= this.y && pMouseX < this.x + this.width && pMouseY < this.y + this.height;

            int yIncrement = this.isHovered ? 20 : 0;
            GuiUtils.drawQuadSplitSprite(pPoseStack, WIDGETS_LOCATION, this.x, this.y, this.width, this.height, 0,
                    66 + yIncrement, 199, 85 + yIncrement);

            if (this.isHovered) {
                renderToolTip(pPoseStack, pMouseX, pMouseY);
            }
        }
    }

    public class RemoveEffectButton extends AbstractWidget {
        private final EffectsWidget parent;

        public RemoveEffectButton(EffectsWidget parent, int pX, int pY) {
            super(pX, pY, 20, 20, Component.empty());

            this.parent = parent;
        }

        @Override
        public void updateNarration(NarrationElementOutput pNarrationElementOutput) {
            defaultButtonNarrationText(pNarrationElementOutput);
        }

        @Override
        protected boolean clicked(double pMouseX, double pMouseY) {
            if (!this.active || !this.isHovered) return false;

            this.parent.setDeleteMode(!this.parent.isDeleteMode());
            return true;
        }

        @Override
        public void render(PoseStack pPoseStack, int pMouseX, int pMouseY, float pPartialTick) {
            if (!this.visible) return;

            this.isHovered =
                    pMouseX >= this.x && pMouseY >= this.y && pMouseX < this.x + this.width && pMouseY < this.y + this.height;

            RenderSystem.setShader(GameRenderer::getPositionTexShader);
            RenderSystem.setShaderTexture(0, WIDGETS);
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, this.alpha);

            int yIncrement = this.isHovered ? 20 : 0;
            blit(pPoseStack, this.x, this.y, 20, 108 + yIncrement, this.width, this.height);

            if (this.isHovered) {
                renderToolTip(pPoseStack, pMouseX, pMouseY);
            }
        }
    }

    public static class EffectAdderScreen extends Screen {
        private static final Component TITLE = Component.translatable(
                "gui." + Industria.MODID + ".entity_interactor.effect_adder");

        private static final ResourceLocation TEXTURE = new ResourceLocation(Industria.MODID,
                "textures/gui/effect_adder.png");

        private int leftPos, topPos;
        private final int imageWidth, imageHeight;

        public EffectAdderScreen() {
            super(TITLE);

            this.imageWidth = 176;
            this.imageHeight = 166;
        }

        @Override
        protected void init() {
            super.init();

            this.leftPos = (this.width - this.imageWidth) / 2;
            this.topPos = (this.height - this.imageHeight) / 2;

            addRenderableWidget(new SearchBar());
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

        public class SearchBar extends EditBox {
            private final List<MobEffect> suggestions = new ArrayList<>();

            public SearchBar() {
                super(Minecraft.getInstance().font, EffectAdderScreen.this.leftPos + 10,
                        EffectAdderScreen.this.topPos + 10, EffectAdderScreen.this.imageWidth - 20, 20,
                        Component.empty());

                setMaxLength(getMaxLength());
                setResponder(this::onTextChanged);

                setFocus(true);
                setCanLoseFocus(false);

                setSuggestion(ForgeRegistries.MOB_EFFECTS.getKeys().stream().map(ResourceLocation::toString).sorted()
                                                         .findFirst().orElse("Search..."));

                setTextColor(0xFFFFFF);
                setTextColorUneditable(0xAAAAAA);

                setBordered(false);
            }

            private static int getMaxLength() {
                return ForgeRegistries.MOB_EFFECTS.getKeys().stream().map(ResourceLocation::toString)
                                                  .mapToInt(String::length).max().orElse(32);
            }

            private void onTextChanged(String text) {
                this.suggestions.clear();
                this.suggestions.addAll(ForgeRegistries.MOB_EFFECTS.getEntries().stream()
                                                                   .filter(entry -> entry.getKey().location().toString()
                                                                                         .contains(text))
                                                                   .map(Map.Entry::getValue).toList());
            }

            @Override
            public void render(PoseStack pPoseStack, int pMouseX, int pMouseY, float pPartialTick) {
                super.render(pPoseStack, pMouseX, pMouseY, pPartialTick);

                if (this.suggestions.isEmpty()) return;

                for (int index = 0; index < this.suggestions.size(); index++) {
                    MobEffect effect = this.suggestions.get(index);

                    GuiComponent.fill(pPoseStack, this.x, this.y + this.height + index * 10, this.x + this.width,
                            this.y + this.height + (index + 1) * 10, 0x88000000);

                    Minecraft.getInstance().font.draw(pPoseStack, effect.getDisplayName(), x + 2, y + 2 + index * 10,
                            0xFFFFFF);
                }
            }
        }
    }
}
