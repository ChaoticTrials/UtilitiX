package de.melanx.utilitix.compat.jei;

import de.melanx.utilitix.UtilitiX;
import de.melanx.utilitix.recipe.BreweryRecipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SmithingTransformRecipe;
import org.moddingx.libx.annotation.registration.RegisterClass;

@RegisterClass(registry = "RECIPE_TYPE")
public class RecipeTypes {

    public static final RecipeType<SmithingTransformRecipe> GILDING = RecipeType.simple(UtilitiX.getInstance().resource("gilding"));
    public static final RecipeType<BreweryRecipe> BREWING = RecipeType.simple(UtilitiX.getInstance().resource("advanced_brewery"));
}
