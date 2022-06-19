package de.melanx.utilitix.registration;

import de.melanx.utilitix.UtilitiX;
import de.melanx.utilitix.recipe.BreweryRecipe;
import net.minecraft.world.item.crafting.RecipeType;
import org.moddingx.libx.annotation.registration.Reg;
import org.moddingx.libx.annotation.registration.RegisterClass;

@RegisterClass(registry = "RECIPE_TYPES")
public class ModRecipeTypes {

    @Reg.Name("utilitix_brewery")
    public static final RecipeType<BreweryRecipe> BREWERY = new RecipeType<>() {
        @Override
        public String toString() {
            return UtilitiX.getInstance().modid + "_brewery";
        }
    };
}
