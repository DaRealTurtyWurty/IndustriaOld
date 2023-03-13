package dev.turtywurty.industria.registry;

import com.mojang.datafixers.util.Either;
import dev.turtywurty.turtylib.core.multiblock.Multiblock;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Rarity;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public class ResearchData {
    private final Supplier<Item> input;
    private final int requiredEnergy;
    private final Either<Supplier<ItemStack>, RegistryObject<Multiblock>> result;
    private final String title;
    private final String description;
    private final Either<Supplier<ItemStack>, ResourceLocation> icon;
    private final Rarity rarity;

    public ResearchData(Builder builder) {
        this.input = builder.input;
        this.requiredEnergy = builder.requiredEnergy;
        this.result = builder.result;
        this.title = builder.title;
        this.description = builder.description;
        this.icon = builder.icon;
        this.rarity = builder.rarity;
    }

    public static class Builder {
        private Supplier<Item> input;
        private int requiredEnergy;
        private Either<Supplier<ItemStack>, RegistryObject<Multiblock>> result;
        private String title;
        private String description;
        private Either<Supplier<ItemStack>, ResourceLocation> icon;
        private Rarity rarity;

        public Builder input(Supplier<Item> input) {
            this.input = input;
            return this;
        }

        public Builder requiredEnergy(int requiredEnergy) {
            this.requiredEnergy = requiredEnergy;
            return this;
        }

        public Builder result(Supplier<ItemStack> result) {
            this.result = Either.left(result);
            return this;
        }

        public Builder result(RegistryObject<Multiblock> result) {
            this.result = Either.right(result);
            return this;
        }

        public Builder title(String title) {
            this.title = title;
            return this;
        }

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public Builder icon(Supplier<ItemStack> icon) {
            this.icon = Either.left(icon);
            return this;
        }

        public Builder icon(ResourceLocation icon) {
            this.icon = Either.right(icon);
            return this;
        }

        public Builder rarity(Rarity rarity) {
            this.rarity = rarity;
            return this;
        }
    }
}
