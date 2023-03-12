package dev.turtywurty.industria.client.util;

import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import org.apache.commons.lang3.ArrayUtils;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

public class SimpleButton extends AbstractWidget {
    private RenderableButtonIcon icon;
    private Function<SimpleButton, Collection<Component>> tooltip = button -> List.of();
    private Optional<Component> narration = Optional.empty();
    private Function<SimpleButton, Boolean> clickedHandler = button -> true;

    private SimpleButton(int pX, int pY, int pWidth, int pHeight) {
        super(pX, pY, pWidth, pHeight, Component.empty());
    }

    @Override
    public void updateNarration(NarrationElementOutput pNarrationElementOutput) {
        defaultButtonNarrationText(pNarrationElementOutput);
        pNarrationElementOutput.add(NarratedElementType.HINT, this.narration.map(ArrayUtils::toArray)
                .orElseGet(() -> this.tooltip.apply(this).toArray(Component[]::new)));
    }

    public void setIcon(RenderableButtonIcon icon) {
        this.icon = icon;
    }

    public void setTooltip(Function<SimpleButton, Collection<Component>> tooltip) {
        this.tooltip = Objects.requireNonNullElseGet(tooltip, () -> button -> List.of());
    }

    public void setNarration(Component narration) {
        this.narration = Optional.ofNullable(narration);
    }

    public void setClickedHandler(Function<SimpleButton, Boolean> clickedHandler) {
        this.clickedHandler = Objects.requireNonNullElseGet(clickedHandler, () -> button -> true);
    }

    @Override
    protected boolean clicked(double pMouseX, double pMouseY) {
        if (!this.active || !this.isHovered) return false;

        return this.clickedHandler.apply(this);
    }

    public static class Builder {
        private final int x, y;
        private int width, height;
        private RenderableButtonIcon icon;
        private Function<SimpleButton, Collection<Component>> tooltip;
        private Optional<Component> narration = Optional.empty();
        private Function<SimpleButton, Boolean> onClickHandler;

        public Builder(int x, int y) {
            this.x = x;
            this.y = y;
        }

        public Builder size(int width, int height) {
            this.width = width;
            this.height = height;
            return this;
        }

        public Builder size(int size) {
            this.width = size;
            this.height = size;
            return this;
        }

        public Builder icon(RenderableButtonIcon icon) {
            this.icon = icon;
            return this;
        }

        public Builder tooltip(Function<SimpleButton, Collection<Component>> tooltip) {
            this.tooltip = tooltip;
            return this;
        }

        public Builder tooltip(Component... tooltip) {
            this.tooltip = button -> List.of(tooltip);
            return this;
        }

        public Builder narration(Component narration) {
            this.narration = Optional.ofNullable(narration);
            return this;
        }

        public Builder onClick(Function<SimpleButton, Boolean> clickedHandler) {
            this.onClickHandler = clickedHandler;
            return this;
        }

        public Builder onClick(Runnable clickedHandler) {
            this.onClickHandler = button -> {
                clickedHandler.run();
                return true;
            };

            return this;
        }

        public SimpleButton build() {
            SimpleButton button = new SimpleButton(this.x, this.y, this.width, this.height);
            if (icon != null) button.setIcon(icon);
            if (tooltip != null) button.setTooltip(tooltip);
            narration.ifPresent(button::setNarration);
            if (onClickHandler != null) button.setClickedHandler(onClickHandler);

            return button;
        }
    }
}
