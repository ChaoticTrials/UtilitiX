package de.melanx.utilitix.data.recipe;

import com.google.gson.JsonObject;
import de.melanx.utilitix.recipe.EffectTransformer;
import de.melanx.utilitix.registration.ModRecipes;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.item.Item;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.tags.ITag;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.Consumer;

public class BreweryRecipeBuilder {

    @Nullable
    private Ingredient input;
    @Nullable
    private EffectTransformer transformer;

    private BreweryRecipeBuilder() {

    }

    public static BreweryRecipeBuilder breweryRecipe() {
        return new BreweryRecipeBuilder();
    }

    public BreweryRecipeBuilder input(IItemProvider input) {
        return this.input(Ingredient.fromItems(input));
    }

    public BreweryRecipeBuilder input(ITag<Item> input) {
        return this.input(Ingredient.fromTag(input));
    }

    public BreweryRecipeBuilder input(Ingredient input) {
        if (this.input != null) {
            throw new IllegalStateException("Advanced Brewery Recipes can only take one input.");
        }
        this.input = input;
        return this;
    }

    public BreweryRecipeBuilder action(EffectTransformer transformer) {
        if (this.transformer != null) {
            throw new IllegalStateException("Advanced Brewery Recipes can only take one effect transformer.");
        }
        this.transformer = transformer;
        return this;
    }

    public void build(Consumer<IFinishedRecipe> consumerIn, ResourceLocation id) {
        if (this.transformer == null) {
            throw new IllegalStateException("Can't build Advanced Brewery Recipe without action.");
        }
        consumerIn.accept(new FinishedRecipe(new ResourceLocation(id.getNamespace(), "utilitix_brewery/" + id.getPath()), this.input, this.transformer));
    }

    private static class FinishedRecipe implements IFinishedRecipe {

        private final ResourceLocation id;
        @Nullable
        private final Ingredient input;
        private final EffectTransformer transformer;

        private FinishedRecipe(ResourceLocation id, @Nullable Ingredient input, EffectTransformer transformer) {
            this.id = id;
            this.input = input;
            this.transformer = transformer;
        }

        @Nonnull
        @Override
        public ResourceLocation getID() {
            return this.id;
        }

        @Override
        public void serialize(@Nonnull JsonObject json) {
            if (this.input != null) {
                json.add("input", this.input.serialize());
            }
            json.add("action", this.transformer.serialize());
        }

        @Nonnull
        @Override
        public IRecipeSerializer<?> getSerializer() {
            return ModRecipes.BREWERY_SERIALIZER;
        }

        @Nullable
        @Override
        public JsonObject getAdvancementJson() {
            return null;
        }

        @Nullable
        @Override
        public ResourceLocation getAdvancementID() {
            return null;
        }
    }
}
