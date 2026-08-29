package de.melanx.utilitix.content.crudefurnace;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import org.moddingx.libx.crafting.RecipeHelper;

import javax.annotation.Nullable;

public class CrudeFurnaceRecipeHelper {

    @Nullable
    public static ModifiedRecipe getResult(Level level, ItemStack input) {
        if (!(level instanceof ServerLevel serverLevel)) {
            return null;
        }

        return CrudeFurnaceRecipeHelper.getResult(serverLevel.recipeAccess(), input);
    }

    @Nullable
    public static ModifiedRecipe getResult(RecipeManager recipeManager, ItemStack input) {
        if (input.isEmpty()) {
            return null;
        }

        RecipeHolder<SmeltingRecipe> recipe = recipeManager.recipes.byType(RecipeType.SMELTING).stream()
                .filter(r -> r.value().input().test(input))
                .findFirst().orElse(null);

        if (recipe == null) {
            return null;
        }

        if (RecipeHelper.isItemValidInput(recipeManager, RecipeType.BLASTING, input)
                || RecipeHelper.isItemValidInput(recipeManager, RecipeType.SMOKING, input)) {
            // Recipe already has a special type of furnace
            return null;
        }

        return new ModifiedRecipe(recipe);
    }

    public static class ModifiedRecipe {

        private final float xp;
        private final int burnTime;
        private final ItemStack output;
        private final RecipeHolder<SmeltingRecipe> originalRecipe;

        ModifiedRecipe(RecipeHolder<SmeltingRecipe> recipeHolder) {
            SmeltingRecipe recipe = recipeHolder.value();
            this.xp = recipe.experience() / 2;
            this.burnTime = recipe.cookingTime() / 2;
            this.output = recipe.assemble(new SingleRecipeInput(ItemStack.EMPTY));
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
