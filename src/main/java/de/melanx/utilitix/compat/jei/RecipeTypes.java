package de.melanx.utilitix.compat.jei;

import de.melanx.utilitix.UtilitiX;
import de.melanx.utilitix.recipe.BreweryRecipe;
import mezz.jei.api.recipe.RecipeType;
import net.minecraft.world.item.crafting.SmithingTransformRecipe;

public class RecipeTypes {

    public static final RecipeType<SmithingTransformRecipe> GILDING = RecipeType.create(UtilitiX.getInstance().modid, "gilding", SmithingTransformRecipe.class);
    public static final RecipeType<BreweryRecipe> BREWING = RecipeType.create(UtilitiX.getInstance().modid, "advanced_brewery", BreweryRecipe.class);
}
