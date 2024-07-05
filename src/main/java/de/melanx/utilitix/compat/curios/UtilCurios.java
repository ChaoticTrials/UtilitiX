package de.melanx.utilitix.compat.curios;

import de.melanx.utilitix.content.backpack.BackpackMenu;
import de.melanx.utilitix.content.backpack.ItemBackpack;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.items.IItemHandlerModifiable;
import org.moddingx.libx.inventory.BaseItemStackHandler;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.type.capability.ICuriosItemHandler;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

public class UtilCurios {

    public static final String MODID = "curios";

    public static boolean isLoaded() {
        return ModList.get().isLoaded(MODID);
    }

    public static void openBackpack(ServerPlayer player) {
        Optional<FilledCurioSlot> optional = UtilCurios.getBackpack(player);
        if (optional.isPresent()) {
            int curioSlot = optional.get().slot;
            IItemHandlerModifiable curioHandler = optional.get().handler;
            ItemStack backpack = curioHandler.getStackInSlot(curioSlot);
            AtomicReference<BaseItemStackHandler> handler = new AtomicReference<>(null);
            handler.set(BaseItemStackHandler.builder(ItemBackpack.slotSize(backpack))
                    .contentsChanged(slot -> {
                        backpack.getOrCreateTag().put("Items", handler.get().serializeNBT());
                        curioHandler.setStackInSlot(curioSlot, backpack);
                    })
                    .validator(ItemBackpack.SLOT_VALIDATOR)
                    .build());

            if (!ItemBackpack.isEmpty(backpack)) {
                handler.get().deserializeNBT(backpack.getOrCreateTag().getCompound("Items"));
            }

            BackpackMenu.open(player, handler.get(), backpack);
        }
    }

    public static Optional<FilledCurioSlot> getBackpack(Player player) {
        if (!UtilCurios.isLoaded()) {
            return Optional.empty();
        }

        LazyOptional<ICuriosItemHandler> curiosInventory = CuriosApi.getCuriosInventory(player);
        if (!curiosInventory.isPresent() || curiosInventory.resolve().isEmpty()) {
            return Optional.empty();
        }

        IItemHandlerModifiable itemHandlerModifiable = curiosInventory.resolve().get().getEquippedCurios();
        for (int i = 0; i < itemHandlerModifiable.getSlots(); i++) {
            ItemStack stack = itemHandlerModifiable.getStackInSlot(i);
            if (stack.getItem() instanceof ItemBackpack) {
                return Optional.of(new FilledCurioSlot(itemHandlerModifiable, i));
            }
        }

        return Optional.empty();
    }

    public record FilledCurioSlot(IItemHandlerModifiable handler, int slot) {}
}
