package de.melanx.utilitix.content.track;

import io.github.noeppi_noeppi.libx.base.ItemBase;
import io.github.noeppi_noeppi.libx.menu.GenericMenu;
import io.github.noeppi_noeppi.libx.mod.ModX;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemStackHandler;

public class ItemMinecartTinkerer extends ItemBase {

    public ItemMinecartTinkerer(ModX mod, Properties properties) {
        super(mod, properties);
    }

    @Override
    public boolean onLeftClickEntity(ItemStack stack, Player player, Entity entity) {
        if (entity instanceof AbstractMinecart) {
            Level level = player.level;
            if (!level.isClientSide && player instanceof ServerPlayer) {
                IItemHandlerModifiable handler = new ItemStackHandler(1) {

                    @Override
                    public int getSlotLimit(int slot) {
                        return 1;
                    }

                    @Override
                    protected void onContentsChanged(int slot) {
                        if (slot == 0) {
                            setLabelStack((AbstractMinecart) entity, this.getStackInSlot(0));
                        }
                    }
                };
                handler.setStackInSlot(0, getLabelStack((AbstractMinecart) entity));
                GenericMenu.open((ServerPlayer) player, handler, new TranslatableComponent("screen.utilitix.minecart_tinkerer"), null);
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean doesSneakBypassUse(ItemStack stack, LevelReader level, BlockPos pos, Player player) {
        return true;
    }

    public static ItemStack getLabelStack(AbstractMinecart entity) {
        CompoundTag nbt = entity.getPersistentData();
        return ItemStack.of(nbt.getCompound("utilitix_minecart_label_item"));
    }

    public static void setLabelStack(AbstractMinecart entity, ItemStack stack) {
        CompoundTag nbt = entity.getPersistentData();
        nbt.put("utilitix_minecart_label_item", stack.save(new CompoundTag()));
    }
}
