package de.melanx.utilitix.registration;

import de.melanx.utilitix.UtilitiX;
import net.minecraft.item.Item;
import net.minecraft.tags.ITag;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.ResourceLocation;

public class ModItemTags {

    public static final ITag.INamedTag<Item> POTIONS = ItemTags.makeWrapperTag(new ResourceLocation(UtilitiX.getInstance().modid, "potions").toString());
    public static final ITag.INamedTag<Item> BOTTLES = ItemTags.makeWrapperTag(new ResourceLocation(UtilitiX.getInstance().modid, "bottles").toString());
}
