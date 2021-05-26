package de.melanx.utilitix.content.brewery;

import com.google.common.collect.ImmutableList;
import io.github.noeppi_noeppi.libx.mod.ModX;
import io.github.noeppi_noeppi.libx.mod.registration.ItemBase;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.UseAction;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.stats.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.DrinkHelper;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import java.util.List;

public class ItemFailedPotion extends ItemBase {

    private static final List<Effect> VERY_LONG_POTIONS = ImmutableList.of(Effects.BAD_OMEN, Effects.UNLUCK);
    private static final List<Effect> LONG_POTIONS = ImmutableList.of(Effects.SLOWNESS, Effects.POISON, Effects.HUNGER, Effects.WEAKNESS);
    private static final List<Effect> SHORT_POTIONS = ImmutableList.of(Effects.NAUSEA, Effects.BLINDNESS, Effects.LEVITATION);
    
    public ItemFailedPotion(ModX mod, Properties properties) {
        super(mod, properties);
    }

    @Nonnull
    @Override
    public ItemStack onItemUseFinish(@Nonnull ItemStack stack, @Nonnull World world, @Nonnull LivingEntity living) {
        PlayerEntity player = living instanceof PlayerEntity ? (PlayerEntity)living : null;
        if (player instanceof ServerPlayerEntity) {
            CriteriaTriggers.CONSUME_ITEM.trigger((ServerPlayerEntity)player, stack);
        }
        if (!world.isRemote) {
            living.addPotionEffect(new EffectInstance(VERY_LONG_POTIONS.get(world.rand.nextInt(VERY_LONG_POTIONS.size())), 20 * 60 * 2));
            living.addPotionEffect(new EffectInstance(LONG_POTIONS.get(world.rand.nextInt(LONG_POTIONS.size())), 20 * 30));
            living.addPotionEffect(new EffectInstance(SHORT_POTIONS.get(world.rand.nextInt(SHORT_POTIONS.size())), 20 * 10));
        }
        if (player != null) {
            player.addStat(Stats.ITEM_USED.get(this));
            if (!player.abilities.isCreativeMode) {
                stack.shrink(1);
            }
        }
        if (player == null || !player.abilities.isCreativeMode) {
            if (stack.isEmpty()) {
                return new ItemStack(Items.GLASS_BOTTLE);
            }
            if (player != null) {
                player.inventory.addItemStackToInventory(new ItemStack(Items.GLASS_BOTTLE));
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
    public UseAction getUseAction(@Nonnull ItemStack stack) {
        return UseAction.DRINK;
    }
    
    @Nonnull
    @Override
    public ActionResult<ItemStack> onItemRightClick(@Nonnull World world, @Nonnull PlayerEntity player, @Nonnull Hand hand) {
        return DrinkHelper.startDrinking(world, player, hand);
    }

    @Override
    public boolean hasEffect(@Nonnull ItemStack stack) {
        return true;
    }
}
