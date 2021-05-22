package de.melanx.utilitix.data;

import de.melanx.utilitix.UtilitiX;
import de.melanx.utilitix.registration.ModBlocks;
import de.melanx.utilitix.registration.ModItems;
import io.github.noeppi_noeppi.libx.data.provider.recipe.RecipeProviderBase;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.data.ShapedRecipeBuilder;
import net.minecraft.data.ShapelessRecipeBuilder;
import net.minecraft.item.Items;
import net.minecraft.util.IItemProvider;
import net.minecraftforge.common.Tags;

import javax.annotation.Nonnull;
import java.util.function.Consumer;

public class RecipeProvider extends RecipeProviderBase {

    public RecipeProvider(DataGenerator generator) {
        super(UtilitiX.getInstance(), generator);
    }

    @Override
    protected void registerRecipes(@Nonnull Consumer<IFinishedRecipe> consumer) {
        this.createTinyCoalRecipe(consumer, Items.COAL, ModItems.tinyCoal);
        this.createTinyCoalRecipe(consumer, Items.CHARCOAL, ModItems.tinyCharcoal);
        this.createBellRecipes(consumer);
        this.createRedstoneRecipes(consumer);
    }

    private void createTinyCoalRecipe(Consumer<IFinishedRecipe> consumer, IItemProvider coal, IItemProvider tinyCoal) {
        ShapelessRecipeBuilder.shapelessRecipe(tinyCoal, 8)
                .addIngredient(coal)
                .addCriterion("has_item", hasItem(coal))
                .build(consumer, this.loc(tinyCoal, "to_tiny"));
        ShapelessRecipeBuilder.shapelessRecipe(coal)
                .addIngredient(tinyCoal, 8)
                .addCriterion("has_item", hasItem(tinyCoal))
                .build(consumer, this.loc(tinyCoal, "from_tiny"));
    }

    private void createBellRecipes(Consumer<IFinishedRecipe> consumer) {
        ShapedRecipeBuilder.shapedRecipe(ModItems.handBell)
                .patternLine(" S")
                .patternLine("SB")
                .key('S', ItemTagProvider.WOODEN_STICKS)
                .key('B', Items.BELL)
                .addCriterion("has_bell", hasItem(Items.BELL))
                .build(consumer, this.loc(ModItems.handBell));
        ShapedRecipeBuilder.shapedRecipe(ModItems.mobBell)
                .patternLine("123")
                .patternLine("456")
                .patternLine("789")
                .key('1', Items.SPIDER_EYE)
                .key('2', Items.GHAST_TEAR)
                .key('3', Tags.Items.BONES)
                .key('4', Tags.Items.GUNPOWDER)
                .key('5', ModItems.handBell)
                .key('6', Tags.Items.DUSTS_GLOWSTONE)
                .key('7', Items.BLAZE_POWDER)
                .key('8', Tags.Items.ENDER_PEARLS)
                .key('9', Items.ROTTEN_FLESH)
                .addCriterion("has_bell", hasItem(ModItems.handBell))
                .build(consumer, this.loc(ModItems.mobBell));
    }

    private void createRedstoneRecipes(Consumer<IFinishedRecipe> consumer) {
        ShapedRecipeBuilder.shapedRecipe(ModBlocks.weakRedstoneTorch, 2)
                .patternLine("R")
                .patternLine("S")
                .patternLine("S")
                .key('R', Tags.Items.DUSTS_REDSTONE)
                .key('S', ItemTagProvider.WOODEN_STICKS)
                .addCriterion("has_redstone", hasItem(Tags.Items.DUSTS_REDSTONE))
                .build(consumer, this.loc(ModBlocks.weakRedstoneTorch));
    }
}
