package de.melanx.utilitix.content.brewery;

import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.Level;
import org.moddingx.libx.inventory.BaseItemStackHandler;
import org.moddingx.libx.menu.BlockEntityMenu;
import org.moddingx.libx.menu.slot.BaseSlot;
import org.moddingx.libx.menu.type.AdvancedMenuType;

import javax.annotation.Nullable;

public class AdvancedBreweryMenu extends BlockEntityMenu<AdvancedBreweryBlockEntity> {

    public static final AdvancedMenuType<AdvancedBreweryMenu, BlockPos> TYPE = AdvancedMenuType.create(AdvancedBreweryMenu::new,
            StreamCodec.of(
                    (RegistryFriendlyByteBuf buf, BlockPos pos) -> buf.writeBlockPos(pos),
                    buffer -> RegistryFriendlyByteBuf.readBlockPos(buffer)
            ));

    public AdvancedBreweryMenu(@Nullable MenuType<? extends BlockEntityMenu<?>> type, int windowId, Level level, BlockPos pos, Player player, Inventory playerContainer) {
        super(type, windowId, level, pos, player, playerContainer, 5, 5);

        BaseItemStackHandler inventory = this.blockEntity.getInventory();
        this.addSlot(new BaseSlot(inventory, AdvancedBreweryBlockEntity.OUTPUT_SLOT, 79, 58));
        this.addSlot(new BaseSlot(inventory, AdvancedBreweryBlockEntity.POTION_SLOT_RIGHT, 56, 51));
        this.addSlot(new BaseSlot(inventory, AdvancedBreweryBlockEntity.POTION_SLOT_LEFT, 102, 51));
        this.addSlot(new BaseSlot(inventory, AdvancedBreweryBlockEntity.INGREDIENT_SLOT, 79, 17));
        this.addSlot(new BaseSlot(inventory, AdvancedBreweryBlockEntity.FUEL_SLOT, 17, 17));

        this.layoutPlayerInventorySlots(8, 84);
    }
}
