package de.melanx.utilitix.compat.curios;

import de.melanx.utilitix.content.backpack.BackpackItem;
import de.melanx.utilitix.registration.ModItems;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.type.capability.ICuriosItemHandler;

import java.util.Optional;

public class UtilCurios {

    public static final String MODID = "curios";

    public static boolean isLoaded() {
        return ModList.get().isLoaded(MODID);
    }

    public static void openBackpack(ServerPlayer player) {
        Optional<FilledCurioSlot> optional = UtilCurios.getBackpack(player);
        if (optional.isEmpty()) {
            return;
        }

        int curioSlot = optional.get().slot;
        IItemHandlerModifiable curioHandler = optional.get().handler;
        ItemStack backpack = curioHandler.getStackInSlot(curioSlot);

        BackpackItem.openMenu(backpack, player);
    }

    public static Optional<FilledCurioSlot> getBackpack(Player player) {
        if (!UtilCurios.isLoaded()) {
            return Optional.empty();
        }

        Optional<ICuriosItemHandler> curiosInventory = CuriosApi.getCuriosInventory(player);
        if (curiosInventory.isEmpty() || !curiosInventory.get().isEquipped(ModItems.backpack)) {
            return Optional.empty();
        }

        IItemHandlerModifiable itemHandlerModifiable = curiosInventory.get().getEquippedCurios();
        for (int i = 0; i < itemHandlerModifiable.getSlots(); i++) {
            ItemStack stack = itemHandlerModifiable.getStackInSlot(i);
            if (stack.getItem() instanceof BackpackItem) {
                return Optional.of(new FilledCurioSlot(itemHandlerModifiable, i));
            }
        }

        return Optional.empty();
    }

    public static boolean isBackpackEquipped(Player player, ItemStack stack) {
        return UtilCurios.getBackpack(player).map(slot -> slot.handler.getStackInSlot(slot.slot) == stack).orElse(false);
    }

    public record FilledCurioSlot(IItemHandlerModifiable handler, int slot) {}
}
