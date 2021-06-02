package de.melanx.utilitix.content.gildingarmor;

import de.melanx.utilitix.UtilitiX;
import de.melanx.utilitix.registration.ModItems;
import de.melanx.utilitix.registration.ModRecipes;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.SmithingRecipe;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nonnull;
import java.util.HashSet;
import java.util.Set;

public class GildingArmorRecipe extends SmithingRecipe {

    public GildingArmorRecipe(ResourceLocation id) {
        super(id, Ingredient.EMPTY, Ingredient.EMPTY, ItemStack.EMPTY);
    }

    @Override
    public boolean matches(@Nonnull IInventory inv, @Nonnull World world) {
        ItemStack input = inv.getStackInSlot(0);
        ItemStack addition = inv.getStackInSlot(1);

        if (input.getItem() instanceof ArmorItem && !isGilded(input)) {
            return addition.getItem() == ModItems.gildingCrystal;
        }

        return false;
    }

    @Nonnull
    @Override
    public ItemStack getCraftingResult(@Nonnull IInventory inv) {
        ItemStack stack = inv.getStackInSlot(0).copy();
        stack.getOrCreateTag().putBoolean("Gilded_UtilitiX", true);

        return stack;
    }

    @Nonnull
    @Override
    public ItemStack getRecipeOutput() {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean isValidAdditionItem(@Nonnull ItemStack addition) {
        return addition.getItem() == ModItems.gildingCrystal;
    }

    @Nonnull
    @Override
    public IRecipeSerializer<?> getSerializer() {
        return ModRecipes.GILDING_SERIALIZER;
    }

    public static boolean isGilded(ItemStack stack) {
        return stack.hasTag() && stack.getOrCreateTag().getBoolean("Gilded_UtilitiX");
    }

    public static Set<SmithingRecipe> getRecipes() {
        Set<SmithingRecipe> recipes = new HashSet<>();

        Ingredient gildingItem = Ingredient.fromItems(ModItems.gildingCrystal);

        for (Item item : ForgeRegistries.ITEMS.getValues()) {
            if (item instanceof ArmorItem && ((ArmorItem) item).getArmorMaterial() != ArmorMaterial.GOLD) {
                ResourceLocation id = new ResourceLocation(UtilitiX.getInstance().modid, "gilding." + item.getTranslationKey());

                ItemStack output = new ItemStack(item);
                output.getOrCreateTag().putBoolean("Gilded_UtilitiX", true);

                SmithingRecipe recipe = new SmithingRecipe(id, Ingredient.fromItems(item), gildingItem, output);

                recipes.add(recipe);
            }
        }

        return recipes;
    }
}
