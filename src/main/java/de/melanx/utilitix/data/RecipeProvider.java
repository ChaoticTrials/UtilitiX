package de.melanx.utilitix.data;

import de.melanx.utilitix.UtilitiX;
import de.melanx.utilitix.data.recipe.BreweryRecipeBuilder;
import de.melanx.utilitix.recipe.EffectTransformer;
import de.melanx.utilitix.registration.ModBlocks;
import de.melanx.utilitix.registration.ModEntities;
import de.melanx.utilitix.registration.ModItems;
import io.github.noeppi_noeppi.libx.data.provider.recipe.RecipeProviderBase;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.data.ShapedRecipeBuilder;
import net.minecraft.data.ShapelessRecipeBuilder;
import net.minecraft.item.Items;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.common.Tags;

import javax.annotation.Nonnull;
import java.util.function.Consumer;

public class RecipeProvider extends RecipeProviderBase {

    public RecipeProvider(DataGenerator generator) {
        super(UtilitiX.getInstance(), generator);
    }

    @Override
    protected void registerRecipes(@Nonnull Consumer<IFinishedRecipe> consumer) {
        this.removeNbt(consumer, ModItems.linkedCrystal);

        this.createTinyCoalRecipe(consumer, Items.COAL, ModItems.tinyCoal);
        this.createTinyCoalRecipe(consumer, Items.CHARCOAL, ModItems.tinyCharcoal);
        this.createBellRecipes(consumer);
        this.createMiscRecipes(consumer);
        this.createRedstoneRecipes(consumer);
        this.createBreweryRecipes(consumer);
        this.createRailRecipes(consumer);
        this.createCartRecipes(consumer);
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
                .key('S', Tags.Items.RODS_WOODEN)
                .key('B', Items.BELL)
                .addCriterion("has_bell", hasItem(Items.BELL))
                .build(consumer);

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
                .build(consumer);
    }

    private void createMiscRecipes(Consumer<IFinishedRecipe> consumer) {

        ShapedRecipeBuilder.shapedRecipe(ModItems.armedStand)
                .patternLine(" s ")
                .patternLine(" a ")
                .patternLine("s s")
                .key('a', Items.ARMOR_STAND)
                .key('s', Tags.Items.RODS_WOODEN)
                .addCriterion("has_item0", hasItem(Items.ARMOR_STAND))
                .addCriterion("has_item1", hasItem(Tags.Items.RODS_WOODEN))
                .build(consumer);

        ShapedRecipeBuilder.shapedRecipe(ModItems.gildingCrystal)
                .patternLine(" g ")
                .patternLine("gmg")
                .patternLine(" g ")
                .key('g', Tags.Items.INGOTS_GOLD)
                .key('m', Items.PHANTOM_MEMBRANE)
                .addCriterion("has_item0", hasItem(Tags.Items.INGOTS_GOLD))
                .addCriterion("has_item1", hasItem(Items.PHANTOM_MEMBRANE))
                .build(consumer);

        
        ShapelessRecipeBuilder.shapelessRecipe(ModItems.glueBall, 4)
                .addIngredient(Tags.Items.SLIMEBALLS)
                .addIngredient(Tags.Items.SLIMEBALLS)
                .addCriterion("has_item", hasItem(Tags.Items.SLIMEBALLS))
                .build(consumer);
    }

    private void createRedstoneRecipes(Consumer<IFinishedRecipe> consumer) {

        ShapedRecipeBuilder.shapedRecipe(ModBlocks.weakRedstoneTorch, 2)
                .patternLine("R")
                .patternLine("S")
                .patternLine("S")
                .key('R', Tags.Items.DUSTS_REDSTONE)
                .key('S', Tags.Items.RODS_WOODEN)
                .addCriterion("has_redstone", hasItem(Tags.Items.DUSTS_REDSTONE))
                .build(consumer);

        ShapedRecipeBuilder.shapedRecipe(ModBlocks.comparatorRedirectorUp)
                .patternLine("sis")
                .patternLine("s s")
                .patternLine("sts")
                .key('t', Items.REDSTONE_TORCH)
                .key('s', Tags.Items.COBBLESTONE)
                .key('i', Tags.Items.INGOTS_IRON)
                .addCriterion("has_item0", hasItem(Items.REDSTONE_TORCH))
                .addCriterion("has_item1", hasItem(Tags.Items.COBBLESTONE))
                .addCriterion("has_item2", hasItem(Tags.Items.INGOTS_IRON))
                .build(consumer);

        ShapedRecipeBuilder.shapedRecipe(ModBlocks.comparatorRedirectorDown)
                .patternLine("sts")
                .patternLine("s s")
                .patternLine("sis")
                .key('t', Items.REDSTONE_TORCH)
                .key('s', Tags.Items.COBBLESTONE)
                .key('i', Tags.Items.INGOTS_IRON)
                .addCriterion("has_item0", hasItem(Items.REDSTONE_TORCH))
                .addCriterion("has_item1", hasItem(Tags.Items.COBBLESTONE))
                .addCriterion("has_item2", hasItem(Tags.Items.INGOTS_IRON))
                .build(consumer);

        ShapelessRecipeBuilder.shapelessRecipe(ModBlocks.comparatorRedirectorUp)
                .addIngredient(ModBlocks.comparatorRedirectorDown)
                .addCriterion("has_item0", hasItem(ModBlocks.comparatorRedirectorDown))
                .build(consumer, this.loc(ModBlocks.comparatorRedirectorUp, "flip"));

        ShapelessRecipeBuilder.shapelessRecipe(ModBlocks.comparatorRedirectorDown)
                .addIngredient(ModBlocks.comparatorRedirectorUp)
                .addCriterion("has_item0", hasItem(ModBlocks.comparatorRedirectorUp))
                .build(consumer, this.loc(ModBlocks.comparatorRedirectorDown, "flip"));

        ShapedRecipeBuilder.shapedRecipe(ModItems.linkedCrystal)
                .patternLine(" r ")
                .patternLine("rgr")
                .patternLine(" r ")
                .key('r', Tags.Items.DUSTS_REDSTONE)
                .key('g', Tags.Items.GEMS_EMERALD)
                .addCriterion("has_item0", hasItem(Tags.Items.DUSTS_REDSTONE))
                .addCriterion("has_item1", hasItem(Tags.Items.GEMS_EMERALD))
                .build(consumer);

        ShapedRecipeBuilder.shapedRecipe(ModBlocks.linkedRepeater)
                .patternLine("r t")
                .patternLine("sss")
                .key('r', Tags.Items.DUSTS_REDSTONE)
                .key('t', Items.REDSTONE_TORCH)
                .key('s', Tags.Items.STONE)
                .addCriterion("has_item0", hasItem(Tags.Items.DUSTS_REDSTONE))
                .addCriterion("has_item1", hasItem(Items.REDSTONE_TORCH))
                .addCriterion("has_item2", hasItem(Tags.Items.STONE))
                .build(consumer);
    }

    private void createBreweryRecipes(Consumer<IFinishedRecipe> consumer) {

        ShapedRecipeBuilder.shapedRecipe(ModBlocks.advancedBrewery)
                .patternLine(" g ")
                .patternLine("isi")
                .patternLine("bbb")
                .key('s', Items.BREWING_STAND)
                .key('b', Items.SMOOTH_STONE)
                .key('i', Tags.Items.INGOTS_IRON)
                .key('g', Tags.Items.INGOTS_GOLD)
                .addCriterion("has_item0", hasItem(Items.BREWING_STAND))
                .addCriterion("has_item1", hasItem(Items.SMOOTH_STONE))
                .addCriterion("has_item2", hasItem(Tags.Items.INGOTS_IRON))
                .addCriterion("has_item3", hasItem(Tags.Items.INGOTS_GOLD))
                .build(consumer);

        BreweryRecipeBuilder.breweryRecipe()
                .input(Items.GOLDEN_APPLE)
                .action(new EffectTransformer.Apply(
                        new TranslationTextComponent("item." + UtilitiX.getInstance().modid + ".apple_juice").mergeStyle(TextFormatting.GREEN),
                        new EffectInstance(Effects.REGENERATION, 100, 1),
                        new EffectInstance(Effects.ABSORPTION, 2400, 0)
                ))
                .build(consumer, new ResourceLocation(UtilitiX.getInstance().modid, "apple_juice"));

        BreweryRecipeBuilder.breweryRecipe()
                .input(Items.ENCHANTED_GOLDEN_APPLE)
                .action(new EffectTransformer.Apply(
                        new TranslationTextComponent("item." + UtilitiX.getInstance().modid + ".god_apple_juice").mergeStyle(TextFormatting.GREEN),
                        new EffectInstance(Effects.REGENERATION, 400, 1),
                        new EffectInstance(Effects.RESISTANCE, 6000, 0),
                        new EffectInstance(Effects.FIRE_RESISTANCE, 6000, 0),
                        new EffectInstance(Effects.ABSORPTION, 2400, 3)
                ))
                .build(consumer, new ResourceLocation(UtilitiX.getInstance().modid, "god_apple_juice"));

        BreweryRecipeBuilder.breweryRecipe()
                .action(new EffectTransformer.Merge(1))
                .build(consumer, new ResourceLocation(UtilitiX.getInstance().modid, "merge"));

        BreweryRecipeBuilder.breweryRecipe()
                .input(Items.NETHERITE_SCRAP)
                .action(new EffectTransformer.Clone())
                .build(consumer, new ResourceLocation(UtilitiX.getInstance().modid, "clone"));

        BreweryRecipeBuilder.breweryRecipe()
                .input(Items.POPPED_CHORUS_FRUIT)
                .action(new EffectTransformer.Upgrade(2))
                .build(consumer, new ResourceLocation(UtilitiX.getInstance().modid, "upgrade"));
    }

    private void createRailRecipes(Consumer<IFinishedRecipe> consumer) {

        ShapedRecipeBuilder.shapedRecipe(ModBlocks.highspeedRail, 3)
                .patternLine("e e")
                .patternLine("ese")
                .patternLine("ere")
                .key('e', Tags.Items.GEMS_EMERALD)
                .key('s', Tags.Items.RODS_WOODEN)
                .key('r', Tags.Items.DUSTS_REDSTONE)
                .addCriterion("has_item0", hasItem(Tags.Items.GEMS_EMERALD))
                .addCriterion("has_item1", hasItem(Tags.Items.RODS_WOODEN))
                .addCriterion("has_item2", hasItem(Tags.Items.DUSTS_REDSTONE))
                .build(consumer);

        ShapedRecipeBuilder.shapedRecipe(ModBlocks.directionalRail, 6)
                .patternLine("gig")
                .patternLine("gsg")
                .patternLine("grg")
                .key('g', Tags.Items.INGOTS_GOLD)
                .key('s', Tags.Items.RODS_WOODEN)
                .key('r', Tags.Items.DUSTS_REDSTONE)
                .key('i', Tags.Items.NUGGETS_IRON)
                .addCriterion("has_item0", hasItem(Tags.Items.INGOTS_GOLD))
                .addCriterion("has_item1", hasItem(Tags.Items.RODS_WOODEN))
                .addCriterion("has_item2", hasItem(Tags.Items.DUSTS_REDSTONE))
                .addCriterion("has_item3", hasItem(Tags.Items.NUGGETS_IRON))
                .build(consumer);

        ShapedRecipeBuilder.shapedRecipe(ModBlocks.directionalHighspeedRail, 3)
                .patternLine("eie")
                .patternLine("ese")
                .patternLine("ere")
                .key('e', Tags.Items.GEMS_EMERALD)
                .key('s', Tags.Items.RODS_WOODEN)
                .key('r', Tags.Items.DUSTS_REDSTONE)
                .key('i', Tags.Items.NUGGETS_IRON)
                .addCriterion("has_item0", hasItem(Tags.Items.GEMS_EMERALD))
                .addCriterion("has_item1", hasItem(Tags.Items.RODS_WOODEN))
                .addCriterion("has_item2", hasItem(Tags.Items.DUSTS_REDSTONE))
                .addCriterion("has_item3", hasItem(Tags.Items.NUGGETS_IRON))
                .build(consumer);

        ShapedRecipeBuilder.shapedRecipe(ModBlocks.crossingRail, 4)
                .patternLine(" r ")
                .patternLine("rrr")
                .patternLine(" r ")
                .key('r', Items.RAIL)
                .addCriterion("has_item", hasItem(Items.RAIL))
                .build(consumer);

        ShapedRecipeBuilder.shapedRecipe(ModBlocks.filterRail, 2)
                .patternLine("r ")
                .patternLine("nr")
                .patternLine("r ")
                .key('r', Items.RAIL)
                .key('n', Tags.Items.NUGGETS_IRON)
                .addCriterion("has_item0", hasItem(Items.RAIL))
                .addCriterion("has_item1", hasItem(Tags.Items.NUGGETS_IRON))
                .build(consumer);

        ShapedRecipeBuilder.shapedRecipe(ModItems.minecartTinkerer)
                .patternLine(" nm")
                .patternLine(" in")
                .patternLine("i  ")
                .key('i', Tags.Items.INGOTS_IRON)
                .key('n', Tags.Items.NUGGETS_IRON)
                .key('m', Items.MINECART)
                .addCriterion("has_item0", hasItem(Tags.Items.INGOTS_IRON))
                .addCriterion("has_item1", hasItem(Tags.Items.NUGGETS_IRON))
                .addCriterion("has_item2", hasItem(Items.MINECART))
                .build(consumer);
        
        ShapedRecipeBuilder.shapedRecipe(ModBlocks.reinforcedRail, 16)
                .patternLine("i i")
                .patternLine("lsl")
                .patternLine("i i")
                .key('i', Tags.Items.INGOTS_IRON)
                .key('l', Tags.Items.GEMS_LAPIS)
                .key('s', Tags.Items.RODS_WOODEN)
                .addCriterion("has_item0", hasItem(Tags.Items.INGOTS_IRON))
                .addCriterion("has_item1", hasItem(Tags.Items.GEMS_LAPIS))
                .addCriterion("has_item2", hasItem(Tags.Items.RODS_WOODEN))
                .build(consumer);

        ShapedRecipeBuilder.shapedRecipe(ModBlocks.reinforcedCrossingRail, 4)
                .patternLine(" r ")
                .patternLine("rrr")
                .patternLine(" r ")
                .key('r', ModBlocks.reinforcedRail)
                .addCriterion("has_item", hasItem(ModBlocks.reinforcedRail))
                .build(consumer);

        ShapedRecipeBuilder.shapedRecipe(ModBlocks.reinforcedFilterRail, 2)
                .patternLine("r ")
                .patternLine("nr")
                .patternLine("r ")
                .key('r', ModBlocks.reinforcedRail)
                .key('n', Tags.Items.NUGGETS_IRON)
                .addCriterion("has_item0", hasItem(ModBlocks.reinforcedRail))
                .addCriterion("has_item1", hasItem(Tags.Items.NUGGETS_IRON))
                .build(consumer);

        this.controllerRail(ModBlocks.pistonControllerRail, ModBlocks.reinforcedPistonControllerRail, ModEntities.pistonCart.item(), consumer);
    }

    @SuppressWarnings("SameParameterValue")
    private void controllerRail(IItemProvider rail, IItemProvider reinforcedRail, IItemProvider cart, Consumer<IFinishedRecipe> consumer) {
        ShapedRecipeBuilder.shapedRecipe(rail, 8)
                .patternLine("rar")
                .patternLine("aca")
                .patternLine("rar")
                .key('a', Items.ACTIVATOR_RAIL)
                .key('r', Items.RAIL)
                .key('c', cart)
                .addCriterion("has_item0", hasItem(Items.ACTIVATOR_RAIL))
                .addCriterion("has_item1", hasItem(Items.RAIL))
                .addCriterion("has_item2", hasItem(cart))
                .build(consumer);
        
        ShapedRecipeBuilder.shapedRecipe(reinforcedRail, 8)
                .patternLine("rar")
                .patternLine("aca")
                .patternLine("rar")
                .key('a', Items.ACTIVATOR_RAIL)
                .key('r', ModBlocks.reinforcedRail)
                .key('c', cart)
                .addCriterion("has_item0", hasItem(Items.ACTIVATOR_RAIL))
                .addCriterion("has_item1", hasItem(ModBlocks.reinforcedRail))
                .addCriterion("has_item2", hasItem(cart))
                .build(consumer);
    }

    private void createCartRecipes(Consumer<IFinishedRecipe> consumer) {
        this.cart(ModEntities.enderCart.item(), Items.ENDER_CHEST, consumer);
        this.cart(ModEntities.pistonCart.item(), Items.PISTON, consumer);
        this.cart(ModEntities.stonecutterCart.item(), Items.STONECUTTER, consumer);
        this.cart(ModEntities.anvilCart.item(), Items.ANVIL, consumer);
    }
    
    private void cart(IItemProvider cart, IItemProvider content, Consumer<IFinishedRecipe> consumer) {
        ShapedRecipeBuilder.shapedRecipe(cart)
                .patternLine("i")
                .patternLine("c")
                .key('i', content)
                .key('c', Items.MINECART)
                .addCriterion("has_item0", hasItem(Items.MINECART))
                .addCriterion("has_item1", hasItem(content))
                .build(consumer);
    }

    private void removeNbt(Consumer<IFinishedRecipe> consumer, IItemProvider item) {
        ShapelessRecipeBuilder.shapelessRecipe(item)
                .addIngredient(item)
                .addCriterion("has_item", hasItem(item))
                .build(consumer, this.loc(item, "remove_nbt"));
    }
}