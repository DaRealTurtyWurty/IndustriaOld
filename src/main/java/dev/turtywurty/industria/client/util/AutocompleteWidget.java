package dev.turtywurty.industria.client.util;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class AutocompleteWidget<T> extends EditBox {
    private final List<T> suggestions = new ArrayList<>();
    private final Function<String, List<T>> suggestionProvider;
    private final Function<T, Component> suggestionRenderer;
    private final int maxSuggestions, suggestionHeight;
    private final int suggestionBackgroundColor, suggestionTextColor;

    private AutocompleteWidget(int pX, int pY, int pWidth, int pHeight, int maxSuggestions,
                               Function<String, List<T>> suggestionProvider, Function<T, Component> suggestionRenderer,
                               int suggestionHeight, int suggestionBackgroundColor, int suggestionTextColor) {
        super(Minecraft.getInstance().font, pX, pY, pWidth, pHeight, Component.empty());
        this.maxSuggestions = maxSuggestions;
        this.suggestionProvider = suggestionProvider;
        this.suggestionRenderer = suggestionRenderer;
        this.suggestionHeight = suggestionHeight;
        this.suggestionBackgroundColor = suggestionBackgroundColor;
        this.suggestionTextColor = suggestionTextColor;

        setResponder(this::onTextChanged);
    }

    private void onTextChanged(String text) {
        this.suggestions.clear();
        this.suggestions.addAll(this.suggestionProvider.apply(text));
    }

    @Override
    public void render(PoseStack pPoseStack, int pMouseX, int pMouseY, float pPartialTick) {
        super.render(pPoseStack, pMouseX, pMouseY, pPartialTick);

        if (this.suggestions.isEmpty()) return;

        for (int index = 0; index < Math.min(this.suggestions.size(), this.maxSuggestions); index++) {
            T suggestion = this.suggestions.get(index);
            if (suggestion == null) continue;

            GuiComponent.fill(pPoseStack, getX(), getY() + this.height + index * this.suggestionHeight,
                    getX() + this.width, getY() + this.height + (index + 1) * this.suggestionHeight,
                    this.suggestionBackgroundColor);

            Minecraft.getInstance().font.draw(pPoseStack, this.suggestionRenderer.apply(suggestion), getX() + 2,
                    getY() + 2 + index * this.suggestionHeight, this.suggestionTextColor);
        }
    }

    public static class Builder<T> {
        private final int x, y, width, height;
        private int maxSuggestions = 5, suggestionHeight = 10;
        private Function<String, List<T>> suggestionProvider = s -> new ArrayList<>();
        private Function<T, Component> suggestionRenderer = (str) -> Component.nullToEmpty(str.toString());
        private int suggestionBackgroundColor = 0x88000000, suggestionTextColor = 0xFFFFFFFF;

        public Builder(int x, int y, int width, int height) {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
        }

        public Builder<T> maxSuggestions(int maxSuggestions) {
            this.maxSuggestions = maxSuggestions;
            return this;
        }

        public Builder<T> suggestionHeight(int suggestionHeight) {
            this.suggestionHeight = suggestionHeight;
            return this;
        }

        public Builder<T> suggestionProvider(Function<String, List<T>> suggestionProvider) {
            this.suggestionProvider = suggestionProvider;
            return this;
        }

        public Builder<T> suggestionRenderer(Function<T, Component> suggestionRenderer) {
            this.suggestionRenderer = suggestionRenderer;
            return this;
        }

        public Builder<T> suggestionBackgroundColor(int backgroundColor) {
            this.suggestionBackgroundColor = backgroundColor;
            return this;
        }

        public Builder<T> suggestionTextColor(int textColor) {
            this.suggestionTextColor = textColor;
            return this;
        }

        public AutocompleteWidget<T> build() {
            return new AutocompleteWidget<>(this.x, this.y, this.width, this.height, this.maxSuggestions,
                    this.suggestionProvider, this.suggestionRenderer, this.suggestionHeight, this.suggestionBackgroundColor, this.suggestionTextColor);
        }
    }
}
