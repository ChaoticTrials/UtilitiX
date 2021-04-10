package de.melanx.utilitix.item;

import io.github.noeppi_noeppi.libx.inventory.BaseItemStackHandler;
import io.github.noeppi_noeppi.libx.mod.ModX;
import io.github.noeppi_noeppi.libx.mod.registration.ItemBase;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import net.minecraftforge.items.IItemHandlerModifiable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.concurrent.atomic.AtomicReference;

public class Quiver extends ItemBase {
    public Quiver(ModX mod, Properties properties) {
        super(mod, properties);
    }

    @Nonnull
    @Override
    public ActionResult<ItemStack> onItemRightClick(@Nonnull World world, @Nonnull PlayerEntity player, @Nonnull Hand hand) {
        ItemStack stack = player.getHeldItem(hand);

        if (!world.isRemote) {
            AtomicReference<BaseItemStackHandler> handler = new AtomicReference<>(null);
            handler.set(new BaseItemStackHandler(27,
                    slot -> {
                        stack.getOrCreateTag().put("Items", handler.get().serializeNBT());
                        player.setHeldItem(hand, stack);
                    },
                    (slot, stack1) -> ItemTags.ARROWS.contains(stack1.getItem())));
            if (stack.getOrCreateTag().contains("Items")) {
                handler.get().deserializeNBT(stack.getOrCreateTag().getCompound("Items"));
            }
            player.openContainer(new QuiverContainerProvider(handler.get(), () -> handler.get().onContentsChanged(0)));
            return ActionResult.func_233538_a_(stack, false);
        }

        return ActionResult.resultPass(stack);
    }

    @Override
    public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment) {
        return enchantment == Enchantments.INFINITY;
    }

    @Nullable
    public static IItemHandlerModifiable getInventory(ItemStack stack) {
        if (!stack.hasTag() || !stack.getOrCreateTag().contains("Items")) {
            return null;
        }

        AtomicReference<BaseItemStackHandler> handler = new AtomicReference<>(null);
        handler.set(new BaseItemStackHandler(27,
                slot -> {
                    stack.getOrCreateTag().put("Items", handler.get().serializeNBT());
                },
                (slot, stack1) -> ItemTags.ARROWS.contains(stack1.getItem())));
        handler.get().deserializeNBT(stack.getOrCreateTag().getCompound("Items"));

        return handler.get();
    }

    public static boolean isEmpty(ItemStack stack) {
        IItemHandlerModifiable inventory = getInventory(stack);
        if (inventory != null) {
            for (int i = 0; i < inventory.getSlots(); i++) {
                ItemStack itemStack = inventory.extractItem(i, 1, true);
                if (!itemStack.isEmpty()) {
                    return false;
                }
            }
        }

        return true;
    }
}
