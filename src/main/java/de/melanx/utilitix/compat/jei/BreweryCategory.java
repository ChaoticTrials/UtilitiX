package de.melanx.utilitix.compat.jei;

import com.mojang.blaze3d.vertex.PoseStack;
import de.melanx.utilitix.UtilitiX;
import de.melanx.utilitix.recipe.BreweryRecipe;
import de.melanx.utilitix.registration.ModBlocks;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.ITickTimer;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.drawable.IDrawableAnimated;
import mezz.jei.api.gui.drawable.IDrawableStatic;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;

import javax.annotation.Nonnull;

public class BreweryCategory implements IRecipeCategory<BreweryRecipe> {

    public static final ResourceLocation ID = new ResourceLocation(UtilitiX.getInstance().modid, "advanced_brewery");

    private final IDrawable background;
    private final IDrawable icon;
    private final IDrawable slot;
    private final Component localizedName;
    private final IDrawableAnimated arrow;
    private final IDrawableAnimated bubbles;
    private final IDrawableStatic blazeHeat;

    public BreweryCategory(IGuiHelper guiHelper) {
        ResourceLocation location = new ResourceLocation(UtilitiX.getInstance().modid, "textures/container/advanced_brewery.png");
        this.background = guiHelper.drawableBuilder(location, 55, 15, 64, 60).addPadding(1, 0, 0, 50).build();
        this.icon = guiHelper.createDrawableIngredient(VanillaTypes.ITEM, new ItemStack(ModBlocks.advancedBrewery));
        this.localizedName = new TranslatableComponent("screen.utilitix.advanced_brewery");
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
    public Component getTitle() {
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
    public void setRecipe(@Nonnull IRecipeLayoutBuilder builder, @Nonnull BreweryRecipe recipe, @Nonnull IFocusGroup focuses) {
        ItemStack stack = new ItemStack(Items.POTION);
        PotionUtils.setPotion(stack, Potions.AWKWARD);

        builder.addSlot(RecipeIngredientRole.OUTPUT, 24, 44)
                .addItemStack(stack);

        builder.addSlot(RecipeIngredientRole.INPUT, 24, 3);
        builder.addSlot(RecipeIngredientRole.OUTPUT, 81, 3)
                .addItemStack(recipe.getResultItem())
                .setBackground(this.slot, -1, -1);
    }

    @Override
    public void draw(@Nonnull BreweryRecipe recipe, @Nonnull IRecipeSlotsView slotsView, @Nonnull PoseStack poseStack, double mouseX, double mouseY) {
        this.blazeHeat.draw(poseStack, 5, 30);
        this.bubbles.draw(poseStack, 8, 0);
        this.arrow.draw(poseStack, 42, 2);
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
