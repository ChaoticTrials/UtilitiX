package de.melanx.utilitix.content.track;

import de.melanx.utilitix.content.track.tinkerer.MinecartTinkererMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import org.moddingx.libx.base.ItemBase;
import org.moddingx.libx.mod.ModX;

import javax.annotation.Nonnull;

public class ItemMinecartTinkerer extends ItemBase {

    public ItemMinecartTinkerer(ModX mod, Properties properties) {
        super(mod, properties);
    }

    @Override
    public boolean onLeftClickEntity(@Nonnull ItemStack stack, @Nonnull Player player, @Nonnull Entity entity) {
        if (entity instanceof AbstractMinecart minecart) {
            Level level = player.level();
            if (!level.isClientSide && player instanceof ServerPlayer serverPlayer) {
                MinecartTinkererMenu.open(serverPlayer, minecart);
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
        if (tag.contains("utilitix_minecart_label_item", Tag.TAG_COMPOUND)) {
            return ItemStack.parse(entity.registryAccess(), tag.getCompound("utilitix_minecart_label_item")).orElse(ItemStack.EMPTY);
        } else {
            return ItemStack.EMPTY;
        }
    }

    public static void setLabelStack(AbstractMinecart entity, ItemStack stack) {
        CompoundTag tag = entity.getPersistentData();
        if (stack.isEmpty()) {
            tag.remove("utilitix_minecart_label_item");
        } else {
            tag.put("utilitix_minecart_label_item", stack.save(entity.registryAccess(), new CompoundTag()));
        }
    }
}
