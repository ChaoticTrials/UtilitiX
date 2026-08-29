package de.melanx.utilitix.content.bell;

import com.mojang.serialization.MapCodec;
import net.minecraft.client.color.item.ItemTintSource;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.util.ARGB;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class MobBellTintSource implements ItemTintSource {

    public static final MapCodec<MobBellTintSource> CODEC = MapCodec.unit(new MobBellTintSource());

    @Override
    public int calculate(@Nonnull ItemStack stack, @Nullable ClientLevel level, @Nullable LivingEntity owner) {
        return ARGB.opaque(MobBellItem.getColor(stack));
    }

    @Nonnull
    @Override
    public MapCodec<? extends ItemTintSource> type() {
        return CODEC;
    }
}
