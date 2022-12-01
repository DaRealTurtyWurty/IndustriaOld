package dev.turtywurty.industria.recipes;

import com.google.gson.JsonObject;
import dev.turtywurty.industria.init.RecipeInit;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.wrapper.RecipeWrapper;
import org.jetbrains.annotations.Nullable;

public class BiomassGeneratorRecipe implements Recipe<Container> {
    public static final String ID = "biomass_generator";

    private final ResourceLocation id;
    private final Ingredient input;
    private final int energy;


    public BiomassGeneratorRecipe(ResourceLocation id, Ingredient input, int energy) {
        this.id = id;
        this.input = input;
        this.energy = energy;
    }

    public boolean matches(IItemHandlerModifiable container, Level level) {
        return matches(new RecipeWrapper(container), level);
    }

    @Override
    public boolean matches(Container pContainer, Level pLevel) {
        if (pLevel.isClientSide()) {
            return false;
        }

        return this.input.test(pContainer.getItem(0));
    }

    @Override
    public ItemStack assemble(Container pContainer) {
        pContainer.removeItem(0, 1);
        return getResultItem();
    }

    @Override
    public boolean canCraftInDimensions(int pWidth, int pHeight) {
        return true;
    }

    @Override
    public ItemStack getResultItem() {
        return ItemStack.EMPTY;
    }

    @Override
    public ResourceLocation getId() {
        return this.id;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return RecipeInit.BIOMASS_GENERATOR_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return RecipeInit.BIOMASS_GENERATOR_TYPE.get();
    }

    public Ingredient getInput() {
        return this.input;
    }

    public int getEnergy() {
        return this.energy;
    }

    public static final class Type implements RecipeType<BiomassGeneratorRecipe> {
        public static final BiomassGeneratorRecipe.Type INSTANCE = new BiomassGeneratorRecipe.Type();

        private Type() {
        }

        @Override
        public String toString() {
            return BiomassGeneratorRecipe.ID;
        }
    }

    public static final class Serializer implements RecipeSerializer<BiomassGeneratorRecipe> {
        public static final BiomassGeneratorRecipe.Serializer INSTANCE = new BiomassGeneratorRecipe.Serializer();

        private Serializer() {
        }

        @Override
        public BiomassGeneratorRecipe fromJson(ResourceLocation pRecipeId, JsonObject pSerializedRecipe) {
            Ingredient input = Ingredient.fromJson(GsonHelper.getAsJsonObject(pSerializedRecipe, "input"));
            int energy = GsonHelper.getAsInt(pSerializedRecipe, "energy");

            return new BiomassGeneratorRecipe(pRecipeId, input, energy);
        }

        @Override
        public @Nullable BiomassGeneratorRecipe fromNetwork(ResourceLocation pRecipeId, FriendlyByteBuf pBuffer) {
            Ingredient input = Ingredient.fromNetwork(pBuffer);
            int energy = pBuffer.readInt();

            return new BiomassGeneratorRecipe(pRecipeId, input, energy);
        }

        @Override
        public void toNetwork(FriendlyByteBuf pBuffer, BiomassGeneratorRecipe pRecipe) {
            pRecipe.getInput().toNetwork(pBuffer);
            pBuffer.writeInt(pRecipe.getEnergy());
        }
    }
}