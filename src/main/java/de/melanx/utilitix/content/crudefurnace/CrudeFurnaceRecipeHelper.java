package de.melanx.utilitix.content.crudefurnace;

import net.minecraft.core.RegistryAccess;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SmeltingRecipe;
import net.minecraft.world.level.Level;
import org.moddingx.libx.crafting.RecipeHelper;

import javax.annotation.Nullable;

public class CrudeFurnaceRecipeHelper {

    @Nullable
    public static ModifiedRecipe getResult(Level level, ItemStack input) {
        if (input.isEmpty()) {
            return null;
        }

        RecipeManager rm = level.getRecipeManager();
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
            return new ModifiedRecipe(level.registryAccess(), recipe);
        }
    }

    public static class ModifiedRecipe {

        private final float xp;
        private final int burnTime;
        private final ItemStack output;
        private final SmeltingRecipe originalRecipe;

        ModifiedRecipe(RegistryAccess registryAccess, SmeltingRecipe recipe) {
            this.xp = recipe.getExperience() / 2;
            this.burnTime = recipe.getCookingTime() / 2;
            this.output = recipe.getResultItem(registryAccess);
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
