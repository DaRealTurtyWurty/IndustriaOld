package dev.turtywurty.industria.items;

import com.mojang.datafixers.util.Either;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;

import java.util.function.Supplier;

public interface ResearchAdvancer {
    String getResearchTitle();
    String getResearchDescription();
    Either<Supplier<ItemStack>, ResourceLocation> getResearchIcon();
    Rarity getResearchRarity();
    Item getResearchItem();
}
