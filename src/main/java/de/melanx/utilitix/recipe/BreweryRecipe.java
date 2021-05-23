package de.melanx.utilitix.recipe;

import com.google.common.collect.ImmutableList;
import com.google.gson.JsonObject;
import de.melanx.utilitix.registration.ModItemTags;
import de.melanx.utilitix.registration.ModRecipes;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.network.PacketBuffer;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.PotionUtils;
import net.minecraft.potion.Potions;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.registries.ForgeRegistryEntry;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;

public class BreweryRecipe implements IRecipe<IInventory> {

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
    public boolean matches(@Nonnull IInventory inv, @Nonnull World world) {
        if (inv.getSizeInventory() == 5) {
            ItemStack mainInput = inv.getStackInSlot(0);
            if (this.input == null && !mainInput.isEmpty() || this.input != null && !this.input.test(mainInput)) {
                return false;
            }
            return this.transformer.canTransform(new PotionInput(inv.getStackInSlot(3), inv.getStackInSlot(1), inv.getStackInSlot(2)));
        }
        return false;
    }

    @Nullable
    public PotionOutput getPotionResult(@Nonnull IInventory inv) {
        if (inv.getSizeInventory() == 5) {
            return this.transformer.transform(new PotionInput(inv.getStackInSlot(3), inv.getStackInSlot(1), inv.getStackInSlot(2)));
        }
        return null;
    }
    
    @Nonnull
    @Override
    public ItemStack getCraftingResult(@Nonnull IInventory inv) {
        PotionOutput output = this.getPotionResult(inv);
        return output == null ? inv.getStackInSlot(3).copy() : output.getMain();
    }

    @Override
    public boolean canFit(int width, int height) {
        return true;
    }

    @Nonnull
    @Override
    public ItemStack getRecipeOutput() {
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
    public boolean isDynamic() {
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
    public IRecipeType<?> getType() {
        return ModRecipes.BREWERY;
    }

    @Nonnull
    @Override
    public IRecipeSerializer<?> getSerializer() {
        return ModRecipes.BREWERY_SERIALIZER;
    }

    public static class Serializer extends ForgeRegistryEntry<IRecipeSerializer<?>> implements IRecipeSerializer<BreweryRecipe> {

        @Nonnull
        @Override
        public BreweryRecipe read(@Nonnull ResourceLocation recipeId, @Nonnull JsonObject json) {
            Ingredient input = null;
            if (json.has("input")) {
                input = Ingredient.deserialize(json.getAsJsonObject("input"));
            }
            EffectTransformer transformer = EffectTransformer.deserialize(json.getAsJsonObject("action"));
            return new BreweryRecipe(recipeId, input, transformer);
        }

        @Nullable
        @Override
        public BreweryRecipe read(@Nonnull ResourceLocation recipeId, @Nonnull PacketBuffer buffer) {
            Ingredient input = null;
            if (buffer.readBoolean()) {
                input = Ingredient.read(buffer);
            }
            EffectTransformer transformer = EffectTransformer.read(buffer);
            return new BreweryRecipe(recipeId, input, transformer);
        }

        @Override
        public void write(@Nonnull PacketBuffer buffer, @Nonnull BreweryRecipe recipe) {
            buffer.writeBoolean(recipe.input != null);
            if (recipe.input != null) {
                recipe.input.write(buffer);
            }
            recipe.transformer.write(buffer);
        }
    }
}
