package de.melanx.utilitix.jei;

import de.melanx.utilitix.UtilitiX;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IGuiItemStackGroup;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.UpgradeRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.chat.TranslatableComponent;

import javax.annotation.Nonnull;
import java.util.Arrays;

public class GildingCategory implements IRecipeCategory<UpgradeRecipe> {

    private final IDrawable background;
    private final IDrawable icon;

    public GildingCategory(IGuiHelper helper) {
        this.background = helper.createDrawable(new ResourceLocation("jei", "textures/gui/gui_vanilla.png"), 0, 168, 125, 18);
        this.icon = helper.createDrawableIngredient(new ItemStack(Blocks.SMITHING_TABLE));
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
    public void setIngredients(@Nonnull UpgradeRecipe recipe, @Nonnull IIngredients ingredients) {
        ingredients.setInputIngredients(Arrays.asList(recipe.base, recipe.addition));
        ingredients.setOutput(VanillaTypes.ITEM, recipe.getResultItem());
    }

    @Override
    public void setRecipe(@Nonnull IRecipeLayout layout, @Nonnull UpgradeRecipe recipe, @Nonnull IIngredients ingredients) {
        IGuiItemStackGroup stacks = layout.getItemStacks();

        stacks.init(0, true, 0, 0);
        stacks.init(1, true, 49, 0);
        stacks.init(2, false, 107, 0);

        stacks.set(ingredients);
    }
}
