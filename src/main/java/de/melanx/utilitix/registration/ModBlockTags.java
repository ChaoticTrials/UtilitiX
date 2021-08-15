package de.melanx.utilitix.registration;

import de.melanx.utilitix.UtilitiX;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.Tag;
import net.minecraft.world.level.block.Block;

public class ModBlockTags {

    public static final Tag.Named<Block> RAIL_POWER_SOURCES = BlockTags.bind(new ResourceLocation(UtilitiX.getInstance().modid, "rail_power_sources").toString());
}
