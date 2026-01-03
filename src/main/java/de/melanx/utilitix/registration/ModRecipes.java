package de.melanx.utilitix.registration;

import de.melanx.utilitix.content.gildingarmor.GildingArmorRecipe;
import de.melanx.utilitix.recipe.BreweryRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import org.moddingx.libx.annotation.registration.RegisterClass;

@RegisterClass(registry = "RECIPE_SERIALIZER")
public class ModRecipes {

    public static final RecipeSerializer<BreweryRecipe> brewerySerializer = new BreweryRecipe.Serializer();
    public static final RecipeSerializer<GildingArmorRecipe> gildingSerializer = new GildingArmorRecipe.Serializer();
}
