package de.melanx.utilitix.registration;

import de.melanx.utilitix.UtilitiX;
import de.melanx.utilitix.content.AncientCompass;
import de.melanx.utilitix.content.ArmedStand;
import de.melanx.utilitix.content.bell.ItemHandBell;
import de.melanx.utilitix.content.bell.ItemMobBell;
import de.melanx.utilitix.content.brewery.ItemFailedPotion;
import de.melanx.utilitix.content.shulkerboat.ShulkerBoatItem;
import de.melanx.utilitix.content.slime.ItemGlueBall;
import de.melanx.utilitix.content.track.ItemMinecartTinkerer;
import de.melanx.utilitix.content.wireless.ItemLinkedCrystal;
import de.melanx.utilitix.item.ItemBurnable;
import de.melanx.utilitix.item.ItemMobYoinker;
import de.melanx.utilitix.item.Quiver;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ShearsItem;
import org.moddingx.libx.annotation.registration.RegisterClass;
import org.moddingx.libx.base.ItemBase;

@RegisterClass(registry = "ITEM")
public class ModItems {

    public static final Item tinyCoal = new ItemBurnable(UtilitiX.getInstance(), new Item.Properties(), 200);
    public static final Item tinyCharcoal = new ItemBurnable(UtilitiX.getInstance(), new Item.Properties(), 200);
    public static final Item handBell = new ItemHandBell(UtilitiX.getInstance(), new Item.Properties().stacksTo(1));
    public static final Item mobBell = new ItemMobBell(UtilitiX.getInstance(), new Item.Properties().stacksTo(1));
    public static final Item quiver = new Quiver(UtilitiX.getInstance(), new Item.Properties().stacksTo(1));
    public static final Item failedPotion = new ItemFailedPotion(UtilitiX.getInstance(), new Item.Properties().stacksTo(1));
    public static final Item armedStand = new ArmedStand(new Item.Properties().stacksTo(16));
    public static final Item glueBall = new ItemGlueBall(UtilitiX.getInstance(), new Item.Properties());
    public static final Item linkedCrystal = new ItemLinkedCrystal(UtilitiX.getInstance(), new Item.Properties().stacksTo(8));
    public static final Item gildingCrystal = new ItemBase(UtilitiX.getInstance(), new Item.Properties().stacksTo(16));
    public static final Item minecartTinkerer = new ItemMinecartTinkerer(UtilitiX.getInstance(), new Item.Properties().stacksTo(1));
    public static final Item diamondShears = new ShearsItem(new Item.Properties().stacksTo(1).durability(1486));
    public static final Item mobYoinker = new ItemMobYoinker(new Item.Properties().stacksTo(1));
    public static final Item oakShulkerBoat = new ShulkerBoatItem(UtilitiX.getInstance(), Boat.Type.OAK, new Item.Properties().stacksTo(1));
    public static final Item spruceShulkerBoat = new ShulkerBoatItem(UtilitiX.getInstance(), Boat.Type.SPRUCE, new Item.Properties().stacksTo(1));
    public static final Item birchShulkerBoat = new ShulkerBoatItem(UtilitiX.getInstance(), Boat.Type.BIRCH, new Item.Properties().stacksTo(1));
    public static final Item jungleShulkerBoat = new ShulkerBoatItem(UtilitiX.getInstance(), Boat.Type.JUNGLE, new Item.Properties().stacksTo(1));
    public static final Item acaciaShulkerBoat = new ShulkerBoatItem(UtilitiX.getInstance(), Boat.Type.ACACIA, new Item.Properties().stacksTo(1));
    public static final Item cherryShulkerBoat = new ShulkerBoatItem(UtilitiX.getInstance(), Boat.Type.CHERRY, new Item.Properties().stacksTo(1));
    public static final Item darkOakShulkerBoat = new ShulkerBoatItem(UtilitiX.getInstance(), Boat.Type.DARK_OAK, new Item.Properties().stacksTo(1));
    public static final Item mangroveShulkerBoat = new ShulkerBoatItem(UtilitiX.getInstance(), Boat.Type.MANGROVE, new Item.Properties().stacksTo(1));
    public static final Item bambooShulkerRaft = new ShulkerBoatItem(UtilitiX.getInstance(), Boat.Type.BAMBOO, new Item.Properties().stacksTo(1));
    public static final Item ancientCompass = new AncientCompass(UtilitiX.getInstance(), new Item.Properties().stacksTo(1));
    public static final Item organicFertilizer = new ItemBase(UtilitiX.getInstance(), new Item.Properties());
}
