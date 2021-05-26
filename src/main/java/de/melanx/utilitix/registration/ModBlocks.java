package de.melanx.utilitix.registration;

import de.melanx.utilitix.UtilitiX;
import de.melanx.utilitix.block.ComparatorRedirector;
import de.melanx.utilitix.block.WeakRedstoneTorch;
import de.melanx.utilitix.content.brewery.BlockAdvancedBrewery;
import de.melanx.utilitix.content.brewery.ContainerAdvancedBrewery;
import de.melanx.utilitix.content.brewery.TileAdvancedBrewery;
import de.melanx.utilitix.content.wireless.BlockLinkedRepeater;
import io.github.noeppi_noeppi.libx.annotation.RegisterClass;
import io.github.noeppi_noeppi.libx.mod.registration.BlockBase;
import io.github.noeppi_noeppi.libx.mod.registration.BlockGUI;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Blocks;
import net.minecraft.util.Direction;

@RegisterClass(priority = 1)
public class ModBlocks {

    public static final BlockGUI<TileAdvancedBrewery, ContainerAdvancedBrewery> advancedBrewery = new BlockAdvancedBrewery(UtilitiX.getInstance(), AbstractBlock.Properties.from(Blocks.BREWING_STAND));
    public static final BlockBase comparatorRedirectorUp = new ComparatorRedirector(UtilitiX.getInstance(), Direction.UP, AbstractBlock.Properties.from(Blocks.OBSERVER));
    public static final BlockBase comparatorRedirectorDown = new ComparatorRedirector(UtilitiX.getInstance(), Direction.DOWN, AbstractBlock.Properties.from(Blocks.OBSERVER));
    public static final WeakRedstoneTorch weakRedstoneTorch = new WeakRedstoneTorch(UtilitiX.getInstance(), AbstractBlock.Properties.from(Blocks.REDSTONE_TORCH));
    public static final BlockBase linkedRepeater = new BlockLinkedRepeater(UtilitiX.getInstance(), AbstractBlock.Properties.from(Blocks.REPEATER));
}
