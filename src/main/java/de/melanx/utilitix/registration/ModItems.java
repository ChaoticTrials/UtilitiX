package de.melanx.utilitix.registration;

import de.melanx.utilitix.UtilitiX;
import de.melanx.utilitix.item.*;
import de.melanx.utilitix.item.bells.HandBell;
import de.melanx.utilitix.item.bells.MobBell;
import de.melanx.utilitix.wireless.ItemLinkedCrystal;
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
    public static final Item armedStand = new ArmedStand(UtilitiX.getInstance(), new Item.Properties().maxStackSize(16));
    public static final Item glueBall = new ItemGlueBall(UtilitiX.getInstance(), new Item.Properties());
    public static final Item linkedCrystal = new ItemLinkedCrystal(UtilitiX.getInstance(), new Item.Properties().maxStackSize(8));
}
