package de.melanx.utilitix.util;

import de.melanx.utilitix.UtilitiX;
import de.melanx.utilitix.registration.ModDataComponentTypes;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.TagValueOutput;
import org.moddingx.libx.util.data.ResourceList;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;
import java.util.Optional;

public class MobUtil {

    public static final MutableComponent NO_MOB = Component.translatable("tooltip." + UtilitiX.getInstance().modid + ".no_mob").withStyle(ChatFormatting.DARK_RED);
    private static final MutableComponent DENYLISTED_MOB = Component.translatable("tooltip." + UtilitiX.getInstance().modid + ".blacklisted_mob").withStyle(ChatFormatting.DARK_RED);

    public static boolean storeEntityData(Player player, InteractionHand hand, LivingEntity entity, ResourceList denylist, boolean typeKeyOnly) {
        String entityKey = entity.getEncodeId();
        ItemStack stack = player.getItemInHand(hand);
        MobData mobData = stack.get(ModDataComponentTypes.mobData);
        if (entityKey == null || (mobData != null && entityKey.equals(mobData.entityType()))) {
            return false;
        }

        if (!denylist.test(Identifier.tryParse(entityKey))) {
            player.sendOverlayMessage(DENYLISTED_MOB);
            return false;
        }

        CompoundTag entityData = new CompoundTag();
        if (!typeKeyOnly) {
            try (ProblemReporter.ScopedCollector reporter = new ProblemReporter.ScopedCollector(entity.problemPath(), UtilitiX.getInstance().logger)) {
                TagValueOutput output = TagValueOutput.createWithContext(reporter, entity.registryAccess());
                entity.saveWithoutId(output);
                entityData = output.buildResult();
            }
        }

        MobData newMobData = new MobData(entityKey, entityData);

        if (stack.getCount() > 1) {
            stack.shrink(1);
            ItemStack copyStack = stack.copy();
            copyStack.setCount(1);
            copyStack.set(ModDataComponentTypes.mobData, newMobData);
            player.addItem(copyStack);
        } else {
            stack.set(ModDataComponentTypes.mobData, newMobData);
            player.setItemInHand(hand, stack);
        }

        player.sendOverlayMessage(MobUtil.getCurrentMob(entity.getType()));
        return true;
    }

    @Nullable
    public static MutableComponent getCurrentMob(ItemStack stack) {
        if (!stack.has(ModDataComponentTypes.mobData)) {
            return null;
        }

        String s = Objects.requireNonNull(stack.get(ModDataComponentTypes.mobData)).entityType();
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
