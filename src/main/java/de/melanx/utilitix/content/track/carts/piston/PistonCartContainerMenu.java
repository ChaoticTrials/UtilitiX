package de.melanx.utilitix.content.track.carts.piston;

import de.melanx.utilitix.content.track.carts.PistonCart;
import io.github.noeppi_noeppi.libx.annotation.registration.RegName;
import io.github.noeppi_noeppi.libx.annotation.registration.RegisterClass;
import io.github.noeppi_noeppi.libx.menu.EntityMenu;
import io.github.noeppi_noeppi.libx.menu.slot.OutputSlot;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.Level;
import net.minecraftforge.items.SlotItemHandler;

import javax.annotation.Nullable;

@RegisterClass
public class PistonCartContainerMenu extends EntityMenu<PistonCart> {

    @RegName("piston_cart_container")
    public static final MenuType<PistonCartContainerMenu> TYPE = createMenuType(PistonCartContainerMenu::new);

    protected PistonCartContainerMenu(@Nullable MenuType<?> type, int windowId, Level level, int entityId, Inventory playerContainer, Player player) {
        super(type, windowId, level, entityId, playerContainer, player, 13, 25);
        this.addSlotBox(this.entity.getRailInputInventory(), 0, 8, 18, 3, 18, 4, 18);
        this.addSlot(new SlotItemHandler(this.entity.getTorchInventory(), 0, 80, 72));
        this.addSlotBox(this.entity.getRailOutputInventory(), 0, 116, 18, 3, 18, 4, 18, OutputSlot::new);
        this.layoutPlayerInventorySlots(8, 104);
    }
}
