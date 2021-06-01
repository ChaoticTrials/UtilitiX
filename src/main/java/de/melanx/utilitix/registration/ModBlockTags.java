package de.melanx.utilitix.registration;

import de.melanx.utilitix.UtilitiX;
import net.minecraft.block.Block;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ITag;
import net.minecraft.util.ResourceLocation;

public class ModBlockTags {

    public static final ITag.INamedTag<Block> RAIL_POWER_SOURCES = BlockTags.makeWrapperTag(new ResourceLocation(UtilitiX.getInstance().modid, "rail_power_sources").toString());
}
