package dev.turtywurty.industria.recipes;

import com.google.gson.JsonObject;
import dev.turtywurty.industria.init.RecipeInit;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemHandlerHelper;
import org.jetbrains.annotations.NotNull;

public class CrusherRecipe implements Recipe<Container> {
    public static final String ID = "crusher";

    private final ResourceLocation id;
    private final Ingredient input;
    private final ItemStack output;
    private final int processTime;
    private final int energyCost;


    public CrusherRecipe(ResourceLocation id, Ingredient input, ItemStack output, int processTime, int energyCost) {
        this.id = id;
        this.input = input;
        this.output = output;
        this.processTime = processTime;
        this.energyCost = energyCost;
    }

    public boolean matches(IItemHandlerModifiable input, IItemHandlerModifiable output, Level level) {
        if (level.isClientSide()) return false;


        return this.input.test(input.getStackInSlot(0)) && (output.getStackInSlot(0)
                .isEmpty() || ItemHandlerHelper.canItemStacksStack(output.getStackInSlot(0),
                this.output) && output.getStackInSlot(0).getCount() < output.getStackInSlot(0).getMaxStackSize());
    }

    @Override
    public boolean matches(Container pContainer, Level pLevel) {
        if (pLevel.isClientSide()) {
            return false;
        }

        return this.input.test(pContainer.getItem(0)) && (pContainer.getItem(1).isEmpty() || pContainer.canPlaceItem(1,
                this.output) && pContainer.getItem(1).getCount() < pContainer.getItem(1).getMaxStackSize());
    }

    @Override
    public ItemStack assemble(Container pContainer, RegistryAccess pRegistryAccess) {
        pContainer.removeItem(0, 1);
        return getResultItem(pRegistryAccess);
    }

    @Override
    public boolean canCraftInDimensions(int pWidth, int pHeight) {
        return true;
    }

    @Override
    public ItemStack getResultItem(RegistryAccess pRegistryAccess) {
        return this.output.copy();
    }

    @Override
    public ResourceLocation getId() {
        return this.id;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return RecipeInit.CRUSHER_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return RecipeInit.CRUSHER_TYPE.get();
    }

    public Ingredient getInput() {
        return this.input;
    }

    public int getProcessTime() {
        return this.processTime;
    }

    public int getEnergyCost() {
        return this.energyCost;
    }

    public static final class Type implements RecipeType<CrusherRecipe> {
        public static final Type INSTANCE = new Type();

        private Type() {
        }

        @Override
        public String toString() {
            return CrusherRecipe.ID;
        }
    }

    public static final class Serializer implements RecipeSerializer<CrusherRecipe> {
        public static final Serializer INSTANCE = new Serializer();

        private Serializer() {
        }

        @Override
        public CrusherRecipe fromJson(ResourceLocation pRecipeId, JsonObject pSerializedRecipe) {
            ItemStack output = ShapedRecipe.itemStackFromJson(GsonHelper.getAsJsonObject(pSerializedRecipe, "output"));
            Ingredient input = Ingredient.fromJson(GsonHelper.getAsJsonObject(pSerializedRecipe, "input"));
            int processTime = GsonHelper.getAsInt(pSerializedRecipe, "processTime");
            int energyCost = GsonHelper.getAsInt(pSerializedRecipe, "energyCost");

            return new CrusherRecipe(pRecipeId, input, output, processTime, energyCost);
        }

        @Override
        public @NotNull CrusherRecipe fromNetwork(ResourceLocation pRecipeId, FriendlyByteBuf pBuffer) {
            ItemStack output = pBuffer.readItem();
            Ingredient input = Ingredient.fromNetwork(pBuffer);
            int processTime = pBuffer.readInt();
            int energyCost = pBuffer.readInt();

            return new CrusherRecipe(pRecipeId, input, output, processTime, energyCost);
        }

        @Override
        public void toNetwork(FriendlyByteBuf pBuffer, CrusherRecipe pRecipe) {
            pBuffer.writeItem(pRecipe.output);
            pRecipe.getInput().toNetwork(pBuffer);
            pBuffer.writeInt(pRecipe.getProcessTime());
            pBuffer.writeInt(pRecipe.getEnergyCost());
        }
    }
}
