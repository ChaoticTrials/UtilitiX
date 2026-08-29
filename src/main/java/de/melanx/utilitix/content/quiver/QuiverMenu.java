package de.melanx.utilitix.content.quiver;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerInput;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.moddingx.libx.menu.MenuBase;
import org.moddingx.libx.menu.type.AdvancedMenuType;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class QuiverMenu extends MenuBase {

    private static final int QUIVER_SLOT_COUNT = QuiverItem.SLOT_SIZE;
    private static final int QUIVER_FIRST_SLOT = 0;
    private static final int PLAYER_FIRST_SLOT = QUIVER_FIRST_SLOT + QUIVER_SLOT_COUNT;

    public static final AdvancedMenuType<QuiverMenu, InteractionHand> TYPE = AdvancedMenuType.create(
            QuiverMenu::new,
            StreamCodec.of(
                    FriendlyByteBuf::writeEnum,
                    (RegistryFriendlyByteBuf buf) -> buf.readEnum(InteractionHand.class)
            )
    );

    private final InteractionHand hand;
    private final ItemStack quiverStack;
    private final QuiverContainer quiverInv;

    public QuiverMenu(@Nullable MenuType<?> type, int windowId, Level level, InteractionHand hand, Player player, Inventory inventory) {
        super(type, windowId, inventory);
        this.hand = hand;
        this.quiverStack = player.getItemInHand(hand);

        QuiverContainer inv = QuiverItem.getInventory(this.quiverStack);
        if (inv == null) {
            // Should never happen
            throw new IllegalStateException("Tried to open quiver menu without a valid quiver stack.");
        }
        this.quiverInv = inv;

        for (int i = 0; i < QUIVER_SLOT_COUNT; i++) {
            this.addSlot(new ArrowSlot(this.quiverInv, i, 8 + i * 18, 18));
        }

        this.layoutPlayerInventorySlots(8, 50);
    }

    public static void open(ServerPlayer player, InteractionHand hand) {
        QuiverMenu.TYPE.open(player, Component.translatable("screen.utilitix.quiver"), hand);
    }

    public ItemStack getQuiverStack() {
        return this.quiverStack;
    }

    @Override
    public boolean stillValid(@Nonnull Player player) {
        return player.isAlive() && player.getItemInHand(this.hand) == this.quiverStack;
    }

    @Override
    public void removed(@Nonnull Player player) {
        super.removed(player);
        this.quiverInv.setChanged();
    }

    @Nonnull
    @Override
    public ItemStack quickMoveStack(@Nonnull Player player, int index) {
        Slot slot = this.slots.get(index);
        if (!slot.hasItem()) {
            return ItemStack.EMPTY;
        }

        ItemStack slotStack = slot.getItem();
        ItemStack copy = slotStack.copy();

        if (index < PLAYER_FIRST_SLOT) {
            // From quiver -> player
            if (!this.moveItemStackTo(slotStack, PLAYER_FIRST_SLOT, this.slots.size(), true)) {
                return ItemStack.EMPTY;
            }
        } else {
            // From player -> quiver
            if (!this.moveItemStackTo(slotStack, QUIVER_FIRST_SLOT, PLAYER_FIRST_SLOT, false)) {
                return ItemStack.EMPTY;
            }
        }

        if (slotStack.isEmpty()) {
            slot.set(ItemStack.EMPTY);
        } else {
            slot.setChanged();
        }

        return copy;
    }

    @Override
    public void clicked(int slotId, int button, @Nonnull ContainerInput containerInput, @Nonnull Player player) {
        if (slotId >= 0 && slotId < this.slots.size() && player.getItemInHand(this.hand) == this.getSlot(slotId).getItem()) {
            return;
        }

        super.clicked(slotId, button, containerInput, player);
    }

    private static class ArrowSlot extends Slot {

        public ArrowSlot(QuiverContainer container, int slot, int x, int y) {
            super(container, slot, x, y);
        }

        @Override
        public boolean mayPlace(@Nonnull ItemStack stack) {
            return stack.is(ItemTags.ARROWS);
        }
    }
}
