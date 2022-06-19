package de.melanx.utilitix.content.brewery;

import com.google.common.collect.ImmutableList;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import org.moddingx.libx.base.ItemBase;
import org.moddingx.libx.mod.ModX;

import javax.annotation.Nonnull;
import java.util.List;

public class ItemFailedPotion extends ItemBase {

    private static final List<MobEffect> VERY_LONG_POTIONS = ImmutableList.of(MobEffects.BAD_OMEN, MobEffects.UNLUCK);
    private static final List<MobEffect> LONG_POTIONS = ImmutableList.of(MobEffects.MOVEMENT_SLOWDOWN, MobEffects.POISON, MobEffects.HUNGER, MobEffects.WEAKNESS);
    private static final List<MobEffect> SHORT_POTIONS = ImmutableList.of(MobEffects.CONFUSION, MobEffects.BLINDNESS, MobEffects.LEVITATION);

    public ItemFailedPotion(ModX mod, Properties properties) {
        super(mod, properties);
    }

    @Nonnull
    @Override
    public ItemStack finishUsingItem(@Nonnull ItemStack stack, @Nonnull Level level, @Nonnull LivingEntity entityLiving) {
        Player player = entityLiving instanceof Player ? (Player) entityLiving : null;
        if (player instanceof ServerPlayer) {
            CriteriaTriggers.CONSUME_ITEM.trigger((ServerPlayer) player, stack);
        }
        if (!level.isClientSide) {
            entityLiving.addEffect(new MobEffectInstance(VERY_LONG_POTIONS.get(level.random.nextInt(VERY_LONG_POTIONS.size())), 20 * 60 * 2));
            entityLiving.addEffect(new MobEffectInstance(LONG_POTIONS.get(level.random.nextInt(LONG_POTIONS.size())), 20 * 30));
            entityLiving.addEffect(new MobEffectInstance(SHORT_POTIONS.get(level.random.nextInt(SHORT_POTIONS.size())), 20 * 10));
        }
        if (player != null) {
            player.awardStat(Stats.ITEM_USED.get(this));
            if (!player.getAbilities().instabuild) {
                stack.shrink(1);
            }
        }
        if (player == null || !player.getAbilities().instabuild) {
            if (stack.isEmpty()) {
                return new ItemStack(Items.GLASS_BOTTLE);
            }
            if (player != null) {
                player.getInventory().add(new ItemStack(Items.GLASS_BOTTLE));
            }
        }
        return stack;
    }

    @Override
    public int getUseDuration(@Nonnull ItemStack stack) {
        return 32;
    }

    @Nonnull
    @Override
    public UseAnim getUseAnimation(@Nonnull ItemStack stack) {
        return UseAnim.DRINK;
    }

    @Nonnull
    @Override
    public InteractionResultHolder<ItemStack> use(@Nonnull Level level, @Nonnull Player player, @Nonnull InteractionHand hand) {
        return ItemUtils.startUsingInstantly(level, player, hand);
    }

    @Override
    public boolean isFoil(@Nonnull ItemStack stack) {
        return true;
    }
}
