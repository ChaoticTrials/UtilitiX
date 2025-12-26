package de.melanx.utilitix.content.crudefurnace;

import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.event.EventHooks;
import net.neoforged.neoforge.items.SlotItemHandler;
import org.moddingx.libx.menu.BlockEntityMenu;
import org.moddingx.libx.menu.type.AdvancedMenuType;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class CrudeFurnaceMenu extends BlockEntityMenu<CrudeFurnaceBlockEntity> {

    public static final AdvancedMenuType<CrudeFurnaceMenu, BlockPos> TYPE = AdvancedMenuType.create(CrudeFurnaceMenu::new,
            StreamCodec.of(
                    (RegistryFriendlyByteBuf buf, BlockPos pos) -> buf.writeBlockPos(pos),
                    buffer -> RegistryFriendlyByteBuf.readBlockPos(buffer)
            ));

    public CrudeFurnaceMenu(@Nullable MenuType<? extends BlockEntityMenu<?>> type, int windowId, Level level, BlockPos pos, Player player, Inventory inventory) {
        super(type, windowId, level, pos, player, inventory, 2, 3);

        this.addSlot(new SlotItemHandler(this.blockEntity.getInventory(), 0, 56, 53));
        this.addSlot(new SlotItemHandler(this.blockEntity.getInventory(), 1, 56, 17));
        this.addSlot(new OutputSlot(player, this.blockEntity, 2, 116, 35));

        this.layoutPlayerInventorySlots(8, 84);
    }

    private static class OutputSlot extends SlotItemHandler {

        private final Player player;
        private final CrudeFurnaceBlockEntity tile;
        private int removeCount;

        public OutputSlot(Player player, CrudeFurnaceBlockEntity tile, int index, int xPosition, int yPosition) {
            super(tile.output, index, xPosition, yPosition);
            this.player = player;
            this.tile = tile;
        }

        @Override
        public void onTake(@Nonnull Player player, @Nonnull ItemStack stack) {
            this.checkTakeAchievements(stack);
            super.onTake(player, stack);
        }

        @Nonnull
        @Override
        public ItemStack remove(int amount) {
            if (this.hasItem()) {
                this.removeCount += Math.min(amount, this.getItem().getCount());
            }

            return super.remove(amount);
        }

        @Override
        protected void onQuickCraft(@Nonnull ItemStack stack, int amount) {
            this.removeCount += amount;
            this.checkTakeAchievements(stack);
        }

        @Override
        protected void checkTakeAchievements(ItemStack stack) {
            stack.onCraftedBy(this.player.level(), this.player, this.removeCount);
            if (this.player instanceof ServerPlayer serverPlayer) {
                this.tile.unlockRecipes(serverPlayer);
            }

            this.removeCount = 0;
            EventHooks.firePlayerSmeltedEvent(this.player, stack);
        }

        @Override
        public boolean mayPlace(@Nonnull ItemStack stack) {
            return false;
        }

        @Override
        public void set(@Nonnull ItemStack stack) {
            // not allowed
        }

        @Override
        public void initialize(@Nonnull ItemStack stack) {
            // not allowed
        }
    }
}
