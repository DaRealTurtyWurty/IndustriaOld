package dev.turtywurty.industria.init;

import dev.turtywurty.industria.Industria;
import dev.turtywurty.industria.recipes.BiomassGeneratorRecipe;
import dev.turtywurty.industria.recipes.CrusherRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class RecipeInit {
    public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister.create(
            ForgeRegistries.RECIPE_SERIALIZERS, Industria.MODID);
    public static final DeferredRegister<RecipeType<?>> RECIPE_TYPES = DeferredRegister.create(
            ForgeRegistries.RECIPE_TYPES, Industria.MODID);

    public static final RegistryObject<RecipeSerializer<CrusherRecipe>> CRUSHER_SERIALIZER = RECIPE_SERIALIZERS
            .register("crusher", () -> CrusherRecipe.Serializer.INSTANCE);
    public static final RegistryObject<RecipeType<CrusherRecipe>> CRUSHER_TYPE = RECIPE_TYPES
            .register("crusher", () -> CrusherRecipe.Type.INSTANCE);

    public static final RegistryObject<RecipeSerializer<BiomassGeneratorRecipe>> BIOMASS_GENERATOR_SERIALIZER = RECIPE_SERIALIZERS
            .register("biomass_generator", () -> BiomassGeneratorRecipe.Serializer.INSTANCE);
    public static final RegistryObject<RecipeType<BiomassGeneratorRecipe>> BIOMASS_GENERATOR_TYPE = RECIPE_TYPES
            .register("biomass_generator", () -> BiomassGeneratorRecipe.Type.INSTANCE);
}
