package de.melanx.utilitix.registration;

import de.melanx.utilitix.UtilitiX;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

public class ModBlockTags {

    public static final TagKey<Block> RAIL_POWER_SOURCES = BlockTags.create(Identifier.fromNamespaceAndPath(UtilitiX.getInstance().modid, "rail_power_sources"));
}
