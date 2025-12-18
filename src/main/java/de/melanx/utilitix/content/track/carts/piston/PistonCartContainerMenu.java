package de.melanx.utilitix.content.track.carts.piston;

import de.melanx.utilitix.content.track.carts.PistonCart;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.items.SlotItemHandler;
import org.moddingx.libx.annotation.registration.Reg.Name;
import org.moddingx.libx.annotation.registration.RegisterClass;
import org.moddingx.libx.menu.EntityMenu;
import org.moddingx.libx.menu.slot.OutputSlot;
import org.moddingx.libx.menu.type.AdvancedMenuType;

import javax.annotation.Nullable;

@RegisterClass(registry = "MENU")
public class PistonCartContainerMenu extends EntityMenu<PistonCart> {

    @Name("piston_cart_container")
    public static final AdvancedMenuType<PistonCartContainerMenu, Integer> TYPE =
            AdvancedMenuType.create(PistonCartContainerMenu::new,
                    StreamCodec.of(
                            FriendlyByteBuf::writeVarInt,
                            FriendlyByteBuf::readVarInt
                    )
            );

    protected PistonCartContainerMenu(@Nullable MenuType<?> type, int windowId, Level level, int entityId, Player player, Inventory inventory) {
        super(type, windowId, level, entityId, player, inventory, 13, 25);
        this.addSlotBox(this.entity.getRailInputInventory(), 0, 8, 18, 3, 18, 4, 18);
        this.addSlot(new SlotItemHandler(this.entity.getTorchInventory(), 0, 80, 72));
        this.addSlotBox(this.entity.getRailOutputInventory(), 0, 116, 18, 3, 18, 4, 18, OutputSlot::new);
        this.layoutPlayerInventorySlots(8, 104);
    }
}
