package de.melanx.utilitix.registration;

import de.melanx.utilitix.UtilitiX;
import de.melanx.utilitix.item.ItemBurnable;
import de.melanx.utilitix.item.ItemFailedPotion;
import de.melanx.utilitix.item.Quiver;
import de.melanx.utilitix.item.bells.HandBell;
import de.melanx.utilitix.item.bells.MobBell;
import io.github.noeppi_noeppi.libx.annotation.RegisterClass;
import net.minecraft.item.Item;

@RegisterClass
public class ModItems {

    public static final Item tinyCoal = new ItemBurnable(UtilitiX.getInstance(), new Item.Properties(), 200);
    public static final Item tinyCharcoal = new ItemBurnable(UtilitiX.getInstance(), new Item.Properties(), 200);
    public static final Item handBell = new HandBell(UtilitiX.getInstance(), new Item.Properties().maxStackSize(1));
    public static final Item mobBell = new MobBell(UtilitiX.getInstance(), new Item.Properties().maxStackSize(1));
    public static final Item quiver = new Quiver(UtilitiX.getInstance(), new Item.Properties().maxStackSize(1));
    public static final Item failedPotion = new ItemFailedPotion(UtilitiX.getInstance(), new Item.Properties().maxStackSize(1));
}
