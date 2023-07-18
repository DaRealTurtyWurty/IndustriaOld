package dev.turtywurty.industria.recipes;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.JsonOps;
import dev.turtywurty.industria.init.RecipeInit;
import dev.turtywurty.turtylib.core.util.Either3;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.Container;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

public record AgitatorRecipe(ResourceLocation id, GroupEither3<CountedIngredient, FluidStack, FluidStack> inputs,
                             GroupEither3<ItemStack, FluidStack, FluidStack> outputs) implements Recipe<Container> {
    public static final String ID = "agitator";

    @Override
    public boolean matches(Container pContainer, Level pLevel) {
        return true;
    }

    @Override
    public ItemStack assemble(Container pContainer, RegistryAccess pRegistryAccess) {
        return getResultItem(pRegistryAccess);
    }

    @Override
    public boolean canCraftInDimensions(int pWidth, int pHeight) {
        return true;
    }

    @Override
    public ItemStack getResultItem(RegistryAccess pRegistryAccess) {
        return ItemStack.EMPTY;
    }

    @Override
    public ResourceLocation getId() {
        return id();
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return RecipeInit.AGITATOR_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return RecipeInit.AGITATOR_TYPE.get();
    }

    public Either3<CountedIngredient, FluidStack, FluidStack> getInput(int index) {
        return inputs().getEither3s()[index];
    }

    public Either3<ItemStack, FluidStack, FluidStack> getOutput(int index) {
        return outputs().getEither3s()[index];
    }

    public GroupEither3<CountedIngredient, FluidStack, FluidStack> getInputs() {
        return inputs();
    }

    public GroupEither3<ItemStack, FluidStack, FluidStack> getOutputs() {
        return outputs();
    }

    public int getProcessTime() {
        return 200;
    }

    public static final class Type implements RecipeType<AgitatorRecipe> {
        public static final AgitatorRecipe.Type INSTANCE = new AgitatorRecipe.Type();

        private Type() {
        }

        @Override
        public String toString() {
            return AgitatorRecipe.ID;
        }
    }

    public static final class Serializer implements RecipeSerializer<AgitatorRecipe> {
        public static final AgitatorRecipe.Serializer INSTANCE = new AgitatorRecipe.Serializer();

        private Serializer() {
        }

        @Override
        public AgitatorRecipe fromJson(ResourceLocation pRecipeId, JsonObject pSerializedRecipe) {
            JsonArray inputArray = pSerializedRecipe.getAsJsonArray("ingredients");
            GroupEither3<CountedIngredient, FluidStack, FluidStack> inputs = new GroupEither3<>(3);
            for (int index = 0; index < inputArray.size() && index < 3; index++) {
                JsonObject element = inputArray.get(index).getAsJsonObject();
                if (element.has("item")) {
                    inputs.setLeft(index,
                            CountedIngredient.ITEM_INGREDIENT_CODEC.parse(JsonOps.INSTANCE, element).get().orThrow());
                } else if (element.has("FluidName")) {
                    inputs.setMiddle(index, FluidStack.CODEC.parse(JsonOps.INSTANCE, element).get().orThrow());
                }
            }

            JsonArray outputArray = pSerializedRecipe.getAsJsonArray("results");
            GroupEither3<ItemStack, FluidStack, FluidStack> outputs = new GroupEither3<>(3);
            for (int index = 0; index < outputArray.size() && index < 3; index++) {
                JsonObject element = outputArray.get(index).getAsJsonObject();
                if (element.has("item")) {
                    outputs.setLeft(index, CraftingHelper.getItemStack(element, true, true));
                } else if (element.has("FluidName")) {
                    outputs.setMiddle(index, FluidStack.CODEC.parse(JsonOps.INSTANCE, element).get().orThrow());
                }
            }

            return new AgitatorRecipe(pRecipeId, inputs, outputs);
        }

        @Override
        public void toNetwork(FriendlyByteBuf pBuffer, AgitatorRecipe pRecipe) {
            pBuffer.writeCollection(Arrays.asList(pRecipe.getInputs().getEither3s()), (buffer, either3) -> {
                either3.ifLeft(ingredient -> {
                    ingredient.ingredient().toNetwork(buffer);
                    buffer.writeVarInt(ingredient.count());
                    buffer.writeBoolean(ingredient.isTagged());
                });
                either3.ifMiddle(buffer::writeFluidStack);
            });

            pBuffer.writeCollection(Arrays.asList(pRecipe.getOutputs().getEither3s()), (buffer, either3) -> {
                either3.ifLeft(buffer::writeItem);
                either3.ifMiddle(buffer::writeFluidStack);
            });
        }

        @Override
        public @NotNull AgitatorRecipe fromNetwork(ResourceLocation pRecipeId, FriendlyByteBuf pBuffer) {
            GroupEither3<CountedIngredient, FluidStack, FluidStack> inputs = new GroupEither3<>(3);
            pBuffer.readList(buffer -> {
                try {
                    inputs.setLeft(0, new CountedIngredient(Ingredient.fromNetwork(buffer), buffer.readVarInt(),
                            buffer.readBoolean()));
                } catch (Exception e) {
                    inputs.setMiddle(0, buffer.readFluidStack());
                }

                return null;
            });

            GroupEither3<ItemStack, FluidStack, FluidStack> outputs = new GroupEither3<>(3);
            pBuffer.readList(buffer -> {
                try {
                    outputs.setLeft(0, buffer.readItem());
                } catch (Exception e) {
                    outputs.setMiddle(0, buffer.readFluidStack());
                }

                return null;
            });

            return new AgitatorRecipe(pRecipeId, inputs, outputs);
        }
    }

    public static class GroupEither3<L, M, R> {
        private final int size;
        private final Either3<L, M, R>[] either3s;

        @SuppressWarnings("unchecked")
        public GroupEither3(int size) {
            this.size = size;
            this.either3s = new Either3[size];
        }

        public int getSize() {
            return this.size;
        }

        public Either3<L, M, R>[] getEither3s() {
            return this.either3s;
        }

        public void setLeft(int index, L left) {
            this.either3s[index] = Either3.left(left);
        }

        public void setMiddle(int index, M middle) {
            this.either3s[index] = Either3.middle(middle);
        }

        public void setRight(int index, R right) {
            this.either3s[index] = Either3.right(right);
        }
    }

    public record CountedIngredient(Ingredient ingredient, int count, boolean isTagged) {
        public static final Codec<CountedIngredient> ITEM_INGREDIENT_CODEC = Codec.PASSTHROUGH.comapFlatMap(dynamic -> {
            try {
                String id = dynamic.get("item").asString("air");
                Ingredient ingredient;
                boolean isTagged = false;
                if (id.startsWith("#")) {
                    TagKey<Item> tagKey = TagKey.create(Registries.ITEM, new ResourceLocation(id.substring(1)));
                    ingredient = Ingredient.of(tagKey);
                    isTagged = true;
                } else {
                    Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(id));
                    ingredient = Ingredient.of(item);
                }

                int count = dynamic.get("Count").asInt(1);
                return DataResult.success(new CountedIngredient(ingredient, count, isTagged));
            } catch (Exception exception) {
                return DataResult.error(() -> "Failed to parse ingredient: " + exception.getMessage());
            }
        }, ingredient -> new Dynamic<>(JsonOps.INSTANCE, ingredient.toJson()));

        public JsonObject toJson() {
            String id;
            if (isTagged()) {
                TagKey<Item> tagKey = null;
                boolean allMatch = true;
                for (ItemStack stack : ingredient().getItems()) {
                    if (tagKey == null) {
                        tagKey = stack.getTags().iterator().next();
                    } else {
                        if (stack.getTags().noneMatch(tagKey::equals)) {
                            allMatch = false;
                            break;
                        }
                    }
                }

                if (allMatch && tagKey != null) {
                    id = "#" + tagKey.location();
                } else {
                    id = ForgeRegistries.ITEMS.getKey(ingredient().getItems()[0].getItem()).toString();
                }
            } else {
                id = ForgeRegistries.ITEMS.getKey(ingredient().getItems()[0].getItem()).toString();
            }

            var json = new JsonObject();
            json.addProperty("item", id);
            json.addProperty("Count", count());
            return json;
        }

        public static boolean isTagged(Ingredient ingredient) {
            TagKey<Item> tagKey = null;
            boolean allMatch = true;
            for (ItemStack stack : ingredient.getItems()) {
                if (tagKey == null) {
                    tagKey = stack.getTags().iterator().next();
                } else {
                    if (stack.getTags().noneMatch(tagKey::equals)) {
                        allMatch = false;
                        break;
                    }
                }
            }

            return allMatch && tagKey != null;
        }
    }
}