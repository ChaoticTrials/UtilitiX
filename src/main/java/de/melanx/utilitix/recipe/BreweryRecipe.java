package de.melanx.utilitix.recipe;

import com.google.gson.JsonObject;
import de.melanx.utilitix.registration.ModRecipeTypes;
import de.melanx.utilitix.registration.ModRecipes;
import net.minecraft.core.NonNullList;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class BreweryRecipe implements Recipe<Container> {

    private final ResourceLocation id;
    @Nullable
    private final Ingredient input;
    private final EffectTransformer transformer;

    public BreweryRecipe(ResourceLocation id, @Nullable Ingredient input, EffectTransformer transformer) {
        this.id = id;
        this.input = input;
        this.transformer = transformer;
    }

    @Override
    public boolean matches(@Nonnull Container inv, @Nonnull Level level) {
        if (inv.getContainerSize() == 5) {
            ItemStack mainInput = inv.getItem(0);
            if (this.input == null && !mainInput.isEmpty() || this.input != null && !this.input.test(mainInput)) {
                return false;
            }
            return this.transformer.canTransform(new PotionInput(inv.getItem(3), inv.getItem(1), inv.getItem(2)));
        }
        return false;
    }

    @Nullable
    public PotionOutput getPotionResult(@Nonnull Container inv) {
        if (inv.getContainerSize() == 5) {
            return this.transformer.transform(new PotionInput(inv.getItem(3), inv.getItem(1), inv.getItem(2)));
        }
        return null;
    }

    @Nonnull
    @Override
    public ItemStack assemble(@Nonnull Container inv) {
        PotionOutput output = this.getPotionResult(inv);
        return output == null ? inv.getItem(3).copy() : output.getMain();
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return true;
    }

    @Nonnull
    @Override
    public ItemStack getResultItem() {
        return this.transformer.output();
    }

    @Nonnull
    @Override
    public NonNullList<Ingredient> getIngredients() {
        NonNullList<Ingredient> nnl = NonNullList.create();
        if (this.input != null) {
            nnl.add(this.input);
        }
        return nnl;
    }

    @Override
    public boolean isSpecial() {
        return true;
    }

    @Nonnull
    @Override
    public ResourceLocation getId() {
        return this.id;
    }

    public EffectTransformer getAction() {
        return this.transformer;
    }

    @Nonnull
    @Override
    public RecipeType<?> getType() {
        return ModRecipeTypes.BREWERY;
    }

    @Nonnull
    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipes.BREWERY_SERIALIZER;
    }

    public static class Serializer implements RecipeSerializer<BreweryRecipe> {

        @Nonnull
        @Override
        public BreweryRecipe fromJson(@Nonnull ResourceLocation recipeId, @Nonnull JsonObject json) {
            Ingredient input = null;
            if (json.has("input")) {
                input = Ingredient.fromJson(json.getAsJsonObject("input"));
            }
            EffectTransformer transformer = EffectTransformer.deserialize(json.getAsJsonObject("action"));
            return new BreweryRecipe(recipeId, input, transformer);
        }

        @Nullable
        @Override
        public BreweryRecipe fromNetwork(@Nonnull ResourceLocation recipeId, @Nonnull FriendlyByteBuf buffer) {
            Ingredient input = null;
            if (buffer.readBoolean()) {
                input = Ingredient.fromNetwork(buffer);
            }
            EffectTransformer transformer = EffectTransformer.read(buffer);
            return new BreweryRecipe(recipeId, input, transformer);
        }

        @Override
        public void toNetwork(@Nonnull FriendlyByteBuf buffer, @Nonnull BreweryRecipe recipe) {
            buffer.writeBoolean(recipe.input != null);
            if (recipe.input != null) {
                recipe.input.toNetwork(buffer);
            }
            recipe.transformer.write(buffer);
        }
    }
}
