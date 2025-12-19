package de.melanx.utilitix.content.track;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.moddingx.libx.base.ItemBase;
import org.moddingx.libx.mod.ModX;

import javax.annotation.Nonnull;

public class ItemMinecartTinkerer extends ItemBase {

    public ItemMinecartTinkerer(ModX mod, Properties properties) {
        super(mod, properties);
    }

    @Override
    public boolean onLeftClickEntity(@Nonnull ItemStack stack, @Nonnull Player player, @Nonnull Entity entity) {
        if (entity instanceof AbstractMinecart) {
            Level level = player.level();
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
//                GenericMenu.open((ServerPlayer) player, handler, Component.translatable("screen.utilitix.minecart_tinkerer"), null); todo
            }

            return true;
        }
        return false;
    }

    @Override
    public boolean doesSneakBypassUse(@Nonnull ItemStack stack, @Nonnull LevelReader level, @Nonnull BlockPos pos, @Nonnull Player player) {
        return true;
    }

    public static ItemStack getLabelStack(AbstractMinecart entity) {
        CompoundTag tag = entity.getPersistentData();
        return ItemStack.parse(entity.registryAccess(), tag).orElse(ItemStack.EMPTY);
    }

    public static void setLabelStack(AbstractMinecart entity, ItemStack stack) {
        CompoundTag tag = entity.getPersistentData();
        tag.put("utilitix_minecart_label_item", stack.save(entity.registryAccess(), new CompoundTag()));
    }
}
