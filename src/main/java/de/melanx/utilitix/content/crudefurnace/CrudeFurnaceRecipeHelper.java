package de.melanx.utilitix.content.crudefurnace;

import io.github.noeppi_noeppi.libx.crafting.recipe.RecipeHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SmeltingRecipe;

import javax.annotation.Nullable;

public class CrudeFurnaceRecipeHelper {

    @Nullable
    public static ModifiedRecipe getResult(RecipeManager rm, ItemStack input) {
        if (input.isEmpty()) {
            return null;
        }

        SmeltingRecipe recipe = rm.getAllRecipesFor(RecipeType.SMELTING).stream()
                .filter(r -> r.getIngredients().get(0).test(input))
                .findFirst().orElse(null);

        if (recipe == null) {
            return null;
        }

        if (RecipeHelper.isItemValidInput(rm, RecipeType.BLASTING, input)
                || RecipeHelper.isItemValidInput(rm, RecipeType.SMOKING, input)) {
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
        private final SmeltingRecipe originalRecipe;

        ModifiedRecipe(SmeltingRecipe recipe) {
            this.xp = recipe.getExperience() / 2;
            this.burnTime = recipe.getCookingTime() / 2;
            this.output = recipe.getResultItem();
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

        public SmeltingRecipe getOriginalRecipe() {
            return this.originalRecipe;
        }
    }
}
