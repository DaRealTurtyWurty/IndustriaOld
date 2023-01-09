package dev.turtywurty.industria.client.screens;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import dev.turtywurty.industria.Industria;
import dev.turtywurty.industria.menu.AgitatorMenu;
import dev.turtywurty.industria.menu.slots.ToggleSlot;
import dev.turtywurty.industria.menu.slots.ToggleSlotItemHandler;
import io.github.darealturtywurty.turtylib.client.ui.components.FluidWidget;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.fluids.FluidStack;
import org.apache.commons.lang3.tuple.Pair;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class AgitatorScreen extends AbstractContainerScreen<AgitatorMenu> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(Industria.MODID, "textures/gui/agitator.png");

    private SwitchingWidget switchingWidget0, switchingWidget1, switchingWidget2, switchingWidget3, switchingWidget4, switchingWidget5;
    private FluidWidget fluidWidget0, fluidWidget1, fluidWidget2, fluidWidget3, fluidWidget4, fluidWidget5;

    public AgitatorScreen(AgitatorMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
        this.imageWidth = 176;
        this.imageHeight = 227;
        this.inventoryLabelY = this.imageHeight - 94;
    }

    @Override
    protected void renderBg(PoseStack pPoseStack, float pPartialTick, int pMouseX, int pMouseY) {
        renderBackground(pPoseStack);
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

    @Override
    protected void init() {
        super.init();

        this.switchingWidget0 = addRenderableWidget(
                new SwitchingWidget(this.menu, this.leftPos + 10, this.topPos + 90, 20, 20));
        this.switchingWidget1 = addRenderableWidget(
                new SwitchingWidget(this.menu, this.leftPos + 35, this.topPos + 90, 20, 20));
        this.switchingWidget2 = addRenderableWidget(
                new SwitchingWidget(this.menu, this.leftPos + 60, this.topPos + 90, 20, 20));
        this.switchingWidget3 = addRenderableWidget(
                new SwitchingWidget(this.menu, this.leftPos + 85, this.topPos + 90, 20, 20));
        this.switchingWidget4 = addRenderableWidget(
                new SwitchingWidget(this.menu, this.leftPos + 110, this.topPos + 90, 20, 20));
        this.switchingWidget5 = addRenderableWidget(
                new SwitchingWidget(this.menu, this.leftPos + 135, this.topPos + 90, 20, 20));

        this.fluidWidget0 = addRenderableWidget(
                new FluidWidget(new FluidStack(Fluids.WATER, 500), 0, 0, 16, 47, 10, 10));
        this.fluidWidget1 = addRenderableWidget(new FluidWidget(FluidStack.EMPTY, 0, 0, 16, 47, 10, 10));
        this.fluidWidget2 = addRenderableWidget(new FluidWidget(FluidStack.EMPTY, 0, 0, 16, 47, 10, 10));
        this.fluidWidget3 = addRenderableWidget(new FluidWidget(FluidStack.EMPTY, 0, 0, 16, 47, 10, 10));
        this.fluidWidget4 = addRenderableWidget(new FluidWidget(FluidStack.EMPTY, 0, 0, 16, 47, 10, 10));
        this.fluidWidget5 = addRenderableWidget(new FluidWidget(FluidStack.EMPTY, 0, 0, 16, 47, 10, 10));

        this.switchingWidget0.addSlot(SwitchingWidget.IOType.ITEM, 0);
        this.switchingWidget0.addFluid(SwitchingWidget.IOType.FLUID, this.fluidWidget0);

        this.switchingWidget1.addSlot(SwitchingWidget.IOType.ITEM, 1);
        this.switchingWidget1.addFluid(SwitchingWidget.IOType.FLUID, this.fluidWidget1);

        this.switchingWidget2.addSlot(SwitchingWidget.IOType.ITEM, 2);
        this.switchingWidget2.addFluid(SwitchingWidget.IOType.FLUID, this.fluidWidget2);

        this.switchingWidget3.addSlot(SwitchingWidget.IOType.ITEM, 3);
        this.switchingWidget3.addFluid(SwitchingWidget.IOType.FLUID, this.fluidWidget3);

        this.switchingWidget4.addSlot(SwitchingWidget.IOType.ITEM, 4);
        this.switchingWidget4.addFluid(SwitchingWidget.IOType.FLUID, this.fluidWidget4);

        this.switchingWidget5.addSlot(SwitchingWidget.IOType.ITEM, 5);
        this.switchingWidget5.addFluid(SwitchingWidget.IOType.FLUID, this.fluidWidget5);
    }

    public class SwitchingWidget extends AbstractWidget {
        private final Map<IOType, Object> values = new HashMap<>();
        private final AgitatorMenu menu;

        private Pair<IOType, Object> current;

        public SwitchingWidget(AgitatorMenu menu, int pX, int pY, int pWidth, int pHeight) {
            super(pX, pY, pWidth, pHeight, Component.empty());

            this.menu = menu;
        }

        public boolean addFluid(IOType type, FluidWidget widget) {
            boolean currentEmpty = isCurrentEmpty();
            if (isFluidEmpty(widget) || currentEmpty) {
                widget.setShouldDrawBorder(true);
                widget.x = this.x + 2;
                widget.y = this.y - 60;

                if (currentEmpty) {
                    widget.active = true;
                    this.current = Pair.of(type, widget);
                }

                disableNonCurrent();

                this.values.put(type, widget);
                return true;
            }

            // here the fluid is not empty and the current is not empty. in this circumstance we refuse to add the fluid widget
            return false;
        }

        public boolean addSlot(IOType type, int slotIndex) {
            boolean currentEmpty = isCurrentEmpty();
            if (isSlotEmpty(slotIndex) || currentEmpty) {
                Slot slot = this.menu.getSlot(slotIndex);
                slot.x = this.x - AgitatorScreen.this.leftPos + 2;
                slot.y = this.y - AgitatorScreen.this.topPos - 39;

                if (currentEmpty) {
                    if (slot instanceof ToggleSlot toggleSlot) {
                        toggleSlot.setActive(true);
                    } else if (slot instanceof ToggleSlotItemHandler toggleSlotItemHandler) {
                        toggleSlotItemHandler.setActive(true);
                    } else {
                        throw new IllegalStateException("Slot must be a ToggleSlot or ToggleSlotItemHandler!");
                    }

                    this.current = Pair.of(type, slotIndex);
                }

                disableNonCurrent();

                this.values.put(type, slotIndex);
                return true;
            }

            // here the slot is not empty and the current is not empty. in this circumstance we refuse to add the slot
            return false;
        }

        private void disableNonCurrent() {
            for (final Object value : this.values.values()) {
                if (value instanceof FluidWidget fluidWidget) {
                    if (Objects.equals(fluidWidget, this.current.getRight())) continue;

                    fluidWidget.active = false;
                } else if (value instanceof Integer slotIndex) {
                    if (Objects.equals(slotIndex, this.current.getRight())) continue;

                    final Slot slot = this.menu.slots.get(slotIndex);
                    if (slot instanceof ToggleSlot toggleSlot) {
                        toggleSlot.setActive(false);
                    } else if (slot instanceof ToggleSlotItemHandler toggleSlotItemHandler) {
                        toggleSlotItemHandler.setActive(false);
                    } else {
                        Industria.LOGGER.error("Slot is not a ToggleSlot or ToggleSlotItemHandler!");
                    }
                }
            }
        }

        private boolean isFluidEmpty(FluidWidget widget) {
            return widget.getInfo().getFluidStack() == null || widget.getInfo().getFluidStack().isEmpty();
        }

        private boolean isCurrentFluidEmpty() {
            return !(this.current.getRight() instanceof FluidWidget fluidWidget) || isFluidEmpty(fluidWidget);
        }

        private boolean isSlotEmpty(int slotIndex) {
            return slotIndex >= 0 && slotIndex < this.menu.slots.size() && this.menu.getSlot(slotIndex).getItem()
                    .isEmpty();
        }

        private boolean isCurrentItemEmpty() {
            if (this.current.getRight() instanceof Integer slotIndex && isSlotEmpty(slotIndex)) return true;
            return !(this.current.getRight() instanceof Integer);
        }

        // TODO: Gas Widget
        private boolean isCurrentEmpty() {
            return (this.current == null || this.current.getRight() == null) || (isCurrentFluidEmpty() && isCurrentItemEmpty()); /* || isCurrentGasEmpty();*/
        }

        // TODO: Gas Widget
        //public void addGas(IOType type, GasWidget widget) {
        //    this.values.put(type, widget);
        //}

        @Override
        public void updateNarration(NarrationElementOutput pNarrationElementOutput) {
            defaultButtonNarrationText(pNarrationElementOutput);
        }

        private boolean firstRender = true;

        @Override
        public void render(PoseStack pPoseStack, int pMouseX, int pMouseY, float pPartialTick) {
            if (!this.visible) return;
            if (this.active) {
                if(firstRender) {
                    this.firstRender = false;
                    this.values.forEach((type, object) -> {
                        // check to see if the object is empty
                        if(object instanceof FluidWidget fluidWidget) {
                            if(!isFluidEmpty(fluidWidget)) {
                                this.current = Pair.of(type, fluidWidget);
                                fluidWidget.active = true;
                                fluidWidget.setShouldDrawBorder(true);
                                fluidWidget.x = this.x + 2;
                                fluidWidget.y = this.y - 60;
                                this.active = false;
                                disableNonCurrent();
                            }
                        } else if(object instanceof Integer slotIndex) {
                            if(!isSlotEmpty(slotIndex)) {
                                this.current = Pair.of(type, slotIndex);
                                final Slot slot = this.menu.slots.get(slotIndex);
                                if (slot instanceof ToggleSlot toggleSlot) {
                                    toggleSlot.setActive(true);
                                } else if (slot instanceof ToggleSlotItemHandler toggleSlotItemHandler) {
                                    toggleSlotItemHandler.setActive(true);
                                } else {
                                    Industria.LOGGER.error("Slot is not a ToggleSlot or ToggleSlotItemHandler!");
                                }

                                slot.x = this.x - AgitatorScreen.this.leftPos + 2;
                                slot.y = this.y - AgitatorScreen.this.topPos - 39;
                                this.active = false;
                                disableNonCurrent();
                            }
                        }
                    });
                }

                if (!isCurrentEmpty()) {
                    this.active = false;
                    return;
                }
            } else {
                if (isCurrentEmpty()) {
                    this.active = true;
                    return;
                }
            }

            if (this.current == null) return;

            if (!this.values.containsKey(this.current.getLeft())) {
                Industria.LOGGER.error(
                        "SwitchingWidget does not contain a value for type " + this.current.getLeft().name());
                return;
            }

            // Render button
            this.isHovered = pMouseX >= this.x && pMouseY >= this.y && pMouseX < this.x + this.width && pMouseY < this.y + this.height;

            RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
            RenderSystem.setShaderTexture(0, TEXTURE);
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            int yChange = getYImage(this.isHovered) * this.height;
            blit(pPoseStack, this.x, this.y, 194, yChange, this.width, this.height);

            if (this.isHovered) {
                this.renderToolTip(pPoseStack, pMouseX, pMouseY);
            }

            // Render current value
            Object value = this.values.get(this.current.getLeft());
            if (value instanceof Integer slotIndex) {
                Slot slot = this.menu.slots.get(slotIndex);
                if (slot.isActive()) {
                    blit(pPoseStack, this.x + 1, this.y - 40, 176, 0, 18, 18);
                }
            }
        }

        public int getYImage(boolean pIsHovered) {
            int i = 1;
            if (!this.active) {
                i = 0;
            } else if (pIsHovered) {
                i = 2;
            }

            return i;
        }

        @Override
        public void onClick(double pMouseX, double pMouseY) {
            next();
        }

        public void next(boolean reverse) {
            if (!isCurrentEmpty()) {
                this.active = false;
                return;
            }

            if (this.values.isEmpty()) {
                this.current = null;
                return;
            }

            if (this.current == null) {
                Pair<IOType, Object> replacement = this.values.entrySet().stream().findFirst()
                        .map(entry -> Pair.of(entry.getKey(), entry.getValue())).orElse(null);
                if (handleToggle(replacement)) {
                    this.current = replacement;
                }

                return;
            }

            int iterations = 0;
            IOType nextType = reverse ? this.current.getLeft().previous() : this.current.getLeft().next();
            while (!this.values.containsKey(nextType)) {
                if (iterations > this.values.size()) {
                    Industria.LOGGER.error("SwitchingWidget is stuck in an infinite loop!");
                    return;
                }

                nextType = nextType.next();
                iterations++;
            }

            Pair<IOType, Object> replacement = Pair.of(nextType, this.values.get(nextType));
            if (handleToggle(replacement)) {
                this.current = replacement;
            }
        }

        public void next() {
            next(false);
        }

        // TODO: Gas Widget
        private boolean handleToggle(Pair<IOType, Object> replacement) {
            boolean changed = true;
            Object oldVal = this.current.getRight();
            if (oldVal instanceof Integer oldSlotIndex) {
                Slot slot = this.menu.getSlot(oldSlotIndex);
                if (!slot.hasItem()) {
                    if (slot instanceof ToggleSlot toggleSlot && toggleSlot.isActive()) {
                        toggleSlot.setActive(false);
                    } else if (slot instanceof ToggleSlotItemHandler toggleSlot && toggleSlot.isActive()) {
                        toggleSlot.setActive(false);
                    }
                } else {
                    changed = false;
                }
            } else if (oldVal instanceof FluidWidget oldWidget && oldWidget.isActive()) {
                if (oldWidget.getInfo().getFluidStack() == null || oldWidget.getInfo().getFluidStack().isEmpty()) {
                    oldWidget.active = false;
                } else {
                    changed = false;
                }
            } else {
                changed = false;
            }

            if (!changed || replacement == null || Objects.equals(oldVal, replacement.getRight())) {
                return false;
            }

            Object newVal = replacement.getRight();
            if (newVal instanceof Integer newSlotIndex) {
                Slot slot = this.menu.getSlot(newSlotIndex);
                if (slot instanceof ToggleSlot toggleSlot && !toggleSlot.isActive()) {
                    toggleSlot.setActive(true);
                } else if (slot instanceof ToggleSlotItemHandler toggleSlot && !toggleSlot.isActive()) {
                    toggleSlot.setActive(true);
                }
            } else if (newVal instanceof FluidWidget newWidget && !newWidget.isActive()) {
                newWidget.active = true;
            }

            return true;
        }

        // TODO: Gas
        public enum IOType {
            ITEM, FLUID;

            public IOType next() {
                return values()[(this.ordinal() + 1) % values().length];
            }

            public IOType previous() {
                return values()[(this.ordinal() - 1) % values().length];
            }
        }
    }
}
