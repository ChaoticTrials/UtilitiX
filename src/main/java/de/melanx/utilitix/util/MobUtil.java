package de.melanx.utilitix.util;

import de.melanx.utilitix.UtilitiX;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.moddingx.libx.util.data.ResourceList;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Optional;

public class MobUtil {

    public static final String ENTITY_TYPE_TAG = "EntityType";
    public static final String ENTITY_DATA_TAG = "EntityData";
    public static final MutableComponent NO_MOB = Component.translatable("tooltip." + UtilitiX.getInstance().modid + ".no_mob").withStyle(ChatFormatting.DARK_RED);
    private static final MutableComponent DENYLISTED_MOB = Component.translatable("tooltip." + UtilitiX.getInstance().modid + ".blacklisted_mob").withStyle(ChatFormatting.DARK_RED);

    public static boolean storeEntityData(Player player, InteractionHand hand, LivingEntity entity, ResourceList denylist, boolean typeKeyOnly) {
        String entityKey = entity.getEncodeId();
        ItemStack stack = player.getItemInHand(hand);
        CompoundTag nbt = stack.getOrCreateTag().copy();
        if (entityKey == null || entityKey.equals(nbt.getString(MobUtil.ENTITY_TYPE_TAG))) {
            return false;
        }

        if (!denylist.test(new ResourceLocation(entityKey))) {
            player.displayClientMessage(DENYLISTED_MOB, true);
            return false;
        }

        nbt.putString(ENTITY_TYPE_TAG, entityKey);
        if (!typeKeyOnly) {
            nbt.put(MobUtil.ENTITY_DATA_TAG, entity.saveWithoutId(new CompoundTag()));
        }

        if (stack.getCount() > 1) {
            stack.shrink(1);
            ItemStack copyStack = stack.copy();
            copyStack.setCount(1);
            copyStack.setTag(nbt);
            player.addItem(copyStack);
        } else {
            stack.setTag(nbt);
            player.setItemInHand(hand, stack);
        }

        player.displayClientMessage(MobUtil.getCurrentMob(entity.getType()), true);
        return true;
    }

    @Nullable
    public static MutableComponent getCurrentMob(ItemStack stack) {
        String s = stack.getOrCreateTag().getString(MobUtil.ENTITY_TYPE_TAG);
        Optional<EntityType<?>> entityType = EntityType.byString(s);

        return entityType.map(MobUtil::getCurrentMob).orElse(null);
    }

    @Nonnull
    public static MutableComponent getCurrentMob(EntityType<?> entityType) {
        Component name = entityType.getDescription();
        MutableComponent component = Component.translatable("tooltip." + UtilitiX.getInstance().modid + ".current_mob");
        component.withStyle(entityType.getCategory() == MobCategory.MONSTER ? ChatFormatting.RED : ChatFormatting.GOLD);

        return component.append(": ").append(name);
    }
}
