package de.melanx.utilitix.compat.jei;

import com.google.common.collect.ImmutableList;
import de.melanx.utilitix.content.brewery.ScreenAdvancedBrewery;
import de.melanx.utilitix.content.gildingarmor.GildingArmorRecipe;
import de.melanx.utilitix.recipe.BreweryRecipe;
import de.melanx.utilitix.recipe.brewery.Apply;
import de.melanx.utilitix.registration.*;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.registration.IGuiHandlerRegistration;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import mezz.jei.api.runtime.IJeiRuntime;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.SmithingTransformRecipe;
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

    @Nonnull
    @Override
    public ResourceLocation getPluginUid() {
        return ResourceLocation.fromNamespaceAndPath("utilitix", "jeiplugin");
    }

    @Override
    public void registerCategories(@Nonnull IRecipeCategoryRegistration registration) {
        registration.addRecipeCategories(
                new BreweryCategory(registration.getJeiHelpers().getGuiHelper()),
                new GildingCategory(registration.getJeiHelpers().getGuiHelper())
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

        registration.addIngredientInfo(new ItemStack(ModBlocks.advancedBrewery), VanillaTypes.ITEM_STACK, Component.translatable("description.utilitix.advanced_brewery"), Component.translatable("description.utilitix.advanced_brewery.brewing"), Component.translatable("description.utilitix.advanced_brewery.merging"), Component.translatable("description.utilitix.advanced_brewery.upgrading"), Component.translatable("description.utilitix.advanced_brewery.cloning"));
        registration.addIngredientInfo(ImmutableList.of(new ItemStack(ModBlocks.comparatorRedirectorUp), new ItemStack(ModBlocks.comparatorRedirectorDown)), VanillaTypes.ITEM_STACK, Component.translatable("description.utilitix.comparator_redirector"));
        registration.addIngredientInfo(new ItemStack(ModBlocks.weakRedstoneTorch), VanillaTypes.ITEM_STACK, Component.translatable("description.utilitix.weak_redstone_torch"));
        registration.addIngredientInfo(ImmutableList.of(new ItemStack(ModItems.tinyCoal), new ItemStack(ModItems.tinyCharcoal)), VanillaTypes.ITEM_STACK, Component.translatable("description.utilitix.tiny_coal"));
        registration.addIngredientInfo(new ItemStack(ModItems.handBell), VanillaTypes.ITEM_STACK, Component.translatable("description.utilitix.hand_bell"));
        registration.addIngredientInfo(new ItemStack(ModItems.mobBell), VanillaTypes.ITEM_STACK, Component.translatable("description.utilitix.mob_bell"));
        registration.addIngredientInfo(new ItemStack(ModItems.failedPotion), VanillaTypes.ITEM_STACK, Component.translatable("description.utilitix.failed_potion"));
        registration.addIngredientInfo(new ItemStack(ModItems.armedStand), VanillaTypes.ITEM_STACK, Component.translatable("description.utilitix.armed_stand"));
        registration.addIngredientInfo(new ItemStack(ModItems.glueBall), VanillaTypes.ITEM_STACK, Component.translatable("description.utilitix.glue_ball"));
        registration.addIngredientInfo(new ItemStack(ModItems.linkedCrystal), VanillaTypes.ITEM_STACK, Component.translatable("description.utilitix.linked_crystal"));
        registration.addIngredientInfo(new ItemStack(ModBlocks.linkedRepeater), VanillaTypes.ITEM_STACK, Component.translatable("description.utilitix.linked_repeater"));
        registration.addIngredientInfo(new ItemStack(ModItems.minecartTinkerer), VanillaTypes.ITEM_STACK, Component.translatable("description.utilitix.minecart_tinkerer"));
        registration.addIngredientInfo(new ItemStack(ModBlocks.highspeedRail), VanillaTypes.ITEM_STACK, Component.translatable("description.utilitix.highspeed_rail"));
        registration.addIngredientInfo(ImmutableList.of(new ItemStack(ModBlocks.directionalRail), new ItemStack(ModBlocks.directionalHighspeedRail)), VanillaTypes.ITEM_STACK, Component.translatable("description.utilitix.directional_rail"));
        registration.addIngredientInfo(ImmutableList.of(new ItemStack(ModBlocks.crossingRail), new ItemStack(ModBlocks.reinforcedCrossingRail)), VanillaTypes.ITEM_STACK, Component.translatable("description.utilitix.crossing_rail"));
        registration.addIngredientInfo(ImmutableList.of(new ItemStack(ModBlocks.filterRail), new ItemStack(ModBlocks.reinforcedFilterRail)), VanillaTypes.ITEM_STACK, Component.translatable("description.utilitix.filter_rail"));
        registration.addIngredientInfo(new ItemStack(ModBlocks.reinforcedRail), VanillaTypes.ITEM_STACK, Component.translatable("description.utilitix.reinforced_rail"));
        registration.addIngredientInfo(new ItemStack(ModRegisterables.enderCart.item()), VanillaTypes.ITEM_STACK, Component.translatable("description.utilitix.ender_cart"));
        registration.addIngredientInfo(new ItemStack(ModRegisterables.pistonCart.item()), VanillaTypes.ITEM_STACK, Component.translatable("description.utilitix.piston_cart"));
        registration.addIngredientInfo(ImmutableList.of(new ItemStack(ModBlocks.pistonControllerRail), new ItemStack(ModBlocks.reinforcedPistonControllerRail)), VanillaTypes.ITEM_STACK, Component.translatable("description.utilitix.piston_controller_rail"));
        registration.addIngredientInfo(new ItemStack(ModRegisterables.stonecutterCart.item()), VanillaTypes.ITEM_STACK, Component.translatable("description.utilitix.stonecutter_cart"));
        registration.addIngredientInfo(new ItemStack(ModRegisterables.anvilCart.item()), VanillaTypes.ITEM_STACK, Component.translatable("description.utilitix.anvil_cart"));
//        registration.addIngredientInfo(new ItemStack(ModBlocks.crudeFurnace), VanillaTypes.ITEM_STACK, Component.translatable("description.utilitix.crude_furnace")); todo
    }

    @Override
    public void registerRecipeCatalysts(@Nonnull IRecipeCatalystRegistration registration) {
        registration.addRecipeCatalyst(new ItemStack(ModBlocks.advancedBrewery), BREWING_RECIPE);
        registration.addRecipeCatalyst(new ItemStack(Blocks.SMITHING_TABLE), GILDING_RECIPE);
    }

    @Override
    public void registerGuiHandlers(@Nonnull IGuiHandlerRegistration registration) {
        registration.addRecipeClickArea(ScreenAdvancedBrewery.class, 98, 17, 7, 26, BREWING_RECIPE);
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
}
