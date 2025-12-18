package de.melanx.utilitix.compat.jei;

import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.common.platform.IPlatformRecipeHelper;
import mezz.jei.common.platform.Services;
import mezz.jei.library.util.RecipeUtil;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.SmithingTransformRecipe;
import net.minecraft.world.level.block.Blocks;

import javax.annotation.Nonnull;

public class GildingCategory implements IRecipeCategory<SmithingTransformRecipe> {

    private final IDrawable icon;

    public GildingCategory(IGuiHelper helper) {
        // todo remove this.background = helper.createDrawable(Constants.RECIPE_GUI_VANILLA, 0, 168, 108, 18);
        this.icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(Blocks.SMITHING_TABLE));
    }

    @Nonnull
    @Override
    public RecipeType<SmithingTransformRecipe> getRecipeType() {
        return UtiliJei.GILDING_RECIPE;
    }

    @Nonnull
    @Override
    public Component getTitle() {
        return Component.translatable("jei.utilitix.gilding");
    }

    @Override
    public int getWidth() {
        return 108;
    }

    @Override
    public int getHeight() {
        return 18;
    }

    @Nonnull
    @Override
    public IDrawable getIcon() {
        return this.icon;
    }

    @Override
    public void setRecipe(@Nonnull IRecipeLayoutBuilder builder, @Nonnull SmithingTransformRecipe recipe, @Nonnull IFocusGroup focuses) {
        IPlatformRecipeHelper recipeHelper = Services.PLATFORM.getRecipeHelper();

        builder.addSlot(RecipeIngredientRole.INPUT, 19, 1).addIngredients(recipeHelper.getBase(recipe));
        builder.addSlot(RecipeIngredientRole.INPUT, 37, 1).addIngredients(recipeHelper.getAddition(recipe));
        builder.addSlot(RecipeIngredientRole.OUTPUT, 91, 1).addItemStack(RecipeUtil.getResultItem(recipe));
    }
}
