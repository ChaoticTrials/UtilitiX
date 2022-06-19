package de.melanx.utilitix.registration;

import de.melanx.utilitix.content.gildingarmor.GildingArmorRecipe;
import de.melanx.utilitix.recipe.BreweryRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SimpleRecipeSerializer;
import org.moddingx.libx.annotation.registration.Reg.Name;
import org.moddingx.libx.annotation.registration.RegisterClass;

@RegisterClass(registry = "RECIPE_SERIALIZERS")
public class ModRecipes {

    @Name("brewery_serializer")
    public static final RecipeSerializer<BreweryRecipe> BREWERY_SERIALIZER = new BreweryRecipe.Serializer();
    @Name("gilding_serializer")
    public static final RecipeSerializer<GildingArmorRecipe> GILDING_SERIALIZER = new SimpleRecipeSerializer<>(GildingArmorRecipe::new);
}
