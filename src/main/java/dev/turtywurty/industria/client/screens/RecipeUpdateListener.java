package dev.turtywurty.industria.client.screens;

import net.minecraft.world.Container;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;

import java.util.Collection;

public interface RecipeUpdateListener<T extends Recipe<Container>> {
    RecipeType<T> getRecipeType();

    void setRecipes(Collection<T> recipes);
    void onRecipeAdded(T recipe);
    void onRecipeRemoved(T recipe);
    void onClearRecipes();
    void onRecipeUpdated(T original, T updated);
}
