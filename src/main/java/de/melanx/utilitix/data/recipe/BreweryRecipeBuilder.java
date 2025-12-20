package de.melanx.utilitix.data.recipe;

import de.melanx.utilitix.recipe.BreweryRecipe;
import de.melanx.utilitix.recipe.brewery.EffectTransformer;
import net.minecraft.advancements.Criterion;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class BreweryRecipeBuilder implements RecipeBuilder {

    @Nullable
    private Ingredient input;
    @Nullable
    private EffectTransformer transformer;

    private BreweryRecipeBuilder() {}

    public static BreweryRecipeBuilder breweryRecipe() {
        return new BreweryRecipeBuilder();
    }

    public BreweryRecipeBuilder input(ItemLike input) {
        return this.input(Ingredient.of(input));
    }

    public BreweryRecipeBuilder input(TagKey<Item> input) {
        return this.input(Ingredient.of(input));
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

    @Nonnull
    @Override
    public RecipeBuilder unlockedBy(@Nonnull String name, @Nonnull Criterion<?> criterion) {
        return this;
    }

    @Nonnull
    @Override
    public RecipeBuilder group(@Nullable String groupName) {
        return this;
    }

    @Nonnull
    @Override
    public Item getResult() {
        if (this.transformer == null) {
            throw new IllegalStateException("Can't build Advanced Brewery Recipe without action.");
        }

        return this.transformer.output().getItem();
    }

    @Override
    public void save(@Nonnull RecipeOutput recipeOutput, @Nonnull ResourceLocation id) {
        if (this.transformer == null) {
            throw new IllegalStateException("Can't build Advanced Brewery Recipe without action.");
        }

        BreweryRecipe breweryRecipe = new BreweryRecipe(this.input, this.transformer);
        recipeOutput.accept(ResourceLocation.fromNamespaceAndPath(id.getNamespace(), "utilitix_brewery/" + id.getPath()), breweryRecipe, null);
    }
}
