package de.melanx.utilitix.content.track.tinkerer;

import de.melanx.utilitix.content.track.MinecartTinkererItem;
import de.melanx.utilitix.content.track.rails.ControllerRailBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.neoforged.neoforge.items.SlotItemHandler;
import org.moddingx.libx.menu.MenuBase;
import org.moddingx.libx.menu.type.AdvancedMenuType;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class MinecartTinkererMenu extends MenuBase {

    private static final int LABEL_SLOT = 0;
    private static final Component TITLE = Component.translatable("screen.utilitix.minecart_tinkerer");

    public static final AdvancedMenuType<MinecartTinkererMenu, Target> TYPE = AdvancedMenuType.create(
            MinecartTinkererMenu::new,
            StreamCodec.of(
                    (buf, target) -> {
                        buf.writeEnum(target.type());
                        switch(target.type()) {
                            case ENTITY -> buf.writeVarInt(target.entityId());
                            case BLOCK -> buf.writeBlockPos(target.pos());
                        }
                    },
                    buf -> {
                        TargetType targetType = buf.readEnum(TargetType.class);
                        return switch(targetType) {
                            case ENTITY -> Target.entity(buf.readVarInt());
                            case BLOCK -> Target.block(buf.readBlockPos());
                        };
                    }
            )
    );

    private final Level level;
    private final Target target;
    @Nullable
    private final AbstractMinecart minecart;
    @Nullable
    private final ControllerRailBlockEntity controllerRail;

    public MinecartTinkererMenu(@Nullable MenuType<?> type, int windowId, Level level, Target target, Player player, Inventory playerInventory) {
        super(type, windowId, playerInventory);
        this.level = level;
        this.target = target;

        switch(target.type()) {
            case ENTITY -> {
                this.minecart = level.getEntity(target.entityId()) instanceof AbstractMinecart cart ? cart : null;
                this.controllerRail = null;
            }
            case BLOCK -> {
                this.minecart = null;
                this.controllerRail = level.getBlockEntity(target.pos()) instanceof ControllerRailBlockEntity rail ? rail : null;
            }
            default -> throw new IllegalStateException("Unknown target type: " + target.type());
        }

        ItemStackHandler labelInventory = new ItemStackHandler(1) {

            @Override
            public int getSlotLimit(int slot) {
                return 1;
            }

            @Override
            protected void onContentsChanged(int slot) {
                if (slot != LABEL_SLOT) return;

                if (!MinecartTinkererMenu.this.level.isClientSide) {
                    ItemStack stack = this.getStackInSlot(LABEL_SLOT);
                    if (MinecartTinkererMenu.this.minecart != null) {
                        MinecartTinkererItem.setLabelStack(MinecartTinkererMenu.this.minecart, stack);
                    } else if (MinecartTinkererMenu.this.controllerRail != null) {
                        MinecartTinkererMenu.this.controllerRail.setFilterStack(stack);
                    }
                }
            }
        };

        switch(target.type()) {
            case ENTITY -> labelInventory.setStackInSlot(LABEL_SLOT, MinecartTinkererItem.getLabelStack(this.minecart));
            case BLOCK -> labelInventory.setStackInSlot(LABEL_SLOT, this.controllerRail.getFilterStack().copy());
        }

        this.addSlot(new SlotItemHandler(labelInventory, LABEL_SLOT, 80, 18));
        this.layoutPlayerInventorySlots(8, 50);
    }

    public static void open(ServerPlayer player, AbstractMinecart minecart) {
        TYPE.open(player, MinecartTinkererMenu.TITLE, Target.entity(minecart.getId()));
    }

    public static void open(ServerPlayer player, BlockPos pos) {
        TYPE.open(player, MinecartTinkererMenu.TITLE, Target.block(pos));
    }

    @Nullable
    public AbstractMinecart getMinecart() {
        return this.minecart;
    }

    @Nullable
    public ControllerRailBlockEntity getControllerRail() {
        return this.controllerRail;
    }

    @Override
    public boolean stillValid(@Nonnull Player player) {
        return switch(this.target.type()) {
            case ENTITY -> player.distanceToSqr(this.minecart) <= 64;
            case BLOCK -> !this.controllerRail.isRemoved();
        };
    }

    @Nonnull
    @Override
    public ItemStack quickMoveStack(@Nonnull Player player, int index) {
        var slot = this.slots.get(index);
        if (!slot.hasItem()) {
            return ItemStack.EMPTY;
        }

        ItemStack slotStack = slot.getItem();
        ItemStack copy = slotStack.copy();

        if (index == LABEL_SLOT) {
            if (!this.moveItemStackTo(slotStack, 1, this.slots.size(), true)) {
                return ItemStack.EMPTY;
            }
        } else {
            if (!this.moveItemStackTo(slotStack, 0, 1, false)) {
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

    public enum TargetType {
        ENTITY,
        BLOCK
    }

    public record Target(TargetType type, int entityId, BlockPos pos) {
        public static Target entity(int entityId) {
            return new Target(TargetType.ENTITY, entityId, BlockPos.ZERO);
        }

        public static Target block(BlockPos pos) {
            return new Target(TargetType.BLOCK, -1, pos);
        }
    }
}
