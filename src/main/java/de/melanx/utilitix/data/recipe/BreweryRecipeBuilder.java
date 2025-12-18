package de.melanx.utilitix.data.recipe;

import de.melanx.utilitix.recipe.EffectTransformer;
import net.minecraft.advancements.Criterion;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;

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

//    public void build(Consumer<FinishedRecipe> consumerIn, ResourceLocation id) { todo
//
//    }

    @Override
    public RecipeBuilder unlockedBy(String name, Criterion<?> criterion) {
        return null;
    }

    @Override
    public RecipeBuilder group(@Nullable String groupName) {
        return null;
    }

    @Override
    public Item getResult() {
        return null;
    }

    @Override
    public void save(RecipeOutput recipeOutput, ResourceLocation id) {
        if (this.transformer == null) {
            throw new IllegalStateException("Can't build Advanced Brewery Recipe without action.");
        }
//        recipeOutput.accept(ResourceLocation.fromNamespaceAndPath(id.getNamespace(), "utilitix_brewery/" + id.getPath()), this.input, this.transformer); todo
    }

//    private record Recipe(ResourceLocation id, todo
//                          @Nullable Ingredient input,
//                          EffectTransformer transformer) implements FinishedRecipe {
//
//        private Recipe(ResourceLocation id, @Nullable Ingredient input, EffectTransformer transformer) {
//            this.id = id;
//            this.input = input;
//            this.transformer = transformer;
//        }
//
//        @Nonnull
//        @Override
//        public ResourceLocation getId() {
//            return this.id;
//        }
//
//        @Override
//        public void serializeRecipeData(@Nonnull JsonObject json) {
//            if (this.input != null) {
//                json.add("input", this.input.toJson());
//            }
//            json.add("action", this.transformer.serialize());
//        }
//
//        @Nonnull
//        @Override
//        public RecipeSerializer<?> getType() {
//            return ModRecipes.BREWERY_SERIALIZER;
//        }
//
//        @Nullable
//        @Override
//        public JsonObject serializeAdvancement() {
//            return null;
//        }
//
//        @Nullable
//        @Override
//        public ResourceLocation getAdvancementId() {
//            return null;
//        }
//    }
}
