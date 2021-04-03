package de.melanx.utilitix.item;

import io.github.noeppi_noeppi.libx.mod.ModX;
import io.github.noeppi_noeppi.libx.mod.registration.ItemBase;
import net.minecraft.item.ItemStack;

public class ItemBurnable extends ItemBase {

    private final int burnTime;

    public ItemBurnable(ModX mod, Properties properties, int burnTime) {
        super(mod, properties);
        this.burnTime = burnTime;
    }

    @Override
    public int getBurnTime(ItemStack itemStack) {
        return this.burnTime;
    }
}
