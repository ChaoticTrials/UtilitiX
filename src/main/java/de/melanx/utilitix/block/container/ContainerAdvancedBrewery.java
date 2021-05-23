package de.melanx.utilitix.block.container;

import de.melanx.utilitix.block.tile.TileAdvancedBrewery;
import io.github.noeppi_noeppi.libx.inventory.container.ContainerBase;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.items.SlotItemHandler;

import javax.annotation.Nullable;

public class ContainerAdvancedBrewery extends ContainerBase<TileAdvancedBrewery> {

    public ContainerAdvancedBrewery(@Nullable ContainerType type, int windowId, World world, BlockPos pos, PlayerInventory playerInventory, PlayerEntity player) {
        super(type, windowId, world, pos, playerInventory, player, 5, 5);
        
        this.addSlot(new SlotItemHandler(this.tile.getUnrestricted(), 3, 79, 58));
        this.addSlot(new SlotItemHandler(this.tile.getUnrestricted(), 1, 56, 51));
        this.addSlot(new SlotItemHandler(this.tile.getUnrestricted(), 2, 102, 51));
        this.addSlot(new SlotItemHandler(this.tile.getUnrestricted(), 0, 79, 17));
        this.addSlot(new SlotItemHandler(this.tile.getUnrestricted(), 4, 17, 17));
        
        this.layoutPlayerInventorySlots(8, 84);
    }
}
