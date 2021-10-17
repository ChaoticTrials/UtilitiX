package de.melanx.utilitix.content.brewery;

import io.github.noeppi_noeppi.libx.menu.BlockEntityMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.Level;
import net.minecraftforge.items.SlotItemHandler;

import javax.annotation.Nullable;

public class ContainerMenuAdvancedBrewery extends BlockEntityMenu<TileAdvancedBrewery> {

    public ContainerMenuAdvancedBrewery(@Nullable MenuType type, int windowId, Level level, BlockPos pos, Inventory playerContainer, Player player) {
        super(type, windowId, level, pos, playerContainer, player, 5, 5);

        this.addSlot(new SlotItemHandler(this.blockEntity.getUnrestricted(), 3, 79, 58));
        this.addSlot(new SlotItemHandler(this.blockEntity.getUnrestricted(), 1, 56, 51));
        this.addSlot(new SlotItemHandler(this.blockEntity.getUnrestricted(), 2, 102, 51));
        this.addSlot(new SlotItemHandler(this.blockEntity.getUnrestricted(), 0, 79, 17));
        this.addSlot(new SlotItemHandler(this.blockEntity.getUnrestricted(), 4, 17, 17));

        this.layoutPlayerInventorySlots(8, 84);
    }
}
