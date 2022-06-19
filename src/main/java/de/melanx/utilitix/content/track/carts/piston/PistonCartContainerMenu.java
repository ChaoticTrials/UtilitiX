package de.melanx.utilitix.content.track.carts.piston;

import de.melanx.utilitix.content.track.carts.PistonCart;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.Level;
import net.minecraftforge.items.SlotItemHandler;
import org.moddingx.libx.annotation.registration.Reg.Name;
import org.moddingx.libx.annotation.registration.RegisterClass;
import org.moddingx.libx.menu.EntityMenu;
import org.moddingx.libx.menu.slot.OutputSlot;

import javax.annotation.Nullable;

@RegisterClass(registry = "MENU_REGISTRY")
public class PistonCartContainerMenu extends EntityMenu<PistonCart> {

    @Name("piston_cart_container")
    public static final MenuType<PistonCartContainerMenu> TYPE = createMenuType(PistonCartContainerMenu::new);

    protected PistonCartContainerMenu(@Nullable MenuType<?> type, int windowId, Level level, int entityId, Inventory playerContainer, Player player) {
        super(type, windowId, level, entityId, playerContainer, player, 13, 25);
        this.addSlotBox(this.entity.getRailInputInventory(), 0, 8, 18, 3, 18, 4, 18);
        this.addSlot(new SlotItemHandler(this.entity.getTorchInventory(), 0, 80, 72));
        this.addSlotBox(this.entity.getRailOutputInventory(), 0, 116, 18, 3, 18, 4, 18, OutputSlot::new);
        this.layoutPlayerInventorySlots(8, 104);
    }
}
