package de.melanx.utilitix.compat.jei;

import de.melanx.utilitix.UtilitiX;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.UpgradeRecipe;
import net.minecraft.world.level.block.Blocks;

import javax.annotation.Nonnull;

public class GildingCategory implements IRecipeCategory<UpgradeRecipe> {

    private final IDrawable background;
    private final IDrawable icon;

    public GildingCategory(IGuiHelper helper) {
        this.background = helper.createDrawable(new ResourceLocation("jei", "textures/gui/gui_vanilla.png"), 0, 168, 125, 18);
        this.icon = helper.createDrawableIngredient(VanillaTypes.ITEM, new ItemStack(Blocks.SMITHING_TABLE));
    }

    public static final ResourceLocation ID = new ResourceLocation(UtilitiX.getInstance().modid, "gilding");

    @Nonnull
    @Override
    public ResourceLocation getUid() {
        return ID;
    }

    @Nonnull
    @Override
    public Class<? extends UpgradeRecipe> getRecipeClass() {
        return UpgradeRecipe.class;
    }

    @Nonnull
    @Override
    public Component getTitle() {
        return new TranslatableComponent("jei.utilitix.gilding");
    }

    @Nonnull
    @Override
    public IDrawable getBackground() {
        return this.background;
    }

    @Nonnull
    @Override
    public IDrawable getIcon() {
        return this.icon;
    }

    @Override
    public void setRecipe(@Nonnull IRecipeLayoutBuilder builder, @Nonnull UpgradeRecipe recipe, @Nonnull IFocusGroup focuses) {
        builder.addSlot(RecipeIngredientRole.INPUT, 1, 1).addIngredients(recipe.base);
        builder.addSlot(RecipeIngredientRole.INPUT, 50, 1).addIngredients(recipe.addition);
        builder.addSlot(RecipeIngredientRole.OUTPUT, 108, 1).addItemStack(recipe.getResultItem());
    }
}
