package de.melanx.utilitix.content.backpack;

import com.google.common.collect.ImmutableList;
import de.melanx.utilitix.compat.curios.UtilCurios;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerInput;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.transfer.access.ItemAccess;
import org.moddingx.libx.inventory.IAdvancedItemHandlerModifiable;
import org.moddingx.libx.menu.MenuBase;
import org.moddingx.libx.menu.slot.BaseSlot;

import javax.annotation.Nonnull;
import java.util.List;

public class BackpackMenu extends MenuBase {

    private static final int MAX_ROWS = 8;
    public static MenuType<BackpackMenu> TYPE;
    private final ItemStack stack;
    public final int width;
    public final int height;
    public final int invX;
    public final int invY;
    public final List<BackpackMenu.Coordinates> slotList;

    public BackpackMenu(MenuType<BackpackMenu> type, int containerId, Level level, ItemStack stack, Player player, Inventory playerInventory) {
        super(type, containerId, playerInventory);

        this.stack = stack;
        IAdvancedItemHandlerModifiable handler = (IAdvancedItemHandlerModifiable) this.stack.getCapability(Capabilities.Item.ITEM, ItemAccess.forStack(stack));
        if (handler == null) {
            throw new IllegalStateException("Tried to open backpack menu without a valid item handler.");
        }

        Layout layout = layoutSlots(handler.size());
        this.width = layout.size().width;
        this.height = layout.size().height;
        this.invX = layout.coordinates().x;
        this.invY = layout.coordinates().y;
        this.slotList = layout.slots();

        for (int i = 0; i < this.slotList.size(); i++) {
            this.addSlot(new BaseSlot(handler, handler, i, this.slotList.get(i).x, this.slotList.get(i).y));
        }

        // Player inventory
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                this.addSlot(new Slot(playerInventory, col + row * 9 + 9, this.invX + col * 18, this.invY + row * 18));
            }
        }

        // Hotbar
        for (int col = 0; col < 9; col++) {
            this.addSlot(new Slot(playerInventory, col, this.invX + col * 18, this.invY + 58));
        }
    }

    @Override
    public boolean stillValid(@Nonnull Player player) {
        ItemStack mainHandItem = player.getItemInHand(InteractionHand.MAIN_HAND);
        ItemStack offHandItem = player.getItemInHand(InteractionHand.OFF_HAND);

        return mainHandItem == this.stack || offHandItem == this.stack || UtilCurios.isBackpackEquipped(player, this.stack);
    }

    @Nonnull
    @Override
    public ItemStack quickMoveStack(@Nonnull Player player, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        //noinspection ConstantConditions
        if (slot != null && slot.hasItem()) {
            ItemStack stack = slot.getItem();
            itemstack = stack.copy();

            final int inventorySize = this.slotList.size();
            final int playerInventoryEnd = inventorySize + 27;
            final int playerHotBarEnd = playerInventoryEnd + 9;

            if (index < inventorySize) {
                if (!this.moveItemStackTo(stack, inventorySize, playerHotBarEnd, true)) {
                    return ItemStack.EMPTY;
                }

                slot.onQuickCraft(stack, itemstack);
            } else {
                if (!this.moveItemStackTo(stack, 0, inventorySize, false)) {
                    return ItemStack.EMPTY;
                }

                if (index < playerInventoryEnd && !this.moveItemStackTo(stack, playerInventoryEnd, playerHotBarEnd, false)) {
                    return ItemStack.EMPTY;
                }

                if (index < playerHotBarEnd && !this.moveItemStackTo(stack, inventorySize, playerInventoryEnd, false)) {
                    return ItemStack.EMPTY;
                }
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
    public void clicked(int slotId, int button, @Nonnull ContainerInput containerInput, @Nonnull Player player) {
        if (slotId >= 0 && this.slots.get(slotId).getItem() == this.stack) {
            return;
        }

        super.clicked(slotId, button, containerInput, player);
    }

    private static Layout layoutSlots(int size) {
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

    private static Layout layoutRectangle(int width, int height, int maxSize) {
        int invX = Math.max((width - 9) * 9, 0);
        int paddingX = width < 9 ? (9 - width) * 9 : 0;

        ImmutableList.Builder<BackpackMenu.Coordinates> builder = ImmutableList.builder();
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if ((y * width + x) < maxSize) {
                    builder.add(new BackpackMenu.Coordinates(7 + paddingX + (18 * x) + 1, 17 + (18 * y) + 1));
                }
            }
        }

        int totalWidth = Math.max((2 * (7 + invX)) + 9 * 18, (2 * (7 + paddingX)) + width * 18);
        int totalHeight = 17 + 18 * height + 14 + 83;

        return new Layout(
                new Size(totalWidth, totalHeight),
                new Coordinates(7 + invX + 1, 17 + height * 18 + 14 + 1),
                builder.build()
        );
    }

    public record Layout(Size size, Coordinates coordinates, List<Coordinates> slots) {}

    public record Coordinates(int x, int y) {}

    public record Size(int width, int height) {}
}
