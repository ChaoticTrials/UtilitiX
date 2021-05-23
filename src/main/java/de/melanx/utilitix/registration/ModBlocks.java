package de.melanx.utilitix.registration;

import de.melanx.utilitix.UtilitiX;
import de.melanx.utilitix.block.BlockAdvancedBrewery;
import de.melanx.utilitix.block.WeakRedstoneTorch;
import de.melanx.utilitix.block.container.ContainerAdvancedBrewery;
import de.melanx.utilitix.block.tile.TileAdvancedBrewery;
import io.github.noeppi_noeppi.libx.annotation.RegisterClass;
import io.github.noeppi_noeppi.libx.mod.registration.BlockGUI;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Blocks;

@RegisterClass(priority = 1)
public class ModBlocks {

    public static final WeakRedstoneTorch weakRedstoneTorch = new WeakRedstoneTorch(UtilitiX.getInstance(), AbstractBlock.Properties.from(Blocks.REDSTONE_TORCH));
    public static final BlockGUI<TileAdvancedBrewery, ContainerAdvancedBrewery> advancedBrewery = new BlockAdvancedBrewery(UtilitiX.getInstance(), AbstractBlock.Properties.from(Blocks.BREWING_STAND));
}
