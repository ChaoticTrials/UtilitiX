package de.melanx.utilitix.content.brewery;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.items.SlotItemHandler;
import org.moddingx.libx.menu.BlockEntityMenu;
import org.moddingx.libx.menu.type.AdvancedMenuType;

import javax.annotation.Nullable;

public class ContainerMenuAdvancedBrewery extends BlockEntityMenu<TileAdvancedBrewery> {

    public static final AdvancedMenuType<ContainerMenuAdvancedBrewery, BlockPos> TYPE = AdvancedMenuType.create(ContainerMenuAdvancedBrewery::new,
            StreamCodec.of((buffer, value) -> FriendlyByteBuf.writeBlockPos(buffer, value), buffer -> FriendlyByteBuf.readBlockPos(buffer)));

    public ContainerMenuAdvancedBrewery(@Nullable MenuType<? extends BlockEntityMenu<?>> type, int windowId, Level level, BlockPos pos, Player player, Inventory playerContainer) {
        super(type, windowId, level, pos, player, playerContainer, 5, 5);

        this.addSlot(new SlotItemHandler(this.blockEntity.getInventory(), 3, 79, 58));
        this.addSlot(new SlotItemHandler(this.blockEntity.getInventory(), 1, 56, 51));
        this.addSlot(new SlotItemHandler(this.blockEntity.getInventory(), 2, 102, 51));
        this.addSlot(new SlotItemHandler(this.blockEntity.getInventory(), 0, 79, 17));
        this.addSlot(new SlotItemHandler(this.blockEntity.getInventory(), 4, 17, 17));

        this.layoutPlayerInventorySlots(8, 84);
    }
}
