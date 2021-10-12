package de.melanx.utilitix.data;

import de.melanx.utilitix.UtilitiX;
import de.melanx.utilitix.data.recipe.BreweryRecipeBuilder;
import de.melanx.utilitix.recipe.EffectTransformer;
import de.melanx.utilitix.registration.ModBlocks;
import de.melanx.utilitix.registration.ModEntities;
import de.melanx.utilitix.registration.ModItems;
import io.github.noeppi_noeppi.libx.annotation.data.Datagen;
import io.github.noeppi_noeppi.libx.data.provider.recipe.RecipeProviderBase;
import io.github.noeppi_noeppi.libx.mod.ModX;
import net.minecraft.ChatFormatting;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.common.Tags;

import java.util.function.Consumer;

@Datagen
public class RecipeProvider extends RecipeProviderBase {

    public RecipeProvider(ModX mod, DataGenerator generator) {
        super(mod, generator);
    }

    @Override
    protected void setup() {
        this.removeNbt(this.consumer(), ModItems.linkedCrystal);

        this.createTinyCoalRecipe(this.consumer(), Items.COAL, ModItems.tinyCoal);
        this.createTinyCoalRecipe(this.consumer(), Items.CHARCOAL, ModItems.tinyCharcoal);
        this.createBellRecipes(this.consumer());
        this.createMiscRecipes(this.consumer());
        this.createRedstoneRecipes(this.consumer());
        this.createBreweryRecipes(this.consumer());
        this.createRailRecipes(this.consumer());
        this.createCartRecipes(this.consumer());
        this.createShearsRecipes(this.consumer());
    }

    private void createTinyCoalRecipe(Consumer<FinishedRecipe> consumer, ItemLike coal, ItemLike tinyCoal) {

        ShapelessRecipeBuilder.shapeless(tinyCoal, 8)
                .requires(coal)
                .unlockedBy("has_item", has(coal))
                .save(consumer, this.loc(tinyCoal, "to_tiny"));

        ShapelessRecipeBuilder.shapeless(coal)
                .requires(tinyCoal, 8)
                .unlockedBy("has_item", has(tinyCoal))
                .save(consumer, this.loc(tinyCoal, "from_tiny"));
    }

    private void createBellRecipes(Consumer<FinishedRecipe> consumer) {

        ShapedRecipeBuilder.shaped(ModItems.handBell)
                .pattern(" S")
                .pattern("SB")
                .define('S', Tags.Items.RODS_WOODEN)
                .define('B', Items.BELL)
                .unlockedBy("has_bell", has(Items.BELL))
                .save(consumer);

        ShapedRecipeBuilder.shaped(ModItems.mobBell)
                .pattern("123")
                .pattern("456")
                .pattern("789")
                .define('1', Items.SPIDER_EYE)
                .define('2', Items.GHAST_TEAR)
                .define('3', Tags.Items.BONES)
                .define('4', Tags.Items.GUNPOWDER)
                .define('5', ModItems.handBell)
                .define('6', Tags.Items.DUSTS_GLOWSTONE)
                .define('7', Items.BLAZE_POWDER)
                .define('8', Tags.Items.ENDER_PEARLS)
                .define('9', Items.ROTTEN_FLESH)
                .unlockedBy("has_bell", has(ModItems.handBell))
                .save(consumer);
    }

    private void createMiscRecipes(Consumer<FinishedRecipe> consumer) {

        ShapedRecipeBuilder.shaped(ModItems.armedStand)
                .pattern(" s ")
                .pattern(" a ")
                .pattern("s s")
                .define('a', Items.ARMOR_STAND)
                .define('s', Tags.Items.RODS_WOODEN)
                .unlockedBy("has_item0", has(Items.ARMOR_STAND))
                .unlockedBy("has_item1", has(Tags.Items.RODS_WOODEN))
                .save(consumer);

        ShapedRecipeBuilder.shaped(ModItems.gildingCrystal)
                .pattern(" g ")
                .pattern("gmg")
                .pattern(" g ")
                .define('g', Tags.Items.INGOTS_GOLD)
                .define('m', Items.PHANTOM_MEMBRANE)
                .unlockedBy("has_item0", has(Tags.Items.INGOTS_GOLD))
                .unlockedBy("has_item1", has(Items.PHANTOM_MEMBRANE))
                .save(consumer);


        ShapelessRecipeBuilder.shapeless(ModItems.glueBall, 4)
                .requires(Tags.Items.SLIMEBALLS)
                .requires(Tags.Items.SLIMEBALLS)
                .unlockedBy("has_item", has(Tags.Items.SLIMEBALLS))
                .save(consumer);

        ShapedRecipeBuilder.shaped(ModBlocks.experienceCrystal)
                .pattern("geg")
                .pattern("exe")
                .pattern("ccc")
                .define('g', Tags.Items.GLASS_LIME)
                .define('e', Tags.Items.GEMS_EMERALD)
                .define('x', Items.EXPERIENCE_BOTTLE)
                .define('c', Items.BLACK_CONCRETE)
                .unlockedBy("has_item", has(Items.EXPERIENCE_BOTTLE))
                .save(consumer);

        ShapedRecipeBuilder.shaped(ModBlocks.crudeFurnace)
                .pattern("C C")
                .pattern("SFS")
                .pattern("SSS")
                .define('C', Tags.Items.STONE)
                .define('S', Tags.Items.COBBLESTONE)
                .define('F', Items.FURNACE)
                .unlockedBy("has_item", has(Items.FURNACE))
                .save(consumer);
    }

    private void createRedstoneRecipes(Consumer<FinishedRecipe> consumer) {

        ShapedRecipeBuilder.shaped(ModBlocks.weakRedstoneTorch, 2)
                .pattern("R")
                .pattern("S")
                .pattern("S")
                .define('R', Tags.Items.DUSTS_REDSTONE)
                .define('S', Tags.Items.RODS_WOODEN)
                .unlockedBy("has_redstone", has(Tags.Items.DUSTS_REDSTONE))
                .save(consumer);

        ShapedRecipeBuilder.shaped(ModBlocks.comparatorRedirectorUp)
                .pattern("sis")
                .pattern("s s")
                .pattern("sts")
                .define('t', Items.REDSTONE_TORCH)
                .define('s', Tags.Items.COBBLESTONE)
                .define('i', Tags.Items.INGOTS_IRON)
                .unlockedBy("has_item0", has(Items.REDSTONE_TORCH))
                .unlockedBy("has_item1", has(Tags.Items.COBBLESTONE))
                .unlockedBy("has_item2", has(Tags.Items.INGOTS_IRON))
                .save(consumer);

        ShapedRecipeBuilder.shaped(ModBlocks.comparatorRedirectorDown)
                .pattern("sts")
                .pattern("s s")
                .pattern("sis")
                .define('t', Items.REDSTONE_TORCH)
                .define('s', Tags.Items.COBBLESTONE)
                .define('i', Tags.Items.INGOTS_IRON)
                .unlockedBy("has_item0", has(Items.REDSTONE_TORCH))
                .unlockedBy("has_item1", has(Tags.Items.COBBLESTONE))
                .unlockedBy("has_item2", has(Tags.Items.INGOTS_IRON))
                .save(consumer);

        ShapelessRecipeBuilder.shapeless(ModBlocks.comparatorRedirectorUp)
                .requires(ModBlocks.comparatorRedirectorDown)
                .unlockedBy("has_item0", has(ModBlocks.comparatorRedirectorDown))
                .save(consumer, this.loc(ModBlocks.comparatorRedirectorUp, "flip"));

        ShapelessRecipeBuilder.shapeless(ModBlocks.comparatorRedirectorDown)
                .requires(ModBlocks.comparatorRedirectorUp)
                .unlockedBy("has_item0", has(ModBlocks.comparatorRedirectorUp))
                .save(consumer, this.loc(ModBlocks.comparatorRedirectorDown, "flip"));

        ShapedRecipeBuilder.shaped(ModItems.linkedCrystal)
                .pattern(" r ")
                .pattern("rgr")
                .pattern(" r ")
                .define('r', Tags.Items.DUSTS_REDSTONE)
                .define('g', Tags.Items.GEMS_EMERALD)
                .unlockedBy("has_item0", has(Tags.Items.DUSTS_REDSTONE))
                .unlockedBy("has_item1", has(Tags.Items.GEMS_EMERALD))
                .save(consumer);

        ShapedRecipeBuilder.shaped(ModBlocks.linkedRepeater)
                .pattern("r t")
                .pattern("sss")
                .define('r', Tags.Items.DUSTS_REDSTONE)
                .define('t', Items.REDSTONE_TORCH)
                .define('s', Tags.Items.STONE)
                .unlockedBy("has_item0", has(Tags.Items.DUSTS_REDSTONE))
                .unlockedBy("has_item1", has(Items.REDSTONE_TORCH))
                .unlockedBy("has_item2", has(Tags.Items.STONE))
                .save(consumer);
    }

    private void createBreweryRecipes(Consumer<FinishedRecipe> consumer) {

        ShapedRecipeBuilder.shaped(ModBlocks.advancedBrewery)
                .pattern(" g ")
                .pattern("isi")
                .pattern("bbb")
                .define('s', Items.BREWING_STAND)
                .define('b', Items.SMOOTH_STONE)
                .define('i', Tags.Items.INGOTS_IRON)
                .define('g', Tags.Items.INGOTS_GOLD)
                .unlockedBy("has_item0", has(Items.BREWING_STAND))
                .unlockedBy("has_item1", has(Items.SMOOTH_STONE))
                .unlockedBy("has_item2", has(Tags.Items.INGOTS_IRON))
                .unlockedBy("has_item3", has(Tags.Items.INGOTS_GOLD))
                .save(consumer);

        BreweryRecipeBuilder.breweryRecipe()
                .input(Items.GOLDEN_APPLE)
                .action(new EffectTransformer.Apply(
                        new TranslatableComponent("item." + UtilitiX.getInstance().modid + ".apple_juice").withStyle(ChatFormatting.GREEN),
                        new MobEffectInstance(MobEffects.REGENERATION, 100, 1),
                        new MobEffectInstance(MobEffects.ABSORPTION, 2400, 0)
                ))
                .build(consumer, new ResourceLocation(UtilitiX.getInstance().modid, "apple_juice"));

        BreweryRecipeBuilder.breweryRecipe()
                .input(Items.ENCHANTED_GOLDEN_APPLE)
                .action(new EffectTransformer.Apply(
                        new TranslatableComponent("item." + UtilitiX.getInstance().modid + ".god_apple_juice").withStyle(ChatFormatting.GREEN),
                        new MobEffectInstance(MobEffects.REGENERATION, 400, 1),
                        new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 6000, 0),
                        new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 6000, 0),
                        new MobEffectInstance(MobEffects.ABSORPTION, 2400, 3)
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

    private void createRailRecipes(Consumer<FinishedRecipe> consumer) {

        ShapedRecipeBuilder.shaped(ModBlocks.highspeedRail, 3)
                .pattern("e e")
                .pattern("ese")
                .pattern("ere")
                .define('e', Tags.Items.GEMS_EMERALD)
                .define('s', Tags.Items.RODS_WOODEN)
                .define('r', Tags.Items.DUSTS_REDSTONE)
                .unlockedBy("has_item0", has(Tags.Items.GEMS_EMERALD))
                .unlockedBy("has_item1", has(Tags.Items.RODS_WOODEN))
                .unlockedBy("has_item2", has(Tags.Items.DUSTS_REDSTONE))
                .save(consumer);

        ShapedRecipeBuilder.shaped(ModBlocks.directionalRail, 6)
                .pattern("gig")
                .pattern("gsg")
                .pattern("grg")
                .define('g', Tags.Items.INGOTS_GOLD)
                .define('s', Tags.Items.RODS_WOODEN)
                .define('r', Tags.Items.DUSTS_REDSTONE)
                .define('i', Tags.Items.NUGGETS_IRON)
                .unlockedBy("has_item0", has(Tags.Items.INGOTS_GOLD))
                .unlockedBy("has_item1", has(Tags.Items.RODS_WOODEN))
                .unlockedBy("has_item2", has(Tags.Items.DUSTS_REDSTONE))
                .unlockedBy("has_item3", has(Tags.Items.NUGGETS_IRON))
                .save(consumer);

        ShapedRecipeBuilder.shaped(ModBlocks.directionalHighspeedRail, 3)
                .pattern("eie")
                .pattern("ese")
                .pattern("ere")
                .define('e', Tags.Items.GEMS_EMERALD)
                .define('s', Tags.Items.RODS_WOODEN)
                .define('r', Tags.Items.DUSTS_REDSTONE)
                .define('i', Tags.Items.NUGGETS_IRON)
                .unlockedBy("has_item0", has(Tags.Items.GEMS_EMERALD))
                .unlockedBy("has_item1", has(Tags.Items.RODS_WOODEN))
                .unlockedBy("has_item2", has(Tags.Items.DUSTS_REDSTONE))
                .unlockedBy("has_item3", has(Tags.Items.NUGGETS_IRON))
                .save(consumer);

        ShapedRecipeBuilder.shaped(ModBlocks.crossingRail, 4)
                .pattern(" r ")
                .pattern("rrr")
                .pattern(" r ")
                .define('r', Items.RAIL)
                .unlockedBy("has_item", has(Items.RAIL))
                .save(consumer);

        ShapedRecipeBuilder.shaped(ModBlocks.filterRail, 2)
                .pattern("r ")
                .pattern("nr")
                .pattern("r ")
                .define('r', Items.RAIL)
                .define('n', Tags.Items.NUGGETS_IRON)
                .unlockedBy("has_item0", has(Items.RAIL))
                .unlockedBy("has_item1", has(Tags.Items.NUGGETS_IRON))
                .save(consumer);

        ShapedRecipeBuilder.shaped(ModItems.minecartTinkerer)
                .pattern(" nm")
                .pattern(" in")
                .pattern("i  ")
                .define('i', Tags.Items.INGOTS_IRON)
                .define('n', Tags.Items.NUGGETS_IRON)
                .define('m', Items.MINECART)
                .unlockedBy("has_item0", has(Tags.Items.INGOTS_IRON))
                .unlockedBy("has_item1", has(Tags.Items.NUGGETS_IRON))
                .unlockedBy("has_item2", has(Items.MINECART))
                .save(consumer);

        ShapedRecipeBuilder.shaped(ModBlocks.reinforcedRail, 16)
                .pattern("i i")
                .pattern("lsl")
                .pattern("i i")
                .define('i', Tags.Items.INGOTS_IRON)
                .define('l', Tags.Items.GEMS_LAPIS)
                .define('s', Tags.Items.RODS_WOODEN)
                .unlockedBy("has_item0", has(Tags.Items.INGOTS_IRON))
                .unlockedBy("has_item1", has(Tags.Items.GEMS_LAPIS))
                .unlockedBy("has_item2", has(Tags.Items.RODS_WOODEN))
                .save(consumer);

        ShapedRecipeBuilder.shaped(ModBlocks.reinforcedCrossingRail, 4)
                .pattern(" r ")
                .pattern("rrr")
                .pattern(" r ")
                .define('r', ModBlocks.reinforcedRail)
                .unlockedBy("has_item", has(ModBlocks.reinforcedRail))
                .save(consumer);

        ShapedRecipeBuilder.shaped(ModBlocks.reinforcedFilterRail, 2)
                .pattern("r ")
                .pattern("nr")
                .pattern("r ")
                .define('r', ModBlocks.reinforcedRail)
                .define('n', Tags.Items.NUGGETS_IRON)
                .unlockedBy("has_item0", has(ModBlocks.reinforcedRail))
                .unlockedBy("has_item1", has(Tags.Items.NUGGETS_IRON))
                .save(consumer);

        this.controllerRail(ModBlocks.pistonControllerRail, ModBlocks.reinforcedPistonControllerRail, ModEntities.pistonCart.item(), consumer);
    }

    @SuppressWarnings("SameParameterValue")
    private void controllerRail(ItemLike rail, ItemLike reinforcedRail, ItemLike cart, Consumer<FinishedRecipe> consumer) {
        ShapedRecipeBuilder.shaped(rail, 8)
                .pattern("rar")
                .pattern("aca")
                .pattern("rar")
                .define('a', Items.ACTIVATOR_RAIL)
                .define('r', Items.RAIL)
                .define('c', cart)
                .unlockedBy("has_item0", has(Items.ACTIVATOR_RAIL))
                .unlockedBy("has_item1", has(Items.RAIL))
                .unlockedBy("has_item2", has(cart))
                .save(consumer);

        ShapedRecipeBuilder.shaped(reinforcedRail, 8)
                .pattern("rar")
                .pattern("aca")
                .pattern("rar")
                .define('a', Items.ACTIVATOR_RAIL)
                .define('r', ModBlocks.reinforcedRail)
                .define('c', cart)
                .unlockedBy("has_item0", has(Items.ACTIVATOR_RAIL))
                .unlockedBy("has_item1", has(ModBlocks.reinforcedRail))
                .unlockedBy("has_item2", has(cart))
                .save(consumer);
    }

    private void createCartRecipes(Consumer<FinishedRecipe> consumer) {
        this.cart(ModEntities.enderCart.item(), Items.ENDER_CHEST, consumer);
        this.cart(ModEntities.pistonCart.item(), Items.PISTON, consumer);
        this.cart(ModEntities.stonecutterCart.item(), Items.STONECUTTER, consumer);
        this.cart(ModEntities.anvilCart.item(), Items.ANVIL, consumer);
    }

    private void createShearsRecipes(Consumer<FinishedRecipe> consumer) {
        ShapedRecipeBuilder.shaped(ModItems.diamondShears)
                .pattern(" I")
                .pattern("I ")
                .define('I', Tags.Items.GEMS_DIAMOND)
                .unlockedBy("has_item", has(Tags.Items.GEMS_DIAMOND))
                .save(consumer);
    }

    private void cart(ItemLike cart, ItemLike content, Consumer<FinishedRecipe> consumer) {
        ShapedRecipeBuilder.shaped(cart)
                .pattern("i")
                .pattern("c")
                .define('i', content)
                .define('c', Items.MINECART)
                .unlockedBy("has_item0", has(Items.MINECART))
                .unlockedBy("has_item1", has(content))
                .save(consumer);
    }

    private void removeNbt(Consumer<FinishedRecipe> consumer, ItemLike item) {
        ShapelessRecipeBuilder.shapeless(item)
                .requires(item)
                .unlockedBy("has_item", has(item))
                .save(consumer, this.loc(item, "remove_nbt"));
    }
}
