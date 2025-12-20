package de.melanx.utilitix.recipe;

import com.mojang.serialization.*;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.melanx.utilitix.recipe.brewery.*;
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
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Stream;

public class BreweryRecipe implements Recipe<RecipeWrapper> {

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

    public Optional<Ingredient> getInput() {
        return Optional.ofNullable(this.input);
    }

    public static class Serializer implements RecipeSerializer<BreweryRecipe> {

        private static <T> MapCodec<T> errorMapCodec(String message) {
            return new MapCodec<>() {
                @Override
                public <U> Stream<U> keys(DynamicOps<U> ops) {
                    return Stream.empty();
                }

                @Override
                public <U> DataResult<T> decode(DynamicOps<U> ops, MapLike<U> input) {
                    return DataResult.error(() -> message);
                }

                @Override
                public <U> RecordBuilder<U> encode(T input, DynamicOps<U> ops, RecordBuilder<U> prefix) {
                    return prefix.withErrorsFrom(DataResult.error(() -> message));
                }
            };
        }

        private static String actionType(EffectTransformer transformer) {
            if (transformer instanceof Apply) return "apply";
            if (transformer instanceof Merge) return "merge";
            if (transformer instanceof Upgrade) return "upgrade";
            if (transformer instanceof Clone) return "clone";
            throw new IllegalStateException("Unknown EffectTransformer: " + transformer.getClass().getName());
        }

        private static MapCodec<? extends EffectTransformer> actionCodec(String type) {
            return switch(type.toLowerCase(Locale.ROOT)) {
                case "apply" -> Apply.CODEC;
                case "merge" -> Merge.CODEC;
                case "upgrade" -> Upgrade.CODEC;
                case "clone" -> Clone.CODEC;
                default -> errorMapCodec("Unknown effect transformer type: " + type);
            };
        }

        private static final MapCodec<EffectTransformer> ACTION_CODEC =
                Codec.STRING.dispatchMap("type", Serializer::actionType, Serializer::actionCodec);

        private static final Codec<EffectTransformer> DIRECT_ACTION_CODEC = ACTION_CODEC.codec();

        private static final StreamCodec<RegistryFriendlyByteBuf, EffectTransformer> ACTION_STREAM_CODEC = StreamCodec.of(
                Serializer::toNetworkAction, Serializer::fromNetworkAction
        );

        private static EffectTransformer fromNetworkAction(@Nonnull RegistryFriendlyByteBuf buffer) {
            byte id = buffer.readByte();
            return switch(id) {
                case 0 -> Apply.STREAM_CODEC.decode(buffer);
                case 1 -> Merge.STREAM_CODEC.decode(buffer);
                case 2 -> Upgrade.STREAM_CODEC.decode(buffer);
                case 3 -> Clone.STREAM_CODEC.decode(buffer);
                default -> throw new IllegalStateException("Invalid packet: Unknown effect transformer type: " + id);
            };
        }

        private static void toNetworkAction(@Nonnull RegistryFriendlyByteBuf buffer, @Nonnull EffectTransformer transformer) {
            switch(transformer) {
                case Apply apply -> {
                    buffer.writeByte(0);
                    Apply.STREAM_CODEC.encode(buffer, apply);
                }
                case Merge merge -> {
                    buffer.writeByte(1);
                    Merge.STREAM_CODEC.encode(buffer, merge);
                }
                case Upgrade upgrade -> {
                    buffer.writeByte(2);
                    Upgrade.STREAM_CODEC.encode(buffer, upgrade);
                }
                case Clone clone -> {
                    buffer.writeByte(3);
                    Clone.STREAM_CODEC.encode(buffer, clone);
                }
                default -> throw new IllegalStateException("Unknown EffectTransformer: " + transformer.getClass().getName());
            }
        }

        public static final MapCodec<BreweryRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                        Ingredient.CODEC.optionalFieldOf("input").forGetter(BreweryRecipe::getInput),
                        DIRECT_ACTION_CODEC.fieldOf("action").forGetter(BreweryRecipe::getAction)
                )
                .apply(instance, (input, action) -> new BreweryRecipe(input.orElse(null), action)));

        public static final StreamCodec<RegistryFriendlyByteBuf, BreweryRecipe> STREAM_CODEC = StreamCodec.of(
                BreweryRecipe.Serializer::toNetwork, BreweryRecipe.Serializer::fromNetwork
        );

        public static BreweryRecipe fromNetwork(@Nonnull RegistryFriendlyByteBuf buffer) {
            Ingredient input = null;
            if (buffer.readBoolean()) {
                input = Ingredient.CONTENTS_STREAM_CODEC.decode(buffer);
            }

            EffectTransformer transformer = ACTION_STREAM_CODEC.decode(buffer);

            return new BreweryRecipe(input, transformer);
        }

        public static void toNetwork(@Nonnull RegistryFriendlyByteBuf buffer, @Nonnull BreweryRecipe recipe) {
            buffer.writeBoolean(recipe.input != null);
            if (recipe.input != null) {
                Ingredient.CONTENTS_STREAM_CODEC.encode(buffer, recipe.input);
            }

            ACTION_STREAM_CODEC.encode(buffer, recipe.transformer);
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
