package de.melanx.utilitix.content.crudefurnace;

import io.github.noeppi_noeppi.libx.inventory.container.ContainerBase;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.hooks.BasicEventHooks;
import net.minecraftforge.items.SlotItemHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ContainerCrudeFurnace extends ContainerBase<TileCrudeFurnace> {

    public ContainerCrudeFurnace(@Nullable ContainerType<?> type, int windowId, World world, BlockPos pos, PlayerInventory playerInventory, PlayerEntity player) {
        super(type, windowId, world, pos, playerInventory, player, 2, 3);

        this.addSlot(new SlotItemHandler(this.tile.getUnrestricted(), 0, 56, 53));
        this.addSlot(new SlotItemHandler(this.tile.getUnrestricted(), 1, 56, 17));
        this.addSlot(new OutputSlot(player, this.tile, 2, 116, 35));

        this.layoutPlayerInventorySlots(8, 84);
    }

    private static class OutputSlot extends SlotItemHandler {

        private final PlayerEntity player;
        private final TileCrudeFurnace tile;
        private int removeCount;

        public OutputSlot(PlayerEntity player, TileCrudeFurnace tile, int index, int xPosition, int yPosition) {
            super(tile.getUnrestricted(), index, xPosition, yPosition);
            this.player = player;
            this.tile = tile;
        }

        @Nonnull
        @Override
        public ItemStack onTake(@Nonnull PlayerEntity player, @Nonnull ItemStack stack) {
            this.onCrafting(stack);
            super.onTake(player, stack);
            return stack;
        }

        @Nonnull
        @Override
        public ItemStack decrStackSize(int amount) {
            if (this.getHasStack()) {
                this.removeCount += Math.min(amount, this.getStack().getCount());
            }

            return super.decrStackSize(amount);
        }

        @Override
        protected void onCrafting(@Nonnull ItemStack stack, int amount) {
            this.removeCount += amount;
            this.onCrafting(stack);
        }

        @Override
        protected void onCrafting(ItemStack stack) {
            stack.onCrafting(this.player.world, this.player, this.removeCount);
            if (!this.player.world.isRemote) {
                (this.tile).unlockRecipes(this.player);
            }

            this.removeCount = 0;
            BasicEventHooks.firePlayerSmeltedEvent(this.player, stack);
        }
    }
}
