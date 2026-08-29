package de.melanx.utilitix.content.brewery;

import com.google.common.collect.ImmutableList;
import de.melanx.utilitix.config.FeatureConfig;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUseAnimation;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import org.moddingx.libx.base.ItemBase;
import org.moddingx.libx.mod.ModX;

import javax.annotation.Nonnull;
import java.util.List;

public class FailedPotionItem extends ItemBase {

    public static final int DRINK_DURATION = 32; // see PotionItem.DRINK_DURATION
    private static final List<Holder<MobEffect>> VERY_LONG_POTIONS = ImmutableList.of(MobEffects.BAD_OMEN, MobEffects.UNLUCK);
    private static final List<Holder<MobEffect>> LONG_POTIONS = ImmutableList.of(MobEffects.SLOWNESS, MobEffects.POISON, MobEffects.HUNGER, MobEffects.WEAKNESS);
    private static final List<Holder<MobEffect>> SHORT_POTIONS = ImmutableList.of(MobEffects.NAUSEA, MobEffects.BLINDNESS, MobEffects.LEVITATION);

    public FailedPotionItem(ModX mod, Properties properties) {
        super(mod, properties);
    }

    @Nonnull
    @Override
    public ItemStack finishUsingItem(@Nonnull ItemStack stack, @Nonnull Level level, @Nonnull LivingEntity entityLiving) {
        Player player = entityLiving instanceof Player ? (Player) entityLiving : null;
        if (player instanceof ServerPlayer) {
            CriteriaTriggers.CONSUME_ITEM.trigger((ServerPlayer) player, stack);
        }

        if (!level.isClientSide()) {
            RandomSource random = level.getRandom();
            entityLiving.addEffect(new MobEffectInstance(VERY_LONG_POTIONS.get(random.nextInt(VERY_LONG_POTIONS.size())), 20 * 60 * 2));
            entityLiving.addEffect(new MobEffectInstance(LONG_POTIONS.get(random.nextInt(LONG_POTIONS.size())), 20 * 30));
            entityLiving.addEffect(new MobEffectInstance(SHORT_POTIONS.get(random.nextInt(SHORT_POTIONS.size())), 20 * 10));
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
    public int getUseDuration(@Nonnull ItemStack stack, @Nonnull LivingEntity entity) {
        return FailedPotionItem.DRINK_DURATION;
    }

    @Nonnull
    @Override
    public ItemUseAnimation getUseAnimation(@Nonnull ItemStack stack) {
        return ItemUseAnimation.DRINK;
    }

    @Nonnull
    @Override
    public InteractionResult use(@Nonnull Level level, @Nonnull Player player, @Nonnull InteractionHand hand) {
        return ItemUtils.startUsingInstantly(level, player, hand);
    }

    @Override
    public boolean isFoil(@Nonnull ItemStack stack) {
        return true;
    }

    @Override
    public boolean isEnabled(@Nonnull FeatureFlagSet enabledFeatures) {
        return FeatureConfig.Machines.advancedBrewery;
    }
}
