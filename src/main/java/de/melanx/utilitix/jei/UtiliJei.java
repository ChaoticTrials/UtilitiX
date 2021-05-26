package de.melanx.utilitix.jei;

import de.melanx.utilitix.UtilitiX;
import de.melanx.utilitix.content.brewery.ScreenAdvancedBrewery;
import de.melanx.utilitix.recipe.BreweryRecipe;
import de.melanx.utilitix.recipe.EffectTransformer;
import de.melanx.utilitix.registration.ModBlocks;
import de.melanx.utilitix.registration.ModRecipes;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.IGuiHandlerRegistration;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import mezz.jei.api.runtime.IJeiRuntime;
import net.minecraft.client.Minecraft;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.RecipeManager;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

@JeiPlugin
public class UtiliJei implements IModPlugin {

    private static IJeiRuntime runtime = null;

    @Nonnull
    @Override
    public ResourceLocation getPluginUid() {
        return new ResourceLocation(UtilitiX.getInstance().modid, "jeiplugin");
    }

    @Override
    public void registerCategories(@Nonnull IRecipeCategoryRegistration registration) {
        registration.addRecipeCategories(
                new BreweryCategory(registration.getJeiHelpers().getGuiHelper())
        );
    }

    @Override
    public void registerRecipes(@Nonnull IRecipeRegistration registration) {
        ClientWorld world = Minecraft.getInstance().world;
        RecipeManager recipes = Objects.requireNonNull(world).getRecipeManager();
        List<BreweryRecipe> simpleBrewery = recipes.getRecipesForType(ModRecipes.BREWERY).stream()
                .filter(r -> r.getAction() instanceof EffectTransformer.Apply)
                .collect(Collectors.toList());
        registration.addRecipes(simpleBrewery, BreweryCategory.ID);
    }

    @Override
    public void registerRecipeCatalysts(@Nonnull IRecipeCatalystRegistration registration) {
        registration.addRecipeCatalyst(new ItemStack(ModBlocks.advancedBrewery), BreweryCategory.ID);
    }

    @Override
    public void registerGuiHandlers(@Nonnull IGuiHandlerRegistration registration) {
        registration.addRecipeClickArea(ScreenAdvancedBrewery.class, 98, 17, 7, 26, BreweryCategory.ID);
    }

    @Override
    public void onRuntimeAvailable(@Nonnull IJeiRuntime jeiRuntime) {
        runtime = jeiRuntime;
    }

    public static void runtime(Consumer<IJeiRuntime> action) {
        if (runtime != null) {
            action.accept(runtime);
        }
    }

    public static <T> Optional<T> runtime(Function<IJeiRuntime, T> action) {
        if (runtime != null) {
            return Optional.of(action.apply(runtime));
        } else {
            return Optional.empty();
        }
    }
}