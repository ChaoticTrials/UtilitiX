package de.melanx.utilitix.registration;

import de.melanx.utilitix.enchantment.BellRange;
import net.minecraft.world.item.enchantment.Enchantment;
import org.moddingx.libx.annotation.registration.RegisterClass;

@RegisterClass(registry = "ENCHANTMENT_REGISTRY")
public class ModEnchantments {

    public static final Enchantment bellRange = new BellRange();
}
