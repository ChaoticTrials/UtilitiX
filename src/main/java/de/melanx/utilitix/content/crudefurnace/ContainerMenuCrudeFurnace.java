package de.melanx.utilitix.content.crudefurnace;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.items.SlotItemHandler;
import org.moddingx.libx.menu.BlockEntityMenu;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ContainerMenuCrudeFurnace extends BlockEntityMenu<TileCrudeFurnace> {

    public ContainerMenuCrudeFurnace(@Nullable MenuType<? extends BlockEntityMenu<?>> type, int windowId, Level level, BlockPos pos, Inventory playerContainer, Player player) {
        super(type, windowId, level, pos, playerContainer, player, 2, 3);

        this.addSlot(new SlotItemHandler(this.blockEntity.getInventory(), 0, 56, 53));
        this.addSlot(new SlotItemHandler(this.blockEntity.getInventory(), 1, 56, 17));
        this.addSlot(new OutputSlot(player, this.blockEntity, 2, 116, 35));

        this.layoutPlayerInventorySlots(8, 84);
    }

    private static class OutputSlot extends SlotItemHandler {

        private final Player player;
        private final TileCrudeFurnace tile;
        private int removeCount;

        public OutputSlot(Player player, TileCrudeFurnace tile, int index, int xPosition, int yPosition) {
            super(tile.getUnrestricted(), index, xPosition, yPosition);
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
            if (!this.player.level().isClientSide) {
                this.tile.unlockRecipes(this.player);
            }

            this.removeCount = 0;
            ForgeEventFactory.firePlayerSmeltedEvent(this.player, stack);
        }
    }
}
