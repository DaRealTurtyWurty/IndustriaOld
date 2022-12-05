package dev.turtywurty.industria.client.screens;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import dev.turtywurty.industria.Industria;
import dev.turtywurty.industria.capability.Research;
import dev.turtywurty.industria.capability.ResearchCapability;
import dev.turtywurty.industria.client.util.ObservableList;
import dev.turtywurty.industria.data.ResearchData;
import dev.turtywurty.industria.items.ResearchAdvancer;
import dev.turtywurty.industria.menu.ResearcherMenu;
import dev.turtywurty.industria.network.PacketManager;
import dev.turtywurty.industria.network.serverbound.SRequestResearchDataPacket;
import io.github.darealturtywurty.turtylib.client.ui.components.EnergyWidget;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraftforge.client.gui.widget.ScrollPanel;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public class ResearcherScreen extends AbstractContainerScreen<ResearcherMenu> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(Industria.MODID,
            "textures/gui/researcher.png");
    private ResearcherScrollPanel scrollPanel;

    public final List<ResearchData> researchData = new ArrayList<>();
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
                new ResearcherScrollPanel(this.minecraft, this.leftPos + 30, this.topPos + 14, 136, 60));

        List<ResearchOption> options = new ArrayList<>();
        for (ResearchData data : this.researchData) {
            var widget = new ResearchOption(0, 0, data);
            options.add(addWidget(widget));
            ;
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

    public class ResearchOption extends AbstractWidget {
        private static final ResourceLocation TEXTURE = new ResourceLocation(Industria.MODID,
                "textures/gui/research_option.png");
        private final ResearchData data;

        public ResearchOption(int pX, int pY, ResearchData data) {
            super(pX, pY, 18, 18, Component.empty());
            this.data = data;
        }

        @Override
        public void updateNarration(NarrationElementOutput pNarrationElementOutput) {
            defaultButtonNarrationText(pNarrationElementOutput);
        }

        private void renderButton(PoseStack poseStack) {
            RenderSystem.setShader(GameRenderer::getPositionTexShader);
            RenderSystem.setShaderTexture(0, WIDGETS_LOCATION);
            int hoverDifference = getYImage(isHoveredOrFocused());
            blit(poseStack, this.x, this.y, 0, 46 + hoverDifference * 20, this.width / 2, this.height);
            blit(poseStack, this.x + this.width / 2, this.y, 200 - this.width / 2, 46 + hoverDifference * 20,
                    this.width / 2, this.height);
        }

        @Override
        public void render(PoseStack pPoseStack, int pMouseX, int pMouseY, float pPartialTick) {
            renderButton(pPoseStack);

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
                fill(pPoseStack, this.x, this.y, this.x + this.width, this.y + this.height, 0x80FFFF00);
            }

            this.isHovered = isMouseOver(pMouseX, pMouseY);
        }

        private static boolean hasResearched(ResearchData data) {
            return getItemFromData(data).isPresent();
        }

        private static Optional<Item> getItemFromData(ResearchData data) {
            Player player = Minecraft.getInstance().player;
            Research research = player.getCapability(ResearchCapability.INSTANCE)
                    .orElseThrow(IllegalStateException::new);

            return research.getAdvancers().stream().map(ResearchAdvancer::getResearchItem)
                    .filter(item -> ForgeRegistries.ITEMS.getKey(item).toString().equals(data.getInputRegistryName()))
                    .findFirst();
        }

        private static Optional<ResearchAdvancer> getAdvancerFromData(ResearchData data) {
            Optional<Item> item = getItemFromData(data);
            return item.filter(ResearchAdvancer.class::isInstance).map(ResearchAdvancer.class::cast);
        }
    }

    public class ResearcherScrollPanel extends ScrollPanel {
        public ResearcherScrollPanel(Minecraft client, int x, int y, int width, int height) {
            super(client, width, height, y, x);
            update();
        }

        @Override
        protected int getContentHeight() {
            int height = 0;
            height += (ResearcherScreen.this.researchData.size() * 18) / 6;
            if (height < this.height) height = this.height;

            return height;
        }

        @Override
        protected int getScrollAmount() {
            return 22;
        }

        @Override
        protected void drawPanel(PoseStack poseStack, int entryRight, int relativeY, Tesselator tess, int mouseX, int mouseY) {
            for (ResearchOption researchOption : ResearcherScreen.this.researchOptions) {
                researchOption.y += relativeY;
                researchOption.render(poseStack, mouseX, mouseY, 0);

                researchOption.y -= relativeY;
            }
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

            for (int index = 0; index < ResearcherScreen.this.researchOptions.size(); index++) {
                var widget = ResearcherScreen.this.researchOptions.get(index);
                int x = this.left + (column++ * (widgetSize + widgetSpacing)) + widgetSpacing;
                if (x + widgetSize + widgetSpacing > this.left + this.width - 8) {
                    column = 0;
                    x = this.left + widgetSize + widgetSpacing;
                    row++;
                }

                int y = (row * (widgetSize + widgetSpacing)) + widgetSpacing;
                widget.x = x;
                widget.y = y;
            }
        }
    }
}
