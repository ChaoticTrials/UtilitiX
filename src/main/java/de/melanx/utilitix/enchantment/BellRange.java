package de.melanx.utilitix.enchantment;

import de.melanx.utilitix.content.bell.BellBase;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

public class BellRange extends Enchantment {
    
    public BellRange() {
        super(Rarity.UNCOMMON, EnchantmentCategory.create("bell", item -> item instanceof BellBase), new EquipmentSlot[]{EquipmentSlot.MAINHAND, EquipmentSlot.OFFHAND});
    }

    @Override
    public int getMaxLevel() {
        return 3;
    }

    @Override
    public boolean isTreasureOnly() {
        return true;
    }
}
