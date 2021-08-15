package de.melanx.utilitix.registration;

import de.melanx.utilitix.UtilitiX;
import de.melanx.utilitix.content.gildingarmor.GildingArmorRecipe;
import de.melanx.utilitix.recipe.BreweryRecipe;
import io.github.noeppi_noeppi.libx.annotation.registration.NoReg;
import io.github.noeppi_noeppi.libx.annotation.registration.RegName;
import io.github.noeppi_noeppi.libx.annotation.registration.RegisterClass;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SimpleRecipeSerializer;

@RegisterClass
public class ModRecipes {

    @NoReg
    public static final RecipeType<BreweryRecipe> BREWERY = RecipeType.register(UtilitiX.getInstance().modid + "_brewery");

    @RegName("brewery_serializer")
    public static final RecipeSerializer<BreweryRecipe> BREWERY_SERIALIZER = new BreweryRecipe.Serializer();
    @RegName("gilding_serializer")
    public static final RecipeSerializer<GildingArmorRecipe> GILDING_SERIALIZER = new SimpleRecipeSerializer<>(GildingArmorRecipe::new);
}
