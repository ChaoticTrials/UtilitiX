package de.melanx.utilitix.registration;

import de.melanx.utilitix.UtilitiX;
import de.melanx.utilitix.content.*;
import de.melanx.utilitix.content.backpack.BackpackItem;
import de.melanx.utilitix.content.bell.HandBellItem;
import de.melanx.utilitix.content.bell.MobBellItem;
import de.melanx.utilitix.content.brewery.FailedPotionItem;
import de.melanx.utilitix.content.gildingarmor.GildingCrystalItem;
import de.melanx.utilitix.content.glue.GlueBallItem;
import de.melanx.utilitix.content.quiver.QuiverItem;
import de.melanx.utilitix.content.redstone.wireless.LinkedCrystalItem;
import de.melanx.utilitix.content.shulkerboat.ShulkerBoatItem;
import de.melanx.utilitix.content.track.MinecartTinkererItem;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ShearsItem;
import net.minecraft.world.item.component.ItemContainerContents;
import org.moddingx.libx.annotation.registration.RegisterClass;

@RegisterClass(registry = "ITEM")
public class ModItems {

    public static final BackpackItem backpack = new BackpackItem(UtilitiX.getInstance(), new Item.Properties().stacksTo(1));
    public static final Item tinyCoal = new TinyCoalItem(UtilitiX.getInstance(), new Item.Properties(), 200);
    public static final Item tinyCharcoal = new TinyCoalItem(UtilitiX.getInstance(), new Item.Properties(), 200);
    public static final Item handBell = new HandBellItem(UtilitiX.getInstance(), new Item.Properties().stacksTo(1));
    public static final Item mobBell = new MobBellItem(UtilitiX.getInstance(), new Item.Properties().stacksTo(1));
    public static final Item quiver = new QuiverItem(UtilitiX.getInstance(), new Item.Properties().stacksTo(1).enchantable(1).component(DataComponents.CONTAINER, ItemContainerContents.EMPTY));
    public static final Item failedPotion = new FailedPotionItem(UtilitiX.getInstance(), new Item.Properties().stacksTo(1));
    public static final Item armedStand = new ArmedStandItem(new Item.Properties().stacksTo(16));
    public static final Item glueBall = new GlueBallItem(UtilitiX.getInstance(), new Item.Properties());
    public static final Item linkedCrystal = new LinkedCrystalItem(UtilitiX.getInstance(), new Item.Properties().stacksTo(16));
    public static final Item gildingCrystal = new GildingCrystalItem(UtilitiX.getInstance(), new Item.Properties().stacksTo(16));
    public static final Item minecartTinkerer = new MinecartTinkererItem(UtilitiX.getInstance(), new Item.Properties().stacksTo(1));
    public static final Item diamondShears = new DiamondShearsItem(new Item.Properties().stacksTo(1).durability(1486).component(DataComponents.TOOL, ShearsItem.createToolProperties()));
    public static final Item mobYoinker = new MobYoinkerItem(new Item.Properties().stacksTo(1));
    public static final Item oakShulkerBoat = new ShulkerBoatItem(UtilitiX.getInstance(), ModEntities.oakShulkerBoat, new Item.Properties().stacksTo(1));
    public static final Item spruceShulkerBoat = new ShulkerBoatItem(UtilitiX.getInstance(), ModEntities.spruceShulkerBoat, new Item.Properties().stacksTo(1));
    public static final Item birchShulkerBoat = new ShulkerBoatItem(UtilitiX.getInstance(), ModEntities.birchShulkerBoat, new Item.Properties().stacksTo(1));
    public static final Item jungleShulkerBoat = new ShulkerBoatItem(UtilitiX.getInstance(), ModEntities.jungleShulkerBoat, new Item.Properties().stacksTo(1));
    public static final Item acaciaShulkerBoat = new ShulkerBoatItem(UtilitiX.getInstance(), ModEntities.acaciaShulkerBoat, new Item.Properties().stacksTo(1));
    public static final Item cherryShulkerBoat = new ShulkerBoatItem(UtilitiX.getInstance(), ModEntities.cherryShulkerBoat, new Item.Properties().stacksTo(1));
    public static final Item darkOakShulkerBoat = new ShulkerBoatItem(UtilitiX.getInstance(), ModEntities.darkOakShulkerBoat, new Item.Properties().stacksTo(1));
    public static final Item mangroveShulkerBoat = new ShulkerBoatItem(UtilitiX.getInstance(), ModEntities.mangroveShulkerBoat, new Item.Properties().stacksTo(1));
    public static final Item paleOakShulkerBoat = new ShulkerBoatItem(UtilitiX.getInstance(), ModEntities.paleOakShulkerBoat, new Item.Properties().stacksTo(1));
    public static final Item bambooShulkerRaft = new ShulkerBoatItem(UtilitiX.getInstance(), ModEntities.bambooShulkerRaft, new Item.Properties().stacksTo(1));
    public static final Item ancientCompass = new AncientCompassItem(UtilitiX.getInstance(), new Item.Properties().stacksTo(1));
}
