package de.melanx.utilitix.enchantment;

import de.melanx.utilitix.item.bells.BellBase;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentType;
import net.minecraft.inventory.EquipmentSlotType;

public class BellRange extends Enchantment {
    
    public BellRange() {
        super(Rarity.UNCOMMON, EnchantmentType.create("bell", item -> item instanceof BellBase), new EquipmentSlotType[]{EquipmentSlotType.MAINHAND, EquipmentSlotType.OFFHAND});
    }

    @Override
    public int getMaxLevel() {
        return 3;
    }

    @Override
    public boolean isTreasureEnchantment() {
        return true;
    }
}
