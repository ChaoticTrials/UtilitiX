package de.melanx.utilitix.recipe;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.melanx.utilitix.registration.ModRecipeTypes;
import de.melanx.utilitix.registration.ModRecipes;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.items.wrapper.RecipeWrapper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class BreweryRecipe implements Recipe<RecipeWrapper> {

    @Nullable
    private final Ingredient input;
    private final EffectTransformer transformer;

    public BreweryRecipe(@Nullable Ingredient input, EffectTransformer transformer) {
        this.input = input;
        this.transformer = transformer;
    }

    @Override
    public boolean matches(@Nonnull RecipeWrapper recipeWrapper, @Nonnull Level level) {
        if (recipeWrapper.size() == 5) {
            ItemStack mainInput = recipeWrapper.getItem(0);
            if (this.input == null && !mainInput.isEmpty() || this.input != null && !this.input.test(mainInput)) {
                return false;
            }
            return this.transformer.canTransform(new PotionInput(recipeWrapper.getItem(3), recipeWrapper.getItem(1), recipeWrapper.getItem(2)));
        }
        return false;
    }

    @Nullable
    public PotionOutput getPotionResult(@Nonnull RecipeWrapper recipeWrapper) {
        if (recipeWrapper.size() == 5) {
            return this.transformer.transform(new PotionInput(recipeWrapper.getItem(3), recipeWrapper.getItem(1), recipeWrapper.getItem(2)));
        }

        return null;
    }

    @Nonnull
    @Override
    public ItemStack assemble(@Nonnull RecipeWrapper recipeWrapper, @Nonnull HolderLookup.Provider registry) {
        PotionOutput output = this.getPotionResult(recipeWrapper);
        return output == null ? recipeWrapper.getItem(3).copy() : output.getMain();
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return true;
    }

    @Nonnull
    @Override
    public ItemStack getResultItem(@Nonnull HolderLookup.Provider registry) {
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

        public static final MapCodec<BreweryRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                        Ingredient.CODEC.fieldOf("input").forGetter(recipe -> recipe.input),
                        EffectTransformer.DIRECT_CODEC.fieldOf("action").forGetter(BreweryRecipe::getAction)
                )
                .apply(instance, BreweryRecipe::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, BreweryRecipe> STREAM_CODEC = StreamCodec.of(
                BreweryRecipe.Serializer::toNetwork, BreweryRecipe.Serializer::fromNetwork
        );

        public static BreweryRecipe fromNetwork(@Nonnull RegistryFriendlyByteBuf buffer) {
            Ingredient input = null;
            if (buffer.readBoolean()) {
                input = Ingredient.CONTENTS_STREAM_CODEC.decode(buffer);
            }
            EffectTransformer transformer = EffectTransformer.read(buffer);

            return new BreweryRecipe(input, transformer);
        }

        public static void toNetwork(@Nonnull RegistryFriendlyByteBuf buffer, @Nonnull BreweryRecipe recipe) {
            buffer.writeBoolean(recipe.input != null);
            if (recipe.input != null) {
                Ingredient.CONTENTS_STREAM_CODEC.encode(buffer, recipe.input);
            }
            recipe.transformer.write(buffer);
        }

        @Nonnull
        @Override
        public MapCodec<BreweryRecipe> codec() {
            return CODEC;
        }

        @Nonnull
        @Override
        public StreamCodec<RegistryFriendlyByteBuf, BreweryRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
}
