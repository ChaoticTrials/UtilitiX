package de.melanx.utilitix.content.backpack;

import com.google.common.collect.ImmutableList;
import de.melanx.utilitix.UtilitiX;
import de.melanx.utilitix.compat.curios.UtilCurios;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.SlotItemHandler;
import net.minecraftforge.network.NetworkHooks;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;
import org.moddingx.libx.impl.menu.GenericContainerSlotValidationWrapper;
import org.moddingx.libx.inventory.BaseItemStackHandler;
import org.moddingx.libx.menu.MenuBase;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.function.BiPredicate;

public class BackpackMenu extends MenuBase {

    private static final BiPredicate<Integer, ItemStack> BACKPACK_SLOT_VALIDATOR = (slot, stack) -> !(stack.getItem() instanceof ItemBackpack);
    private static final int MAX_ROWS = 8;
    private final ItemStack backpack;
    public final int width;
    public final int height;
    public final int invX;
    public final int invY;
    public final List<Pair<Integer, Integer>> slotList;

    public static final MenuType<BackpackMenu> TYPE = IForgeMenuType.create((id, playerInv, buffer) -> {
        int size = buffer.readVarInt();
        IItemHandlerModifiable handler = new GenericContainerSlotValidationWrapper(new ItemStackHandler(size), BACKPACK_SLOT_VALIDATOR, null);

        return new BackpackMenu(id, handler, playerInv, buffer.readItem());
    });

    protected BackpackMenu(int id, IItemHandlerModifiable handler, Inventory inventory, ItemStack backpack) {
        super(TYPE, id, inventory);
        Triple<Pair<Integer, Integer>, Pair<Integer, Integer>, List<Pair<Integer, Integer>>> layout = layoutSlots(handler.getSlots());
        this.backpack = backpack;
        this.width = layout.getLeft().getLeft();
        this.height = layout.getLeft().getRight();
        this.invX = layout.getMiddle().getLeft();
        this.invY = layout.getMiddle().getRight();
        this.slotList = layout.getRight();
        for (int i = 0; i < this.slotList.size(); i++) {
            this.addSlot(new SlotItemHandler(handler, i, this.slotList.get(i).getLeft(), this.slotList.get(i).getRight()));
        }

        this.layoutPlayerInventorySlots(layout.getMiddle().getLeft(), layout.getMiddle().getRight());
    }

    @Override
    public boolean stillValid(@Nonnull Player player) {
        return player.getItemInHand(player.getUsedItemHand()).getItem() instanceof ItemBackpack || UtilCurios.getBackpack(player).isPresent();
    }

    public static void open(ServerPlayer player, BaseItemStackHandler inventory, ItemStack backpack) {
        MenuProvider provider = new MenuProvider() {

            @Nonnull
            @Override
            public Component getDisplayName() {
                return Component.translatable("screen." + UtilitiX.getInstance().modid + ".backpack");
            }

            @Override
            public AbstractContainerMenu createMenu(int containerId, @Nonnull Inventory inv, @Nonnull Player player) {
                return new BackpackMenu(containerId, new GenericContainerSlotValidationWrapper(inventory, BACKPACK_SLOT_VALIDATOR, null), inv, backpack);
            }
        };

        NetworkHooks.openScreen(player, provider, buffer -> {
            buffer.writeVarInt(inventory.getSlots());
            buffer.writeItemStack(backpack, false);
        });
    }

    @Nonnull
    @Override
    public ItemStack quickMoveStack(@Nonnull Player player, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot.hasItem()) {
            ItemStack stack = slot.getItem();
            itemstack = stack.copy();

            final int inventorySize = this.slotList.size();
            final int playerInventoryEnd = inventorySize + 27;
            final int playerHotBarEnd = playerInventoryEnd + 9;

            if (index >= inventorySize) {
                if (!this.moveItemStackTo(stack, 0, inventorySize, false)) {
                    return ItemStack.EMPTY;
                } else if (index < playerInventoryEnd) {
                    if (!this.moveItemStackTo(stack, playerInventoryEnd, playerHotBarEnd, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (index < playerHotBarEnd && !this.moveItemStackTo(stack, inventorySize, playerInventoryEnd, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.moveItemStackTo(stack, inventorySize, playerHotBarEnd, false)) {
                return ItemStack.EMPTY;
            }
            if (stack.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
            if (stack.getCount() == itemstack.getCount()) {
                return ItemStack.EMPTY;
            }
            slot.onTake(player, stack);
        }
        return itemstack;
    }

    @Override
    public void clicked(int slotId, int button, @Nonnull ClickType clickType, @Nonnull Player player) {
        if (this.validSlotId(slotId) && this.isOpenedBackpack(this.slots.get(slotId).getItem())) {
            return;
        }

        super.clicked(slotId, button, clickType, player);
    }

    private boolean isOpenedBackpack(ItemStack stack) {
        return stack.getItem() instanceof ItemBackpack && stack == this.backpack;
    }

    private boolean validSlotId(int slotId) {
        return slotId != AbstractContainerMenu.SLOT_CLICKED_OUTSIDE && slotId != -1;
    }

    private static Triple<Pair<Integer, Integer>, Pair<Integer, Integer>, List<Pair<Integer, Integer>>> layoutSlots(int size) {
        // Handle small sizes directly
        if (size < 9) {
            return BackpackMenu.layoutRectangle(size, 1, size);
        }

        // Define an array of possible dimensions to check
        int[] preferredWidths = {9, 11, 12, 8, 13, 14};

        for (int width : preferredWidths) {
            if (size % width == 0 && size <= width * MAX_ROWS) {
                return BackpackMenu.layoutRectangle(width, size / width, size);
            }
        }

        // Default handling when no special case matches
        for (int width : preferredWidths) {
            if (size <= width * MAX_ROWS) {
                return BackpackMenu.layoutRectangle(width, (size + width - 1) / width, size); // Equivalent to ceiling division
            }
        }

        // Fallback to the maximum width
        return BackpackMenu.layoutRectangle(14, (size + 13) / 14, size);
    }

    private static Triple<Pair<Integer, Integer>, Pair<Integer, Integer>, List<Pair<Integer, Integer>>> layoutRectangle(int width, int height, int maxSize) {
        int invX = Math.max((width - 9) * 9, 0);
        int paddingX = width < 9 ? (9 - width) * 9 : 0;

        ImmutableList.Builder<Pair<Integer, Integer>> builder = ImmutableList.builder();
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if ((y * width + x) < maxSize) {
                    builder.add(Pair.of(7 + paddingX + (18 * x) + 1, 17 + (18 * y) + 1));
                }
            }
        }

        int totalWidth = Math.max((2 * (7 + invX)) + 9 * 18, (2 * (7 + paddingX)) + width * 18);
        int totalHeight = 17 + 18 * height + 14 + 83;

        return Triple.of(
                Pair.of(totalWidth, totalHeight),
                Pair.of(7 + invX + 1, 17 + height * 18 + 14 + 1),
                builder.build()
        );
    }
}
