package de.melanx.utilitix.jei;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.matrix.MatrixStack;
import de.melanx.utilitix.UtilitiX;
import de.melanx.utilitix.recipe.BreweryRecipe;
import de.melanx.utilitix.registration.ModBlocks;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.ITickTimer;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.drawable.IDrawableAnimated;
import mezz.jei.api.gui.drawable.IDrawableStatic;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.potion.PotionUtils;
import net.minecraft.potion.Potions;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;

public class BreweryCategory implements IRecipeCategory<BreweryRecipe> {

    public static final ResourceLocation ID = new ResourceLocation(UtilitiX.getInstance().modid, "advanced_brewery");

    private final IDrawable background;
    private final IDrawable icon;
    private final IDrawable slot;
    private final String localizedName;
    private final IDrawableAnimated arrow;
    private final IDrawableAnimated bubbles;
    private final IDrawableStatic blazeHeat;

    public BreweryCategory(IGuiHelper guiHelper) {
        ResourceLocation location = new ResourceLocation(UtilitiX.getInstance().modid, "textures/container/advanced_brewery.png");
        this.background = guiHelper.drawableBuilder(location, 55, 15, 64, 60).addPadding(1, 0, 0, 50).build();
        this.icon = guiHelper.createDrawableIngredient(new ItemStack(ModBlocks.advancedBrewery));
        this.localizedName = I18n.format("screen.utilitix.advanced_brewery");
        this.arrow = guiHelper.drawableBuilder(location, 176, 0, 9, 28).buildAnimated(400, IDrawableAnimated.StartDirection.TOP, false);
        ITickTimer bubblesTickTimer = new BubbleTimer(guiHelper);
        this.bubbles = guiHelper.drawableBuilder(location, 185, 0, 12, 29).buildAnimated(bubblesTickTimer, IDrawableAnimated.StartDirection.BOTTOM);
        this.blazeHeat = guiHelper.createDrawable(location, 176, 29, 18, 4);
        this.slot = guiHelper.getSlotDrawable();
    }

    @Nonnull
    @Override
    public ResourceLocation getUid() {
        return ID;
    }

    @Nonnull
    @Override
    public Class<? extends BreweryRecipe> getRecipeClass() {
        return BreweryRecipe.class;
    }

    @Nonnull
    @Override
    public String getTitle() {
        return this.localizedName;
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
    public void setIngredients(@Nonnull BreweryRecipe recipe, @Nonnull IIngredients ii) {
        ItemStack stack = new ItemStack(Items.POTION);
        PotionUtils.addPotionToItemStack(stack, Potions.AWKWARD);
        
        ii.setInputIngredients(recipe.getIngredients());
        ii.setOutputs(VanillaTypes.ITEM, ImmutableList.of(
                stack,
                recipe.getRecipeOutput()
        ));
    }

    @Override
    public void setRecipe(@Nonnull IRecipeLayout layout, @Nonnull BreweryRecipe recipe, @Nonnull IIngredients ii) {
        layout.getItemStacks().init(0, false, 23, 43);
        layout.getItemStacks().init(1, true, 23, 2);
        layout.getItemStacks().init(2, false, 80, 2);
        layout.getItemStacks().setBackground(2, this.slot);
        layout.getItemStacks().set(ii);
    }

    @Override
    public void draw(@Nonnull BreweryRecipe recipe, @Nonnull MatrixStack matrixStack, double mouseX, double mouseY) {
        this.blazeHeat.draw(matrixStack, 5, 30);
        this.bubbles.draw(matrixStack, 8, 0);
        this.arrow.draw(matrixStack, 42, 2);
    }

    private static class BubbleTimer implements ITickTimer {

        private static final int[] BUBBLE_SIZES = new int[]{29, 24, 20, 16, 11, 6, 0};

        private final ITickTimer timer;

        public BubbleTimer(IGuiHelper guiHelper) {
            this.timer = guiHelper.createTickTimer(14, BUBBLE_SIZES.length - 1, false);
        }

        @Override
        public int getValue() {
            return BUBBLE_SIZES[this.timer.getValue()];
        }

        @Override
        public int getMaxValue() {
            return BUBBLE_SIZES[0];
        }
    }
}
