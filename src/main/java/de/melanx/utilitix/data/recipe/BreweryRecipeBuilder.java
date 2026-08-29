package de.melanx.utilitix.data.recipe;

import de.melanx.utilitix.recipe.BreweryRecipe;
import de.melanx.utilitix.recipe.brewery.EffectTransformer;
import net.minecraft.advancements.Criterion;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.ItemLike;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

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
        List<Holder<Item>> holders = new ArrayList<>();
        BuiltInRegistries.ITEM.getTagOrEmpty(input).forEach(holders::add);
        return this.input(Ingredient.of(HolderSet.direct(holders)));
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
    public ResourceKey<Recipe<?>> defaultId() {
        throw new IllegalStateException("No default id given.");
    }

    @Override
    public void save(@Nonnull RecipeOutput output, @Nonnull ResourceKey<Recipe<?>> location) {
        if (this.transformer == null) {
            throw new IllegalStateException("Can't build Advanced Brewery Recipe without action.");
        }

        BreweryRecipe breweryRecipe = new BreweryRecipe(this.input, this.transformer);
        Identifier id = location.identifier();
        ResourceKey<Recipe<?>> key = ResourceKey.create(Registries.RECIPE, Identifier.fromNamespaceAndPath(id.getNamespace(), "utilitix_brewery/" + id.getPath()));
        output.accept(key, breweryRecipe, null);
    }

    @Override
    public void save(@Nonnull RecipeOutput output, @Nonnull String id) {
        this.save(output, Identifier.parse(id));
    }

    public void save(@Nonnull RecipeOutput output, @Nonnull Identifier id) {
        this.save(output, ResourceKey.create(Registries.RECIPE, id));
    }
}
