package de.melanx.utilitix.registration;

import de.melanx.utilitix.UtilitiX;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

public class ModItemTags {

    public static final TagKey<Item> POTIONS = ItemTags.create(new ResourceLocation(UtilitiX.getInstance().modid, "potions"));
    public static final TagKey<Item> BOTTLES = ItemTags.create(new ResourceLocation(UtilitiX.getInstance().modid, "bottles"));
    public static final TagKey<Item> RAIL_POWER_SOURCES = ItemTags.create(new ResourceLocation(UtilitiX.getInstance().modid, "rail_power_sources"));
    public static final TagKey<Item> CURIOS_BACK = ItemTags.create(new ResourceLocation("curios", "back"));
    public static final TagKey<Item> SHULKER_CHEST_BOATS = ItemTags.create(new ResourceLocation(UtilitiX.getInstance().modid, "shulker_chest_boats"));
}
