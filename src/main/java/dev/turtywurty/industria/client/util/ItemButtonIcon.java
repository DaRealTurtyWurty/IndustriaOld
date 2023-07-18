package dev.turtywurty.industria.client.util;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.datafixers.util.Either;
import net.minecraft.client.Minecraft;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.enchantment.Enchantments;
import org.jetbrains.annotations.NotNull;

public class ItemButtonIcon implements RenderableButtonIcon {
    private final SimpleButton self;
    private final Either<ItemStack, Ingredient> item;
    private final int x, y;
    private final boolean enchantGlint;

    private int frame = 0;

    private ItemButtonIcon(SimpleButton self, Either<ItemStack, Ingredient> item, int x, int y, boolean enchantGlint) {
        this.self = self;
        this.item = item;
        this.x = x;
        this.y = y;
        this.enchantGlint = enchantGlint;
    }

    @Override
    public void render(PoseStack poseStack, double mouseX, double mouseY, float partialTicks) {
        this.item.ifLeft(stack -> Minecraft.getInstance().getItemRenderer().renderGuiItem(poseStack, stack, this.x, this.y))
                .ifRight(ingredient -> {
                    ItemStack stack = ingredient.getItems()[this.frame++ % ingredient.getItems().length];
                    if (this.enchantGlint && !stack.isEmpty()) {
                        stack = stack.copy();
                        stack.enchant(Enchantments.BINDING_CURSE, 1);
                    }

                    Minecraft.getInstance().getItemRenderer().renderGuiItem(poseStack, stack, this.x, this.y);
                });
    }

    public static class Builder {
        private Either<ItemStack, Ingredient> item = Either.left(ItemStack.EMPTY);
        private final SimpleButton self;
        private int x, y;
        private boolean enchantGlint = false;

        public Builder(@NotNull SimpleButton self) {
            this.self = self;
            this.x = self.getX();
            this.y = self.getY();
        }

        public Builder item(@NotNull ItemStack stack) {
            this.item = Either.left(stack);
            return this;
        }

        public Builder item(@NotNull Item item) {
            this.item = Either.left(item.getDefaultInstance());
            return this;
        }

        public Builder item(@NotNull Ingredient ingredient) {
            this.item = Either.right(ingredient);
            return this;
        }

        public Builder item(@NotNull TagKey<Item> tag) {
            this.item = Either.right(Ingredient.of(tag));
            return this;
        }

        public Builder item(@NotNull Item... items) {
            this.item = Either.right(Ingredient.of(items));
            return this;
        }

        public Builder item(@NotNull ItemStack... stacks) {
            this.item = Either.right(Ingredient.of(stacks));
            return this;
        }

        public Builder x(int x) {
            this.x = x;
            return this;
        }

        public Builder y(int y) {
            this.y = y;
            return this;
        }

        public Builder enchantGlint(boolean enchantGlint) {
            this.enchantGlint = enchantGlint;
            return this;
        }

        public ItemButtonIcon build() {
            return new ItemButtonIcon(this.self, this.item, this.x, this.y,
                    this.item.left().isEmpty() && this.enchantGlint);
        }
    }
}
