package de.melanx.utilitix.registration;

import de.melanx.utilitix.enchantment.BellRange;
import io.github.noeppi_noeppi.libx.annotation.registration.RegisterClass;
import net.minecraft.world.item.enchantment.Enchantment;

@RegisterClass
public class ModEnchantments {

    public static final Enchantment bellRange = new BellRange();
}
