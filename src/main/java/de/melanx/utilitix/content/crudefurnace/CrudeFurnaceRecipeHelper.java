package de.melanx.utilitix.content.crudefurnace;

import net.minecraft.core.RegistryAccess;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SmeltingRecipe;
import net.minecraft.world.level.Level;
import org.moddingx.libx.crafting.RecipeHelper;

import javax.annotation.Nullable;

public class CrudeFurnaceRecipeHelper {

    @Nullable
    public static ModifiedRecipe getResult(Level level, ItemStack input) {
        return CrudeFurnaceRecipeHelper.getResult(level.getRecipeManager(), level.registryAccess(), input);
    }

    @Nullable
    public static ModifiedRecipe getResult(RecipeManager recipeManager, RegistryAccess registryAccess, ItemStack input) {
        if (input.isEmpty()) {
            return null;
        }

        RecipeHolder<SmeltingRecipe> recipe = recipeManager.getAllRecipesFor(RecipeType.SMELTING).stream()
                .filter(r -> r.value().getIngredients().getFirst().test(input))
                .findFirst().orElse(null);

        if (recipe == null) {
            return null;
        }

        if (RecipeHelper.isItemValidInput(recipeManager, RecipeType.BLASTING, input)
                || RecipeHelper.isItemValidInput(recipeManager, RecipeType.SMOKING, input)) {
            // Recipe already has a special type of furnace
            return null;
        }

        return new ModifiedRecipe(registryAccess, recipe);
    }

    public static class ModifiedRecipe {

        private final float xp;
        private final int burnTime;
        private final ItemStack output;
        private final RecipeHolder<SmeltingRecipe> originalRecipe;

        ModifiedRecipe(RegistryAccess registryAccess, RecipeHolder<SmeltingRecipe> recipeHolder) {
            SmeltingRecipe recipe = recipeHolder.value();
            this.xp = recipe.getExperience() / 2;
            this.burnTime = recipe.getCookingTime() / 2;
            this.output = recipe.getResultItem(registryAccess);
            this.originalRecipe = recipeHolder;
        }

        public float getXp() {
            return this.xp;
        }

        public int getBurnTime() {
            return this.burnTime;
        }

        public ItemStack getOutput() {
            return this.output;
        }

        public RecipeHolder<SmeltingRecipe> getRecipeHolder() {
            return this.originalRecipe;
        }
    }
}
