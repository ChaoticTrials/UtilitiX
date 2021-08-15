package de.melanx.utilitix.registration;

import de.melanx.utilitix.UtilitiX;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.Tag;
import net.minecraft.world.item.Item;

public class ModItemTags {

    public static final Tag.Named<Item> POTIONS = ItemTags.bind(new ResourceLocation(UtilitiX.getInstance().modid, "potions").toString());
    public static final Tag.Named<Item> BOTTLES = ItemTags.bind(new ResourceLocation(UtilitiX.getInstance().modid, "bottles").toString());
    public static final Tag.Named<Item> RAIL_POWER_SOURCES = ItemTags.bind(new ResourceLocation(UtilitiX.getInstance().modid, "rail_power_sources").toString());
}
