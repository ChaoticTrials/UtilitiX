package de.melanx.utilitix.data;

import de.melanx.utilitix.UtilitiX;
import de.melanx.utilitix.data.recipe.BreweryRecipeBuilder;
import de.melanx.utilitix.recipe.EffectTransformer;
import de.melanx.utilitix.registration.ModBlocks;
import de.melanx.utilitix.registration.ModItems;
import de.melanx.utilitix.registration.ModRegisterables;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.common.Tags;
import net.minecraftforge.registries.ForgeRegistries;
import org.moddingx.libx.datagen.DatagenContext;
import org.moddingx.libx.datagen.provider.recipe.RecipeProviderBase;
import org.moddingx.libx.datagen.provider.recipe.StoneCuttingExtension;
import org.moddingx.libx.datagen.provider.recipe.crafting.CraftingExtension;

public class RecipeProvider extends RecipeProviderBase implements CraftingExtension, StoneCuttingExtension {

    public RecipeProvider(DatagenContext context) {
        super(context);
    }

    @Override
    protected void setup() {
        this.removeNbt(ModItems.linkedCrystal);

        this.createTinyCoalRecipe(Items.COAL, ModItems.tinyCoal);
        this.createTinyCoalRecipe(Items.CHARCOAL, ModItems.tinyCharcoal);
        this.createBellRecipes();
        this.createMiscRecipes();
        this.createRedstoneRecipes();
        this.createBreweryRecipes();
        this.createRailRecipes();
        this.createCartRecipes();
        this.createShearsRecipes();
        this.createBoatRecipes();
        this.wall(ModBlocks.stoneWall, Ingredient.of(Items.STONE));
        this.shaped(ModItems.mobYoinker, "CIC", "ILI", "III", 'I', Items.IRON_BARS, 'C', Items.COPPER_INGOT, 'L', Items.LEAD);
        this.shaped(ModBlocks.dimmableRedstoneLamp, 4, " L ", "LRL", " L ", 'L', Blocks.REDSTONE_LAMP, 'R', Tags.Items.DUSTS_REDSTONE);
        //noinspection ConstantConditions
        this.shapeless(UtilitiX.getInstance().resource(ForgeRegistries.BLOCKS.getKey(Blocks.REDSTONE_LAMP).getPath() + "_revert"), Blocks.REDSTONE_LAMP, ModBlocks.dimmableRedstoneLamp);
        this.shaped(ModItems.backpack, "SLS", "LCL", "LLL", 'L', Tags.Items.LEATHER, 'S', Tags.Items.STRING, 'C', Tags.Items.CHESTS_WOODEN);
    }

    private void createTinyCoalRecipe(ItemLike coal, ItemLike tinyCoal) {
        this.shapeless(this.loc(tinyCoal, "to_tiny"), tinyCoal, 8, coal);
        this.shapeless(this.loc(tinyCoal, "from_tiny"), coal, tinyCoal, tinyCoal, tinyCoal, tinyCoal, tinyCoal, tinyCoal, tinyCoal, tinyCoal);
    }

    private void createBellRecipes() {
        this.shaped(ModItems.handBell, " s", "sb", 's', Tags.Items.RODS_WOODEN, 'b', Items.BELL);
        this.shaped(ModItems.mobBell, "abc", "def", "ghi",
                'a', Items.SPIDER_EYE, 'b', Items.GHAST_TEAR, 'c', Tags.Items.BONES,
                'd', Tags.Items.GUNPOWDER, 'e', ModItems.handBell, 'f', Tags.Items.DUSTS_GLOWSTONE,
                'g', Items.BLAZE_POWDER, 'h', Tags.Items.ENDER_PEARLS, 'i', Items.ROTTEN_FLESH);
    }

    private void createMiscRecipes() {
        this.shaped(ModItems.armedStand,
                " s ",
                " a ",
                "s s",
                'a', Items.ARMOR_STAND,
                's', Tags.Items.RODS_WOODEN);
        this.shaped(ModItems.gildingCrystal,
                " g ",
                "gmg",
                " g ",
                'g', Tags.Items.INGOTS_GOLD,
                'm', Items.PHANTOM_MEMBRANE);
        this.shapeless(ModItems.glueBall, 4, Tags.Items.SLIMEBALLS, Tags.Items.SLIMEBALLS);
        this.shaped(ModBlocks.experienceCrystal,
                "geg",
                "exe",
                "ccc",
                'g', Tags.Items.GLASS_LIME,
                'e', Tags.Items.GEMS_EMERALD,
                'x', Items.EXPERIENCE_BOTTLE,
                'c', Items.BLACK_CONCRETE);
        this.shaped(ModBlocks.crudeFurnace,
                "C C",
                "SFS",
                "SSS",
                'C', Tags.Items.STONE,
                'S', Tags.Items.COBBLESTONE,
                'F', Items.FURNACE);
    }

    private void createRedstoneRecipes() {
        this.shaped(ModBlocks.weakRedstoneTorch, 2,
                "R",
                "S",
                "S",
                'R', Tags.Items.DUSTS_REDSTONE,
                'S', Tags.Items.RODS_WOODEN);
        this.shaped(ModBlocks.comparatorRedirectorUp,
                "sis",
                "s s",
                "sts",
                't', Items.REDSTONE_TORCH,
                's', Tags.Items.COBBLESTONE,
                'i', Tags.Items.INGOTS_IRON);
        this.shaped(ModBlocks.comparatorRedirectorDown,
                "sts",
                "s s",
                "sis",
                't', Items.REDSTONE_TORCH,
                's', Tags.Items.COBBLESTONE,
                'i', Tags.Items.INGOTS_IRON);
        this.shapeless(this.loc(ModBlocks.comparatorRedirectorUp, "flip"), ModBlocks.comparatorRedirectorUp, ModBlocks.comparatorRedirectorDown);
        this.shapeless(this.loc(ModBlocks.comparatorRedirectorDown, "flip"), ModBlocks.comparatorRedirectorDown, ModBlocks.comparatorRedirectorUp);
        this.shaped(ModItems.linkedCrystal,
                " r ",
                "rgr",
                " r ",
                'r', Tags.Items.DUSTS_REDSTONE,
                'g', Tags.Items.GEMS_EMERALD);
        this.shaped(ModBlocks.linkedRepeater,
                "r t",
                "sss",
                'r', Tags.Items.DUSTS_REDSTONE,
                't', Items.REDSTONE_TORCH,
                's', Tags.Items.STONE);
    }

    private void createBreweryRecipes() {
        this.shaped(ModBlocks.advancedBrewery,
                " g ",
                "isi",
                "bbb",
                's', Items.BREWING_STAND,
                'b', Items.SMOOTH_STONE,
                'i', Tags.Items.INGOTS_IRON,
                'g', Tags.Items.INGOTS_GOLD);
        BreweryRecipeBuilder.breweryRecipe()
                .input(Items.GOLDEN_APPLE)
                .action(new EffectTransformer.Apply(
                        Component.translatable("item." + UtilitiX.getInstance().modid + ".apple_juice").withStyle(ChatFormatting.GREEN),
                        new MobEffectInstance(MobEffects.REGENERATION, 100, 1),
                        new MobEffectInstance(MobEffects.ABSORPTION, 2400, 0)
                ))
                .build(this.consumer(), new ResourceLocation(UtilitiX.getInstance().modid, "apple_juice"));
        BreweryRecipeBuilder.breweryRecipe()
                .input(Items.ENCHANTED_GOLDEN_APPLE)
                .action(new EffectTransformer.Apply(
                        Component.translatable("item." + UtilitiX.getInstance().modid + ".god_apple_juice").withStyle(ChatFormatting.GREEN),
                        new MobEffectInstance(MobEffects.REGENERATION, 400, 1),
                        new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 6000, 0),
                        new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 6000, 0),
                        new MobEffectInstance(MobEffects.ABSORPTION, 2400, 3)
                ))
                .build(this.consumer(), new ResourceLocation(UtilitiX.getInstance().modid, "god_apple_juice"));
        BreweryRecipeBuilder.breweryRecipe()
                .action(new EffectTransformer.Merge(1))
                .build(this.consumer(), new ResourceLocation(UtilitiX.getInstance().modid, "merge"));
        BreweryRecipeBuilder.breweryRecipe()
                .input(Items.NETHERITE_SCRAP)
                .action(new EffectTransformer.Clone())
                .build(this.consumer(), new ResourceLocation(UtilitiX.getInstance().modid, "clone"));
        BreweryRecipeBuilder.breweryRecipe()
                .input(Items.POPPED_CHORUS_FRUIT)
                .action(new EffectTransformer.Upgrade(2))
                .build(this.consumer(), new ResourceLocation(UtilitiX.getInstance().modid, "upgrade"));
    }

    private void createRailRecipes() {
        this.shaped(ModBlocks.highspeedRail, 3,
                "e e",
                "ese",
                "ere",
                'e', Tags.Items.GEMS_EMERALD,
                's', Tags.Items.RODS_WOODEN,
                'r', Tags.Items.DUSTS_REDSTONE);
        this.shaped(ModBlocks.directionalRail, 6,
                "gig",
                "gsg",
                "grg",
                'g', Tags.Items.INGOTS_GOLD,
                's', Tags.Items.RODS_WOODEN,
                'r', Tags.Items.DUSTS_REDSTONE,
                'i', Tags.Items.NUGGETS_IRON);
        this.shaped(ModBlocks.directionalHighspeedRail, 3,
                "eie",
                "ese",
                "ere",
                'e', Tags.Items.GEMS_EMERALD,
                's', Tags.Items.RODS_WOODEN,
                'r', Tags.Items.DUSTS_REDSTONE,
                'i', Tags.Items.NUGGETS_IRON);
        this.shaped(ModBlocks.crossingRail, 4,
                " r ",
                "rrr",
                " r ",
                'r', Items.RAIL);
        this.shaped(ModBlocks.filterRail, 2,
                "r ",
                "nr",
                "r ",
                'r', Items.RAIL,
                'n', Tags.Items.NUGGETS_IRON);
        this.shaped(ModItems.minecartTinkerer,
                " nm",
                " in",
                "i  ",
                'i', Tags.Items.INGOTS_IRON,
                'n', Tags.Items.NUGGETS_IRON,
                'm', Items.MINECART);
        this.shaped(ModBlocks.reinforcedRail, 16,
                "i i",
                "lsl",
                "i i",
                'i', Tags.Items.INGOTS_IRON,
                'l', Tags.Items.GEMS_LAPIS,
                's', Tags.Items.RODS_WOODEN);

        this.shaped(ModBlocks.reinforcedCrossingRail, 4,
                " r ",
                "rrr",
                " r ",
                'r', ModBlocks.reinforcedRail);

        this.shaped(ModBlocks.reinforcedFilterRail, 2,
                "r ",
                "nr",
                "r ",
                'r', ModBlocks.reinforcedRail,
                'n', Tags.Items.NUGGETS_IRON);

        this.controllerRail(ModBlocks.pistonControllerRail, ModBlocks.reinforcedPistonControllerRail, ModRegisterables.pistonCart.item());
    }

    @SuppressWarnings("SameParameterValue")
    private void controllerRail(ItemLike rail, ItemLike reinforcedRail, ItemLike cart) {
        this.shaped(rail, 8,
                "rar",
                "aca",
                "rar",
                'a', Items.ACTIVATOR_RAIL,
                'r', Items.RAIL,
                'c', cart);
        this.shaped(reinforcedRail, 8,
                "rar",
                "aca",
                "rar",
                'a', Items.ACTIVATOR_RAIL,
                'r', ModBlocks.reinforcedRail,
                'c', cart);
    }

    private void createCartRecipes() {
        this.cart(ModRegisterables.enderCart.item(), Items.ENDER_CHEST);
        this.cart(ModRegisterables.pistonCart.item(), Items.PISTON);
        this.cart(ModRegisterables.stonecutterCart.item(), Items.STONECUTTER);
        this.cart(ModRegisterables.anvilCart.item(), Items.ANVIL);
    }

    private void createShearsRecipes() {
        this.shaped(ModItems.diamondShears,
                " I",
                "I ",
                'I', Tags.Items.GEMS_DIAMOND);
    }

    private void createBoatRecipes() {
        Ingredient shulker = Ingredient.of(Items.SHULKER_BOX, Items.BLACK_SHULKER_BOX, Items.BLUE_SHULKER_BOX, Items.BROWN_SHULKER_BOX, Items.CYAN_SHULKER_BOX, Items.GRAY_SHULKER_BOX, Items.GREEN_SHULKER_BOX, Items.LIGHT_BLUE_SHULKER_BOX, Items.LIGHT_GRAY_SHULKER_BOX, Items.LIME_SHULKER_BOX, Items.MAGENTA_SHULKER_BOX, Items.ORANGE_SHULKER_BOX, Items.PINK_SHULKER_BOX, Items.PURPLE_SHULKER_BOX, Items.RED_SHULKER_BOX, Items.WHITE_SHULKER_BOX, Items.YELLOW_SHULKER_BOX);
        this.shapeless(ModItems.oakShulkerBoat, shulker, Items.OAK_BOAT);
        this.shapeless(ModItems.spruceShulkerBoat, shulker, Items.SPRUCE_BOAT);
        this.shapeless(ModItems.birchShulkerBoat, shulker, Items.BIRCH_BOAT);
        this.shapeless(ModItems.jungleShulkerBoat, shulker, Items.JUNGLE_BOAT);
        this.shapeless(ModItems.acaciaShulkerBoat, shulker, Items.ACACIA_BOAT);
        this.shapeless(ModItems.cherryShulkerBoat, shulker, Items.CHERRY_BOAT);
        this.shapeless(ModItems.darkOakShulkerBoat, shulker, Items.DARK_OAK_BOAT);
        this.shapeless(ModItems.mangroveShulkerBoat, shulker, Items.MANGROVE_BOAT);
        this.shapeless(ModItems.bambooShulkerRaft, shulker, Items.BAMBOO_RAFT);

        this.shaped(this.loc(ModItems.oakShulkerBoat, "with_shell"), ModItems.oakShulkerBoat, "s", "b", "s", 's', Items.SHULKER_SHELL, 'b', Items.OAK_CHEST_BOAT);
        this.shaped(this.loc(ModItems.spruceShulkerBoat, "with_shell"), ModItems.spruceShulkerBoat, "s", "b", "s", 's', Items.SHULKER_SHELL, 'b', Items.SPRUCE_CHEST_BOAT);
        this.shaped(this.loc(ModItems.birchShulkerBoat, "with_shell"), ModItems.birchShulkerBoat, "s", "b", "s", 's', Items.SHULKER_SHELL, 'b', Items.BIRCH_CHEST_BOAT);
        this.shaped(this.loc(ModItems.jungleShulkerBoat, "with_shell"), ModItems.jungleShulkerBoat, "s", "b", "s", 's', Items.SHULKER_SHELL, 'b', Items.JUNGLE_CHEST_BOAT);
        this.shaped(this.loc(ModItems.acaciaShulkerBoat, "with_shell"), ModItems.acaciaShulkerBoat, "s", "b", "s", 's', Items.SHULKER_SHELL, 'b', Items.ACACIA_CHEST_BOAT);
        this.shaped(this.loc(ModItems.cherryShulkerBoat, "with_shell"), ModItems.cherryShulkerBoat, "s", "b", "s", 's', Items.SHULKER_SHELL, 'b', Items.CHERRY_CHEST_BOAT);
        this.shaped(this.loc(ModItems.darkOakShulkerBoat, "with_shell"), ModItems.darkOakShulkerBoat, "s", "b", "s", 's', Items.SHULKER_SHELL, 'b', Items.DARK_OAK_CHEST_BOAT);
        this.shaped(this.loc(ModItems.mangroveShulkerBoat, "with_shell"), ModItems.mangroveShulkerBoat, "s", "b", "s", 's', Items.SHULKER_SHELL, 'b', Items.MANGROVE_CHEST_BOAT);
        this.shaped(this.loc(ModItems.bambooShulkerRaft, "with_shell"), ModItems.bambooShulkerRaft, "s", "b", "s", 's', Items.SHULKER_SHELL, 'b', Items.BAMBOO_CHEST_RAFT);
    }

    private void cart(ItemLike cart, ItemLike content) {
        this.shaped(cart,
                "i",
                "c",
                'i', content,
                'c', Items.MINECART);
    }

    private void wall(ItemLike wall, Ingredient ingredient) {
        this.shaped(wall, "XXX", "XXX", 'X', ingredient);
        this.stoneCutting(ingredient, wall);
    }

    private void removeNbt(ItemLike item) {
        this.shapeless(this.loc(item, "remove_nbt"), item, item);
    }
}
