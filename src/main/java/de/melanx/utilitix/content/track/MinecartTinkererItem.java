package de.melanx.utilitix.content.track;

import de.melanx.utilitix.config.FeatureConfig;
import de.melanx.utilitix.content.track.tinkerer.MinecartTinkererMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.minecart.AbstractMinecart;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import org.moddingx.libx.base.ItemBase;
import org.moddingx.libx.mod.ModX;

import javax.annotation.Nonnull;

public class MinecartTinkererItem extends ItemBase {

    public MinecartTinkererItem(ModX mod, Properties properties) {
        super(mod, properties);
    }

    @Override
    public boolean onLeftClickEntity(@Nonnull ItemStack stack, @Nonnull Player player, @Nonnull Entity entity) {
        if (!(entity instanceof AbstractMinecart minecart)) {
            return false;
        }

        Level level = player.level();
        if (!level.isClientSide() && player instanceof ServerPlayer serverPlayer) {
            MinecartTinkererMenu.open(serverPlayer, minecart);
        }

        return true;
    }

    @Override
    public boolean doesSneakBypassUse(@Nonnull ItemStack stack, @Nonnull LevelReader level, @Nonnull BlockPos pos, @Nonnull Player player) {
        return true;
    }

    @Override
    public boolean isEnabled(@Nonnull FeatureFlagSet enabledFeatures) {
        return FeatureConfig.Transportation.moreRails || FeatureConfig.Transportation.moreMinecarts;
    }

    public static ItemStack getLabelStack(AbstractMinecart entity) {
        CompoundTag tag = entity.getPersistentData();

        return ItemStack.OPTIONAL_CODEC.parse(entity.registryAccess().createSerializationContext(NbtOps.INSTANCE), tag.getCompoundOrEmpty("utilitix_minecart_label_item"))
                .result().orElse(ItemStack.EMPTY);
    }

    public static void setLabelStack(AbstractMinecart entity, ItemStack stack) {
        CompoundTag tag = entity.getPersistentData();
        ItemStack.OPTIONAL_CODEC.encodeStart(entity.registryAccess().createSerializationContext(NbtOps.INSTANCE), stack).result()
                .ifPresent(saved -> tag.put("utilitix_minecart_label_item", saved));
    }
}
