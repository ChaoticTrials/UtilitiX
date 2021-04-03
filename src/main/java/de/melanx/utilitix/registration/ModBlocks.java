package de.melanx.utilitix.registration;

import de.melanx.utilitix.block.WeakRedstoneTorch;
import de.melanx.utilitix.block.WeakRedstoneWallTorch;
import io.github.noeppi_noeppi.libx.annotation.RegisterClass;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;

@RegisterClass(priority = 1)
public class ModBlocks {

    public static final Block weakRedstoneTorch = new WeakRedstoneTorch(AbstractBlock.Properties.from(Blocks.REDSTONE_TORCH));
    public static final Block weakRedstoneWallTorch = new WeakRedstoneWallTorch(AbstractBlock.Properties.from(Blocks.REDSTONE_WALL_TORCH));
}
