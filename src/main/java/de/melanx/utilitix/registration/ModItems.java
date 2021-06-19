package de.melanx.utilitix.registration;

import de.melanx.utilitix.UtilitiX;
import de.melanx.utilitix.content.ArmedStand;
import de.melanx.utilitix.content.bell.ItemHandBell;
import de.melanx.utilitix.content.bell.ItemMobBell;
import de.melanx.utilitix.content.brewery.ItemFailedPotion;
import de.melanx.utilitix.content.slime.ItemGlueBall;
import de.melanx.utilitix.content.track.ItemMinecartTinkerer;
import de.melanx.utilitix.content.wireless.ItemLinkedCrystal;
import de.melanx.utilitix.item.ItemBurnable;
import de.melanx.utilitix.item.Quiver;
import io.github.noeppi_noeppi.libx.annotation.RegisterClass;
import io.github.noeppi_noeppi.libx.mod.registration.ItemBase;
import net.minecraft.item.BucketItem;
import net.minecraft.item.Item;

@RegisterClass
public class ModItems {

    public static final Item tinyCoal = new ItemBurnable(UtilitiX.getInstance(), new Item.Properties(), 200);
    public static final Item tinyCharcoal = new ItemBurnable(UtilitiX.getInstance(), new Item.Properties(), 200);
    public static final Item handBell = new ItemHandBell(UtilitiX.getInstance(), new Item.Properties().maxStackSize(1));
    public static final Item mobBell = new ItemMobBell(UtilitiX.getInstance(), new Item.Properties().maxStackSize(1));
    public static final Item quiver = new Quiver(UtilitiX.getInstance(), new Item.Properties().maxStackSize(1));
    public static final Item failedPotion = new ItemFailedPotion(UtilitiX.getInstance(), new Item.Properties().maxStackSize(1));
    public static final Item armedStand = new ArmedStand(UtilitiX.getInstance(), new Item.Properties().maxStackSize(16));
    public static final Item glueBall = new ItemGlueBall(UtilitiX.getInstance(), new Item.Properties());
    public static final Item linkedCrystal = new ItemLinkedCrystal(UtilitiX.getInstance(), new Item.Properties().maxStackSize(8));
    public static final Item gildingCrystal = new ItemBase(UtilitiX.getInstance(), new Item.Properties().maxStackSize(16));
    public static final Item minecartTinkerer = new ItemMinecartTinkerer(UtilitiX.getInstance(), new Item.Properties().maxStackSize(1));
    public static final Item liquidExperienceBucket = new BucketItem(() -> ModFluids.liquidExperience, new Item.Properties().maxStackSize(1).group(UtilitiX.getInstance().tab));
}
