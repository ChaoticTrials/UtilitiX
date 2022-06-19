package de.melanx.utilitix.item;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import org.moddingx.libx.base.ItemBase;
import org.moddingx.libx.mod.ModX;

import javax.annotation.Nullable;

public class ItemBurnable extends ItemBase {

    private final int burnTime;

    public ItemBurnable(ModX mod, Properties properties, int burnTime) {
        super(mod, properties);
        this.burnTime = burnTime;
    }

    @Override
    public int getBurnTime(ItemStack itemStack, @Nullable RecipeType<?> recipeType) {
        return this.burnTime;
    }
}
