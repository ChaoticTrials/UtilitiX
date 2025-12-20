package de.melanx.utilitix.compat.jei;

import de.melanx.utilitix.registration.ModBlocks;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.placement.HorizontalAlignment;
import mezz.jei.api.gui.placement.VerticalAlignment;
import mezz.jei.api.gui.widgets.IRecipeExtrasBuilder;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.library.util.RecipeUtil;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.SmeltingRecipe;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;

public class CrudeSmeltingCategory implements IRecipeCategory<SmeltingRecipe> {

    private static final int DEFAULT_COOK_TIME = 100;
    private final IDrawable icon;

    public CrudeSmeltingCategory(IGuiHelper guiHelper) {
        this.icon = guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(ModBlocks.crudeFurnace));
        ;
    }

    @Nonnull
    @Override
    public RecipeType<SmeltingRecipe> getRecipeType() {
        return UtiliJei.SMELTING_RECIPE;
    }

    @Nonnull
    @Override
    public Component getTitle() {
        return Component.translatable("jei.utilitix.cruding");
    }

    @Override
    public int getWidth() {
        return 82;
    }

    @Override
    public int getHeight() {
        return 54;
    }

    @Override
    public @Nullable IDrawable getIcon() {
        return this.icon;
    }

    @Override
    public void setRecipe(@Nonnull IRecipeLayoutBuilder builder, @Nonnull SmeltingRecipe recipe, @Nonnull IFocusGroup focuses) {
        builder.addInputSlot(1, 1)
                .setStandardSlotBackground()
                .addIngredients(recipe.getIngredients().getFirst());

        builder.addSlot(RecipeIngredientRole.RENDER_ONLY, 1, 37)
                .setStandardSlotBackground();

        builder.addOutputSlot(61, 19)
                .setOutputSlotBackground()
                .addItemStack(RecipeUtil.getResultItem(recipe));
    }

    @Override
    public void createRecipeExtras(@Nonnull IRecipeExtrasBuilder builder, SmeltingRecipe recipe, @Nonnull IFocusGroup focuses) {
        int cookTime = recipe.getCookingTime() / 2;
        if (cookTime <= 0) {
            cookTime = DEFAULT_COOK_TIME;
        }
        builder.addAnimatedRecipeArrow(cookTime)
                .setPosition(26, 17);
        builder.addAnimatedRecipeFlame(300)
                .setPosition(1, 20);

        addExperience(builder, recipe);
        addCookTime(builder, recipe);
    }

    protected void addExperience(IRecipeExtrasBuilder builder, SmeltingRecipe recipe) {
        float experience = recipe.getExperience();
        if (experience > 0) {
            Component experienceString = Component.translatable("gui.jei.category.smelting.experience", experience);
            builder.addText(experienceString, getWidth() - 20, 10)
                    .setPosition(0, 0, getWidth(), getHeight(), HorizontalAlignment.RIGHT, VerticalAlignment.TOP)
                    .setTextAlignment(HorizontalAlignment.RIGHT)
                    .setColor(0xFF808080);
        }
    }

    protected void addCookTime(IRecipeExtrasBuilder builder, SmeltingRecipe recipe) {
        int cookTime = recipe.getCookingTime() / 2;
        if (cookTime <= 0) {
            cookTime = DEFAULT_COOK_TIME;
        }

        int cookTimeSeconds = cookTime / 20;
        Component timeString = Component.translatable("gui.jei.category.smelting.time.seconds", cookTimeSeconds);
        builder.addText(timeString, getWidth() - 20, 10)
                .setPosition(0, 0, getWidth(), getHeight(), HorizontalAlignment.RIGHT, VerticalAlignment.BOTTOM)
                .setTextAlignment(HorizontalAlignment.RIGHT)
                .setTextAlignment(VerticalAlignment.BOTTOM)
                .setColor(0xFF808080);
    }
}
