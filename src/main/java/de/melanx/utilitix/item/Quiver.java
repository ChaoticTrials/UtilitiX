package de.melanx.utilitix.item;

import de.melanx.utilitix.UtilitiX;
import io.github.noeppi_noeppi.libx.base.ItemBase;
import io.github.noeppi_noeppi.libx.inventory.BaseItemStackHandler;
import io.github.noeppi_noeppi.libx.menu.GenericMenu;
import io.github.noeppi_noeppi.libx.mod.ModX;
import io.github.noeppi_noeppi.libx.mod.registration.Registerable;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraftforge.items.IItemHandlerModifiable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

public class Quiver extends ItemBase implements Registerable {

    public static final ResourceLocation SLOT_VALIDATOR = new ResourceLocation(UtilitiX.getInstance().modid, "quiver_arrows");

    public Quiver(ModX mod, Properties properties) {
        super(mod, properties);
    }

    @Override
    public void registerCommon(ResourceLocation id, Consumer<Runnable> defer) {
        GenericMenu.registerSlotValidator(SLOT_VALIDATOR, (slot, stack) -> ItemTags.ARROWS.contains(stack.getItem()));
    }

    @Nonnull
    @Override
    public InteractionResultHolder<ItemStack> use(@Nonnull Level level, @Nonnull Player player, @Nonnull InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (!level.isClientSide) {
            AtomicReference<BaseItemStackHandler> handler = new AtomicReference<>(null);
            handler.set(BaseItemStackHandler.builder(9)
                    .contentsChanged(slot -> {
                        stack.getOrCreateTag().put("Items", handler.get().serializeNBT());
                        player.setItemInHand(hand, stack);
                    })
                    .validator(stack1 -> ItemTags.ARROWS.contains(stack1.getItem()), 0, 1, 2, 3, 4, 5, 6, 7, 8)
                    .build());
            if (stack.getOrCreateTag().contains("Items")) {
                handler.get().deserializeNBT(stack.getOrCreateTag().getCompound("Items"));
            }
            if (player instanceof ServerPlayer) {
                GenericMenu.open((ServerPlayer) player, handler.get(), new TranslatableComponent("screen." + UtilitiX.getInstance().modid + ".quiver"), SLOT_VALIDATOR);
            }
            return InteractionResultHolder.sidedSuccess(stack, false);
        }

        return InteractionResultHolder.pass(stack);
    }

    @Override
    public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment) {
        return enchantment == Enchantments.INFINITY_ARROWS;
    }

    @Nullable
    public static IItemHandlerModifiable getInventory(ItemStack stack) {
        if (!stack.hasTag() || !stack.getOrCreateTag().contains("Items")) {
            return null;
        }
        AtomicReference<BaseItemStackHandler> handler = new AtomicReference<>(null);
        handler.set(BaseItemStackHandler.builder(9)
                .contentsChanged(slot -> stack.getOrCreateTag().put("Items", handler.get().serializeNBT()))
                .validator(stack1 -> ItemTags.ARROWS.contains(stack1.getItem()))
                .build()
        );
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

    @Override
    public void fillItemCategory(@Nonnull CreativeModeTab group, @Nonnull NonNullList<ItemStack> items) {
        // TODO re-add after item was added
    }
}
