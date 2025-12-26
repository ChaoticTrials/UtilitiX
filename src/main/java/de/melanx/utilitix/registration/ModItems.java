package de.melanx.utilitix.registration;

import de.melanx.utilitix.UtilitiX;
import de.melanx.utilitix.content.AncientCompassItem;
import de.melanx.utilitix.content.ArmedStandItem;
import de.melanx.utilitix.content.DiamondShearsItem;
import de.melanx.utilitix.content.TinyCoalItem;
import de.melanx.utilitix.content.bell.HandBellItem;
import de.melanx.utilitix.content.bell.MobBellItem;
import de.melanx.utilitix.content.brewery.FailedPotionItem;
import de.melanx.utilitix.content.gildingarmor.GildingCrystalItem;
import de.melanx.utilitix.content.glue.GlueBallItem;
import de.melanx.utilitix.content.redstone.wireless.LinkedCrystalItem;
import de.melanx.utilitix.content.shulkerboat.ShulkerBoatItem;
import de.melanx.utilitix.content.track.MinecartTinkererItem;
import de.melanx.utilitix.item.MobYoinkerItem;
import de.melanx.utilitix.item.Quiver;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ShearsItem;
import org.moddingx.libx.annotation.registration.RegisterClass;

@RegisterClass(registry = "ITEM")
public class ModItems {

    public static final Item tinyCoal = new TinyCoalItem(UtilitiX.getInstance(), new Item.Properties(), 200);
    public static final Item tinyCharcoal = new TinyCoalItem(UtilitiX.getInstance(), new Item.Properties(), 200);
    public static final Item handBell = new HandBellItem(UtilitiX.getInstance(), new Item.Properties().stacksTo(1));
    public static final Item mobBell = new MobBellItem(UtilitiX.getInstance(), new Item.Properties().stacksTo(1));
    public static final Item quiver = new Quiver(UtilitiX.getInstance(), new Item.Properties().stacksTo(1));
    public static final Item failedPotion = new FailedPotionItem(UtilitiX.getInstance(), new Item.Properties().stacksTo(1));
    public static final Item armedStand = new ArmedStandItem(new Item.Properties().stacksTo(16));
    public static final Item glueBall = new GlueBallItem(UtilitiX.getInstance(), new Item.Properties());
    public static final Item linkedCrystal = new LinkedCrystalItem(UtilitiX.getInstance(), new Item.Properties().stacksTo(8));
    public static final Item gildingCrystal = new GildingCrystalItem(UtilitiX.getInstance(), new Item.Properties().stacksTo(16));
    public static final Item minecartTinkerer = new MinecartTinkererItem(UtilitiX.getInstance(), new Item.Properties().stacksTo(1));
    public static final Item diamondShears = new DiamondShearsItem(new Item.Properties().stacksTo(1).durability(1486).component(DataComponents.TOOL, ShearsItem.createToolProperties()));
    public static final Item mobYoinker = new MobYoinkerItem(new Item.Properties().stacksTo(1));
    public static final Item oakShulkerBoat = new ShulkerBoatItem(UtilitiX.getInstance(), Boat.Type.OAK, new Item.Properties().stacksTo(1));
    public static final Item spruceShulkerBoat = new ShulkerBoatItem(UtilitiX.getInstance(), Boat.Type.SPRUCE, new Item.Properties().stacksTo(1));
    public static final Item birchShulkerBoat = new ShulkerBoatItem(UtilitiX.getInstance(), Boat.Type.BIRCH, new Item.Properties().stacksTo(1));
    public static final Item jungleShulkerBoat = new ShulkerBoatItem(UtilitiX.getInstance(), Boat.Type.JUNGLE, new Item.Properties().stacksTo(1));
    public static final Item acaciaShulkerBoat = new ShulkerBoatItem(UtilitiX.getInstance(), Boat.Type.ACACIA, new Item.Properties().stacksTo(1));
    public static final Item cherryShulkerBoat = new ShulkerBoatItem(UtilitiX.getInstance(), Boat.Type.CHERRY, new Item.Properties().stacksTo(1));
    public static final Item darkOakShulkerBoat = new ShulkerBoatItem(UtilitiX.getInstance(), Boat.Type.DARK_OAK, new Item.Properties().stacksTo(1));
    public static final Item mangroveShulkerBoat = new ShulkerBoatItem(UtilitiX.getInstance(), Boat.Type.MANGROVE, new Item.Properties().stacksTo(1));
    public static final Item bambooShulkerRaft = new ShulkerBoatItem(UtilitiX.getInstance(), Boat.Type.BAMBOO, new Item.Properties().stacksTo(1));
    public static final Item ancientCompass = new AncientCompassItem(UtilitiX.getInstance(), new Item.Properties().stacksTo(1));
}
