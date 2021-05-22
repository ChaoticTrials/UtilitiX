package de.melanx.utilitix.recipe;

import io.github.noeppi_noeppi.libx.crafting.recipe.RecipeHelper;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.*;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

public class BreweryRecipe implements IRecipe<IInventory> {

    private final Ingredient input;
    
    
    @Override
    public boolean matches(@Nonnull IInventory inv, @Nonnull World world) {
        if (inv.getSizeInventory() != 5) {
            
        }
        return false;
    }

    @Override
    public ItemStack getCraftingResult(IInventory inv) {
        return null;
    }

    @Override
    public boolean canFit(int width, int height) {
        return false;
    }

    @Override
    public ItemStack getRecipeOutput() {
        return null;
    }

    @Override
    public ResourceLocation getId() {
        return null;
    }

    @Override
    public IRecipeSerializer<?> getSerializer() {
        return null;
    }

    @Override
    public IRecipeType<?> getType() {
        return null;
    }
}
