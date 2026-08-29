package de.melanx.utilitix.registration;

import com.mojang.serialization.MapCodec;
import de.melanx.utilitix.content.gildingarmor.GildingArmorRecipe;
import de.melanx.utilitix.recipe.BreweryRecipe;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SmithingTransformRecipe;
import org.moddingx.libx.annotation.registration.RegisterClass;

@RegisterClass(registry = "RECIPE_SERIALIZER")
public class ModRecipes {

    public static final RecipeSerializer<BreweryRecipe> brewerySerializer = new RecipeSerializer<>(BreweryRecipe.Serializer.CODEC, BreweryRecipe.Serializer.STREAM_CODEC);
    public static final RecipeSerializer<SmithingTransformRecipe> gildingSerializer = new RecipeSerializer<>(
            MapCodec.unit(() -> GildingArmorRecipe.INSTANCE),
            StreamCodec.of((_, _) -> {}, _ -> GildingArmorRecipe.INSTANCE)
    );
}
