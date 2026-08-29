package de.melanx.utilitix.recipe;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.item.ItemUtil;

import javax.annotation.Nonnull;

public record ItemHandlerRecipeInput(ResourceHandler<ItemResource> handler) implements RecipeInput {

    @Nonnull
    @Override
    public ItemStack getItem(int index) {
        return ItemUtil.getStack(this.handler, index);
    }

    @Override
    public int size() {
        return this.handler.size();
    }
}
