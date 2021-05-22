package de.melanx.utilitix.block.container;

import de.melanx.utilitix.block.tile.TileAdvancedBrewery;
import io.github.noeppi_noeppi.libx.inventory.container.ContainerBase;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class ContainerAdvancedBrewery extends ContainerBase<TileAdvancedBrewery> {

    protected ContainerAdvancedBrewery(@Nullable ContainerType type, int windowId, World world, BlockPos pos, PlayerInventory playerInventory, PlayerEntity player) {
        super(type, windowId, world, pos, playerInventory, player, firstOutputSlot, firstInventorySlot);
    }
}
