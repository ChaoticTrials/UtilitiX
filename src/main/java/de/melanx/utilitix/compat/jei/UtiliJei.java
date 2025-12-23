package de.melanx.utilitix.compat.jei;

import com.google.common.collect.ImmutableList;
import de.melanx.utilitix.content.brewery.ContainerMenuAdvancedBrewery;
import de.melanx.utilitix.content.brewery.ScreenAdvancedBrewery;
import de.melanx.utilitix.content.crudefurnace.ContainerMenuCrudeFurnace;
import de.melanx.utilitix.content.crudefurnace.CrudeFurnaceRecipeHelper;
import de.melanx.utilitix.content.crudefurnace.ScreenCrudeFurnace;
import de.melanx.utilitix.content.gildingarmor.GildingArmorRecipe;
import de.melanx.utilitix.recipe.BreweryRecipe;
import de.melanx.utilitix.recipe.brewery.Apply;
import de.melanx.utilitix.registration.*;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.RecipeTypes;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.registration.*;
import mezz.jei.api.runtime.IJeiRuntime;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.block.Blocks;
import org.apache.commons.compress.utils.Lists;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

@JeiPlugin
public class UtiliJei implements IModPlugin {

    private static IJeiRuntime runtime = null;
    public static final RecipeType<BreweryRecipe> BREWING_RECIPE = RecipeType.create("utilitix", "advanced_brewery", BreweryRecipe.class);
    public static final RecipeType<SmithingTransformRecipe> GILDING_RECIPE = RecipeType.create("utilitix", "gilding", SmithingTransformRecipe.class);
    public static final RecipeType<SmeltingRecipe> SMELTING_RECIPE = RecipeType.create("utilitix", "smelting", SmeltingRecipe.class);

    @Nonnull
    @Override
    public ResourceLocation getPluginUid() {
        return ResourceLocation.fromNamespaceAndPath("utilitix", "jeiplugin");
    }

    @Override
    public void registerCategories(@Nonnull IRecipeCategoryRegistration registration) {
        registration.addRecipeCategories(
                new BreweryCategory(registration.getJeiHelpers().getGuiHelper()),
                new GildingCategory(registration.getJeiHelpers().getGuiHelper()),
                new CrudeSmeltingCategory(registration.getJeiHelpers().getGuiHelper())
        );
    }

    @Override
    public void registerRecipes(@Nonnull IRecipeRegistration registration) {
        ClientLevel level = Minecraft.getInstance().level;
        RecipeManager recipes = Objects.requireNonNull(level).getRecipeManager();
        List<BreweryRecipe> simpleBrewery = recipes.getAllRecipesFor(ModRecipeTypes.BREWERY).stream()
                .filter(r -> r.value().getAction() instanceof Apply)
                .map(RecipeHolder::value)
                .collect(Collectors.toList());
        registration.addRecipes(BREWING_RECIPE, simpleBrewery);
        registration.addRecipes(GILDING_RECIPE, getGildingRecipes());
        registration.addRecipes(SMELTING_RECIPE, getSmeltingRecipes(recipes, level.registryAccess()));

        registration.addIngredientInfo(ModBlocks.advancedBrewery, Component.translatable("description.utilitix.advanced_brewery"));
        registration.addIngredientInfo(ModBlocks.advancedBrewery, Component.translatable("description.utilitix.advanced_brewery.brewing"));
        registration.addIngredientInfo(ModBlocks.advancedBrewery, Component.translatable("description.utilitix.advanced_brewery.merging"));
        registration.addIngredientInfo(ModBlocks.advancedBrewery, Component.translatable("description.utilitix.advanced_brewery.upgrading"));
        registration.addIngredientInfo(ModBlocks.advancedBrewery, Component.translatable("description.utilitix.advanced_brewery.cloning"));

        registration.addItemStackInfo(ImmutableList.of(new ItemStack(ModBlocks.comparatorRedirectorUp), new ItemStack(ModBlocks.comparatorRedirectorDown)), Component.translatable("description.utilitix.comparator_redirector"));
        registration.addIngredientInfo(ModBlocks.weakRedstoneTorch, Component.translatable("description.utilitix.weak_redstone_torch"));
        registration.addItemStackInfo(ImmutableList.of(new ItemStack(ModItems.tinyCoal), new ItemStack(ModItems.tinyCharcoal)), Component.translatable("description.utilitix.tiny_coal"));
        registration.addIngredientInfo(ModItems.handBell, Component.translatable("description.utilitix.hand_bell"));
        registration.addIngredientInfo(ModItems.mobBell, Component.translatable("description.utilitix.mob_bell"));
        registration.addIngredientInfo(ModItems.failedPotion, Component.translatable("description.utilitix.failed_potion"));
        registration.addIngredientInfo(ModItems.armedStand, Component.translatable("description.utilitix.armed_stand"));
        registration.addIngredientInfo(ModItems.glueBall, Component.translatable("description.utilitix.glue_ball"));
        registration.addIngredientInfo(ModItems.linkedCrystal, Component.translatable("description.utilitix.linked_crystal"));
        registration.addIngredientInfo(ModBlocks.linkedRepeater, Component.translatable("description.utilitix.linked_repeater"));
        registration.addIngredientInfo(ModItems.minecartTinkerer, Component.translatable("description.utilitix.minecart_tinkerer"));
        registration.addIngredientInfo(ModBlocks.highspeedRail, Component.translatable("description.utilitix.highspeed_rail"));
        registration.addItemStackInfo(ImmutableList.of(new ItemStack(ModBlocks.directionalRail), new ItemStack(ModBlocks.directionalHighspeedRail)), Component.translatable("description.utilitix.directional_rail"));
        registration.addItemStackInfo(ImmutableList.of(new ItemStack(ModBlocks.crossingRail), new ItemStack(ModBlocks.reinforcedCrossingRail)), Component.translatable("description.utilitix.crossing_rail"));
        registration.addItemStackInfo(ImmutableList.of(new ItemStack(ModBlocks.filterRail), new ItemStack(ModBlocks.reinforcedFilterRail)), Component.translatable("description.utilitix.filter_rail"));
        registration.addIngredientInfo(ModBlocks.reinforcedRail, Component.translatable("description.utilitix.reinforced_rail"));
        registration.addIngredientInfo(ModRegisterables.enderCart.item(), Component.translatable("description.utilitix.ender_cart"));
        registration.addIngredientInfo(ModRegisterables.pistonCart.item(), Component.translatable("description.utilitix.piston_cart"));
        registration.addItemStackInfo(ImmutableList.of(new ItemStack(ModBlocks.pistonControllerRail), new ItemStack(ModBlocks.reinforcedPistonControllerRail)), Component.translatable("description.utilitix.piston_controller_rail"));
        registration.addIngredientInfo(ModRegisterables.stonecutterCart.item(), Component.translatable("description.utilitix.stonecutter_cart"));
        registration.addIngredientInfo(ModRegisterables.anvilCart.item(), Component.translatable("description.utilitix.anvil_cart"));
        registration.addIngredientInfo(ModBlocks.crudeFurnace, Component.translatable("description.utilitix.crude_furnace"));
        registration.addIngredientInfo(ModItems.mobYoinker, Component.translatable("description.utilitix.mob_yoinker"));
    }

    @Override
    public void registerRecipeTransferHandlers(@Nonnull IRecipeTransferRegistration registration) {
        registration.addRecipeTransferHandler(ContainerMenuCrudeFurnace.class, ModBlocks.crudeFurnace.menu, RecipeTypes.FUELING, 0, 1, 3, 36);
        registration.addRecipeTransferHandler(ContainerMenuCrudeFurnace.class, ModBlocks.crudeFurnace.menu, SMELTING_RECIPE, 1, 1, 3, 36);
        registration.addRecipeTransferHandler(ContainerMenuAdvancedBrewery.class, ModBlocks.advancedBrewery.menu, BREWING_RECIPE, 0, 4, 5, 36);
    }

    @Override
    public void registerRecipeCatalysts(@Nonnull IRecipeCatalystRegistration registration) {
        registration.addRecipeCatalyst(new ItemStack(ModBlocks.advancedBrewery), BREWING_RECIPE);
        registration.addRecipeCatalyst(new ItemStack(ModBlocks.crudeFurnace), SMELTING_RECIPE, RecipeTypes.FUELING);
        registration.addRecipeCatalyst(new ItemStack(Blocks.SMITHING_TABLE), GILDING_RECIPE);
    }

    @Override
    public void registerGuiHandlers(@Nonnull IGuiHandlerRegistration registration) {
        registration.addRecipeClickArea(ScreenAdvancedBrewery.class, 98, 17, 7, 26, BREWING_RECIPE);
        registration.addRecipeClickArea(ScreenCrudeFurnace.class, 78, 32, 28, 23, SMELTING_RECIPE, RecipeTypes.FUELING);
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

    private static List<SmithingTransformRecipe> getGildingRecipes() {
        List<SmithingTransformRecipe> recipes = Lists.newArrayList();
        Ingredient gildingItem = Ingredient.of(ModItems.gildingCrystal);

        for (Map.Entry<ResourceKey<Item>, Item> entry : BuiltInRegistries.ITEM.entrySet()) {
            if (entry.getValue() instanceof ArmorItem item && GildingArmorRecipe.canGild(item, new ItemStack(item))) {
                ItemStack output = new ItemStack(item);
                output.set(ModDataComponentTypes.gilded, true);

                SmithingTransformRecipe recipe = new SmithingTransformRecipe(Ingredient.EMPTY, Ingredient.of(item), gildingItem, output);

                recipes.add(recipe);
            }
        }

        return Collections.unmodifiableList(recipes);
    }

    private static List<SmeltingRecipe> getSmeltingRecipes(RecipeManager recipes, RegistryAccess registryAccess) {
        Registry<Item> items = registryAccess.registryOrThrow(Registries.ITEM);
        List<SmeltingRecipe> crudingRecipes = new ArrayList<>();
        for (Item item : items) {
            CrudeFurnaceRecipeHelper.ModifiedRecipe result = CrudeFurnaceRecipeHelper.getResult(recipes, registryAccess, new ItemStack(item));
            if (result != null) {
                crudingRecipes.add(result.getRecipeHolder().value());
            }
        }

        return crudingRecipes;
    }
}
