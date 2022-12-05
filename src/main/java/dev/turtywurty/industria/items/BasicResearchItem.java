package dev.turtywurty.industria.items;

import com.mojang.datafixers.util.Either;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;

import java.util.function.Supplier;

public class BasicResearchItem extends Item implements ResearchAdvancer {
    private final String title;
    private final String description;
    private final Either<Supplier<ItemStack>, ResourceLocation> icon;
    private final Rarity rarity;

    public BasicResearchItem(Item.Properties properties, String title, String description, Rarity rarity, Supplier<ItemStack> icon) {
        this(properties, title, description, rarity, Either.left(icon));
    }

    public BasicResearchItem(Item.Properties properties, String title, String description, Rarity rarity, ResourceLocation icon) {
        this(properties, title, description, rarity, Either.right(icon));
    }

    protected BasicResearchItem(Item.Properties properties, String title, String description, Rarity rarity, Either<Supplier<ItemStack>, ResourceLocation> icon) {
        super(properties);
        this.title = title;
        this.description = description;
        this.rarity = rarity;
        this.icon = icon;
    }

    @Override
    public String getResearchTitle() {
        return this.title;
    }

    @Override
    public String getResearchDescription() {
        return this.description;
    }

    @Override
    public Either<Supplier<ItemStack>, ResourceLocation> getResearchIcon() {
        return this.icon;
    }

    @Override
    public Rarity getResearchRarity() {
        return this.rarity;
    }

    @Override
    public Item getResearchItem() {
        return this;
    }
}
