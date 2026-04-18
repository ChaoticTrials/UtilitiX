package de.melanx.utilitix.compat.jei;

import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.IRecipeSlotBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
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
        this.icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(Blocks.SMITHING_TABLE));
    }

    @Nonnull
    @Override
    public RecipeType<SmithingTransformRecipe> getRecipeType() {
        return RecipeTypes.GILDING;
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
        return 28;
    }

    @Nonnull
    @Override
    public IDrawable getIcon() {
        return this.icon;
    }

    @Override
    public void setRecipe(@Nonnull IRecipeLayoutBuilder builder, @Nonnull SmithingTransformRecipe recipe, @Nonnull IFocusGroup focuses) {
        IPlatformRecipeHelper recipeHelper = Services.PLATFORM.getRecipeHelper();

        builder.addInputSlot(1, 6).setStandardSlotBackground();

        IRecipeSlotBuilder baseSlot = builder.addInputSlot(19, 6)
                .setStandardSlotBackground();
        IRecipeSlotBuilder additionSlot = builder.addInputSlot(37, 6)
                .setStandardSlotBackground();
        IRecipeSlotBuilder outputSlot = builder.addOutputSlot(91, 6)
                .setStandardSlotBackground();

        baseSlot.addIngredients(recipeHelper.getBase(recipe));
        additionSlot.addIngredients(recipeHelper.getAddition(recipe));
        outputSlot.addItemStack(RecipeUtil.getResultItem(recipe));
    }
}
