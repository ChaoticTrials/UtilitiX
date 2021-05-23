package de.melanx.utilitix.registration;

import de.melanx.utilitix.UtilitiX;
import de.melanx.utilitix.recipe.BreweryRecipe;
import io.github.noeppi_noeppi.libx.annotation.NoReg;
import io.github.noeppi_noeppi.libx.annotation.RegName;
import io.github.noeppi_noeppi.libx.annotation.RegisterClass;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;

@RegisterClass
public class ModRecipes {

    @NoReg public static final IRecipeType<BreweryRecipe> BREWERY = IRecipeType.register(UtilitiX.getInstance().modid + "_brewery");

    @RegName("brewery_serializer") public static final IRecipeSerializer<BreweryRecipe> BREWERY_SERIALIZER = new BreweryRecipe.Serializer();
}
