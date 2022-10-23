package de.melanx.utilitix.content.gildingarmor;

import de.melanx.utilitix.UtilitiX;
import de.melanx.utilitix.registration.ModItems;
import de.melanx.utilitix.registration.ModRecipes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterials;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.UpgradeRecipe;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.commons.compress.utils.Lists;

import javax.annotation.Nonnull;
import java.util.List;

public class GildingArmorRecipe extends UpgradeRecipe {

    public GildingArmorRecipe(ResourceLocation id) {
        super(id, Ingredient.EMPTY, Ingredient.EMPTY, ItemStack.EMPTY);
    }

    @Override
    public boolean matches(@Nonnull Container inv, @Nonnull Level level) {
        ItemStack input = inv.getItem(0);
        ItemStack addition = inv.getItem(1);

        if (input.getItem() instanceof ArmorItem && !isGilded(input) && canGild((ArmorItem) input.getItem())) {
            return addition.getItem() == ModItems.gildingCrystal;
        }

        return false;
    }

    @Nonnull
    @Override
    public ItemStack assemble(@Nonnull Container inv) {
        ItemStack stack = inv.getItem(0).copy();
        stack.getOrCreateTag().putBoolean("Gilded_UtilitiX", true);

        return stack;
    }

    @Nonnull
    @Override
    public ItemStack getResultItem() {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean isAdditionIngredient(@Nonnull ItemStack addition) {
        return addition.getItem() == ModItems.gildingCrystal;
    }

    @Nonnull
    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipes.GILDING_SERIALIZER;
    }

    public static boolean isGilded(ItemStack stack) {
        return stack.hasTag() && stack.getOrCreateTag().getBoolean("Gilded_UtilitiX");
    }

    private static boolean canGild(ArmorItem item) {
        try {
            return !item.makesPiglinsNeutral(new ItemStack(item), null) && item.getMaterial() != ArmorMaterials.GOLD;
        } catch (NullPointerException e) {
            return false;
        }
    }

    public static List<UpgradeRecipe> getRecipes() {
        List<UpgradeRecipe> recipes = Lists.newArrayList();

        Ingredient gildingItem = Ingredient.of(ModItems.gildingCrystal);

        for (Item item : ForgeRegistries.ITEMS.getValues()) {
            if (item instanceof ArmorItem && canGild((ArmorItem) item)) {
                ResourceLocation id = new ResourceLocation(UtilitiX.getInstance().modid, "gilding." + item.getDescriptionId());

                ItemStack output = new ItemStack(item);
                output.getOrCreateTag().putBoolean("Gilded_UtilitiX", true);

                UpgradeRecipe recipe = new UpgradeRecipe(id, Ingredient.of(item), gildingItem, output);

                recipes.add(recipe);
            }
        }

        return recipes;
    }
}
