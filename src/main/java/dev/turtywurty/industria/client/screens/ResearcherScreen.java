package dev.turtywurty.industria.client.screens;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import dev.turtywurty.industria.Industria;
import dev.turtywurty.industria.capability.Research;
import dev.turtywurty.industria.capability.ResearchCapability;
import dev.turtywurty.industria.client.util.ObservableList;
import dev.turtywurty.industria.data.ResearchDataOld;
import dev.turtywurty.industria.items.ResearchAdvancer;
import dev.turtywurty.industria.menu.ResearcherMenu;
import dev.turtywurty.industria.network.PacketManager;
import dev.turtywurty.industria.network.serverbound.SRequestResearchDataPacket;
import dev.turtywurty.industria.network.serverbound.SStartResearchPacket;
import dev.turtywurty.turtylib.client.ui.components.EnergyWidget;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraftforge.client.gui.widget.ExtendedButton;
import net.minecraftforge.client.gui.widget.ScrollPanel;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.logging.log4j.util.TriConsumer;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ResearcherScreen extends AbstractContainerScreen<ResearcherMenu> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(Industria.MODID,
            "textures/gui/researcher.png");
    private ResearcherScrollPanel scrollPanel;
    private StartButton startButton;
    private ResearchOption selectedOption;

    public final List<ResearchDataOld> researchData = new ArrayList<>();
    private final ObservableList<ResearchOption> researchOptions = ObservableList.create(
            () -> this.scrollPanel.update());

    public ResearcherScreen(ResearcherMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
        this.imageWidth = 176;
        this.imageHeight = 166;

        PacketManager.sendToServer(new SRequestResearchDataPacket());
    }

    @Override
    protected void init() {
        super.init();
        addRenderableWidget(
                new EnergyWidget.Builder(this.leftPos + 8, this.topPos + 14, 16, 60).energyStorage(this.menu::getEnergy,
                        this.menu::getMaxEnergy).build(this));
        this.scrollPanel = addRenderableWidget(
                new ResearcherScrollPanel(this.minecraft, this.leftPos + 8, this.topPos + 14, 100, 50));

        this.startButton = addRenderableWidget(new StartButton(this.leftPos + 50, this.topPos + 50));

        List<ResearchOption> options = new ArrayList<>();
        for (ResearchDataOld data : this.researchData) {
            var widget = new ResearchOption(0, 0, data);
            options.add(addWidget(widget));
        }

        this.researchOptions.setAll(options);
        this.scrollPanel.update();
        options.clear();
    }

    @Override
    public void rebuildWidgets() {
        super.rebuildWidgets();
    }

    @Override
    protected void renderBg(PoseStack pPoseStack, float pPartialTick, int pMouseX, int pMouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
        RenderSystem.setShaderTexture(0, TEXTURE);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        blit(pPoseStack, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight);
    }

    @Override
    public void render(PoseStack pPoseStack, int pMouseX, int pMouseY, float pPartialTick) {
        super.render(pPoseStack, pMouseX, pMouseY, pPartialTick);
        renderTooltip(pPoseStack, pMouseX, pMouseY);
    }

    private void startResearch() {
        if (this.selectedOption == null) return;

        ResearchDataOld data = this.selectedOption.data;
        if (data == null) return;

        PacketManager.sendToServer(new SStartResearchPacket(data));
        this.startButton.active = false;
    }

    public void setResearchFailed() {
        this.startButton.active = true;
        // TODO: Receive error message from server and display it
    }

    public class ResearchOption extends ExtendedButton {
        private static final ResourceLocation TEXTURE = new ResourceLocation(Industria.MODID,
                "textures/gui/research_option.png");
        private final ResearchDataOld data;

        public ResearchOption(int pX, int pY, ResearchDataOld data) {
            super(pX, pY, 18, 18, Component.empty(), btn -> {
            });
            this.data = data;
        }

        @Override
        public void updateNarration(NarrationElementOutput pNarrationElementOutput) {
            defaultButtonNarrationText(pNarrationElementOutput);
        }

        @Override
        public void render(PoseStack pPoseStack, int pMouseX, int pMouseY, float pPartialTick) {
            this.isHovered = isMouseOver(pMouseX, pMouseY);

            renderButton(pPoseStack, pMouseX, pMouseY, pPartialTick);

            var registryName = ResourceLocation.tryParse(data.getInputRegistryName());
            if (registryName == null) return;

            if (!(ForgeRegistries.ITEMS.getValue(registryName) instanceof ResearchAdvancer advancer)) return;

            advancer.getResearchIcon().ifLeft(stackSupplier -> {
                Minecraft.getInstance().getItemRenderer()
                         .renderAndDecorateItem(stackSupplier.get(), this.x + 1, this.y + 1);
            }).ifRight(texture -> {
                RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
                RenderSystem.setShaderTexture(0, texture);
                RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
                blit(pPoseStack, this.x + 1, this.y + 1, 0, 0, 16, 16);
            });

            if (!hasResearched(this.data)) {
                fill(pPoseStack, this.x, this.y, this.x + this.width, this.y + this.height, 0x80FF0000);
            }
        }

        @Override
        public void renderToolTip(PoseStack pPoseStack, int pMouseX, int pMouseY) {
            if (hasResearched(this.data)) {
                renderTooltip(pPoseStack, Component.literal(this.data.getInputRegistryName()), pMouseX, pMouseY);
            }
        }

        public boolean isHovered() {
            return this.isHovered;
        }

        private static boolean hasResearched(ResearchDataOld data) {
            return getItemFromData(data).isPresent();
        }

        private static Optional<Item> getItemFromData(ResearchDataOld data) {
            Player player = Minecraft.getInstance().player;
            Research research = player.getCapability(ResearchCapability.INSTANCE)
                                      .orElseThrow(IllegalStateException::new);

            return research.getAdvancers().stream().map(ResearchAdvancer::getResearchItem)
                           .filter(item -> ForgeRegistries.ITEMS.getKey(item).toString()
                                                                .equals(data.getInputRegistryName())).findFirst();
        }

        private static Optional<ResearchAdvancer> getAdvancerFromData(ResearchDataOld data) {
            Optional<Item> item = getItemFromData(data);
            return item.filter(ResearchAdvancer.class::isInstance).map(ResearchAdvancer.class::cast);
        }
    }

    public class ResearcherScrollPanel extends ScrollPanel {
        private int amountPerRow;
        private TriConsumer<PoseStack, Integer, Integer> hoveredRenderTooltip;
        private static final TriConsumer<PoseStack, Integer, Integer> NOOP = (poseStack, integer, integer2) -> {
        };

        public ResearcherScrollPanel(Minecraft client, int x, int y, int width, int height) {
            super(client, width, height, y, x);
            update();
        }

        @Override
        protected int getContentHeight() {
            if (this.amountPerRow == 0) return 0;
            int height = (ResearcherScreen.this.researchData.size() * 18) / this.amountPerRow;
            if (height < this.height) height = this.height;

            return height;
        }

        @Override
        protected int getScrollAmount() {
            return 22;
        }

        @Override
        protected void drawPanel(PoseStack poseStack, int entryRight, int relativeY, Tesselator tess, int mouseX,
                                 int mouseY) {
            this.hoveredRenderTooltip = NOOP;

            for (ResearchOption researchOption : ResearcherScreen.this.researchOptions) {
                researchOption.y += relativeY;
                researchOption.render(poseStack, mouseX, mouseY, 0);
                if (researchOption.isHovered()) {
                    this.hoveredRenderTooltip = researchOption::renderToolTip;
                }

                researchOption.y -= relativeY;
            }
        }

        @Override
        public void render(PoseStack matrix, int mouseX, int mouseY, float partialTick) {
            super.render(matrix, mouseX, mouseY, partialTick);

            this.hoveredRenderTooltip.accept(matrix, mouseX, mouseY);
        }

        @Override
        public NarrationPriority narrationPriority() {
            return NarrationPriority.NONE;
        }

        @Override
        public void updateNarration(NarrationElementOutput pNarrationElementOutput) {
        }

        public void update() {
            final int widgetSize = 18;
            final int widgetSpacing = 2;

            int column = 0;
            int row = 0;

            for (ResearchOption widget : ResearcherScreen.this.researchOptions) {
                int x = this.left + (column++ * (widgetSize + widgetSpacing)) + widgetSpacing;
                if (x + widgetSize + widgetSpacing > this.left + this.width - 8) {
                    column = 0;
                    x = this.left + widgetSpacing;
                    row++;
                }

                int y = (row * (widgetSize + widgetSpacing)) + widgetSpacing;
                widget.x = x;
                widget.y = y;

                if (column > this.amountPerRow) this.amountPerRow = column;
            }
        }
    }

    public class StartButton extends ExtendedButton {
        private static final Component COMPONENT = Component.translatable("gui.button." + Industria.MODID + ".start");

        public StartButton(int xPos, int yPos) {
            super(xPos, yPos, 30, 16, COMPONENT, btn -> ResearcherScreen.this.startResearch());

            this.active = false;
        }
    }
}
