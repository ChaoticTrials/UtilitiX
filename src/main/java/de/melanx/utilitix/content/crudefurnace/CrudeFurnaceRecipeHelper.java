package de.melanx.utilitix.content.crudefurnace;

import io.github.noeppi_noeppi.libx.crafting.recipe.RecipeHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.RecipeManager;

import javax.annotation.Nullable;

public class CrudeFurnaceRecipeHelper {

    @Nullable
    public static ModifiedRecipe getResult(RecipeManager rm, ItemStack input) {
        if (input.isEmpty()) {
            return null;
        }

        FurnaceRecipe recipe = rm.getRecipesForType(IRecipeType.SMELTING).stream()
                .filter(r -> r.getIngredients().get(0).test(input))
                .findFirst().orElse(null);

        if (recipe == null) {
            return null;
        }

        if (RecipeHelper.isItemValidInput(rm, IRecipeType.BLASTING, input)
                || RecipeHelper.isItemValidInput(rm, IRecipeType.SMOKING, input)) {
            // Recipe already has a special type of furnace
            return null;
        } else {
            return new ModifiedRecipe(recipe);
        }
    }

    public static class ModifiedRecipe {

        private final float xp;
        private final int burnTime;
        private final ItemStack output;
        private final FurnaceRecipe originalRecipe;

        ModifiedRecipe(FurnaceRecipe recipe) {
            this.xp = recipe.getExperience() / 2;
            this.burnTime = recipe.getCookTime() / 2;
            this.output = recipe.getRecipeOutput();
            this.originalRecipe = recipe;
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

        public FurnaceRecipe getOriginalRecipe() {
            return this.originalRecipe;
        }
    }
}
