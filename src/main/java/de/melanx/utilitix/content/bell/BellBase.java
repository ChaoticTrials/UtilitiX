package de.melanx.utilitix.content.bell;

import de.melanx.utilitix.UtilitiXConfig;
import de.melanx.utilitix.registration.ModEnchantments;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import net.minecraftforge.fml.ModList;
import org.moddingx.libx.base.ItemBase;
import org.moddingx.libx.mod.ModX;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Consumer;

public abstract class BellBase extends ItemBase {

    public BellBase(ModX mod, Item.Properties properties) {
        super(mod, properties);
    }

    @Override
    public void initializeClient(@Nonnull Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new IClientItemExtensions() {
            @Override
            public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                return new RenderBell(new BlockEntityRendererProvider.Context(
                        Minecraft.getInstance().getBlockEntityRenderDispatcher(),
                        Minecraft.getInstance().getBlockRenderer(),
                        Minecraft.getInstance().getItemRenderer(),
                        Minecraft.getInstance().getEntityRenderDispatcher(),
                        Minecraft.getInstance().getEntityModels(),
                        Minecraft.getInstance().font
                ));
            }
        });
    }

    @Override
    public void onStopUsing(ItemStack stack, LivingEntity entity, int count) {
        if (count % 4 == 0) {
            boolean ringed = this.dinkDonk(entity, stack);
            if (ringed && entity instanceof Player) {
                ((Player) entity).awardStat(Stats.BELL_RING);
            }
        }
    }

    @Override
    public int getUseDuration(@Nonnull ItemStack stack) {
        return UtilitiXConfig.HandBells.ringTime;
    }

    @Nonnull
    @Override
    public ItemStack finishUsingItem(@Nonnull ItemStack stack, @Nonnull Level level, @Nonnull LivingEntity entityLiving) {
        double range = UtilitiXConfig.HandBells.glowRadius * (1 + stack.getEnchantmentLevel(ModEnchantments.bellRange) * 0.25D);
        List<LivingEntity> entities = level.getEntitiesOfClass(LivingEntity.class, new AABB(entityLiving.getX() - range, entityLiving.getY() - range, entityLiving.getZ() - range, entityLiving.getX() + range, entityLiving.getY() + range, entityLiving.getZ() + range), livingEntity -> this.entityFilter(livingEntity, stack));
        entities.forEach(e -> e.addEffect(new MobEffectInstance(MobEffects.GLOWING, UtilitiXConfig.HandBells.glowTime)));

        return super.finishUsingItem(stack, level, entityLiving);
    }

    @Nonnull
    @Override
    public InteractionResultHolder<ItemStack> use(@Nonnull Level level, @Nonnull Player player, @Nonnull InteractionHand hand) {
        player.startUsingItem(hand);
        return InteractionResultHolder.consume(player.getItemInHand(hand));
    }

    @Nonnull
    @Override
    public UseAnim getUseAnimation(@Nonnull ItemStack stack) {
        return UseAnim.BLOCK;
    }

    public boolean dinkDonk(LivingEntity entity, ItemStack stack) {
        Level level = entity.getCommandSenderWorld();
        BlockPos pos = entity.blockPosition();

        if (!level.isClientSide) {
            if (this.notifyNearbyEntities()) {
                double range = UtilitiXConfig.HandBells.notifyRadius * (1 + stack.getEnchantmentLevel(ModEnchantments.bellRange) * 0.25D);
                List<LivingEntity> entities = entity.getCommandSenderWorld().getEntitiesOfClass(LivingEntity.class, new AABB(entity.getX() - range, entity.getY() - range, entity.getZ() - range, entity.getX() + range, entity.getY() + range, entity.getZ() + range));
                for (LivingEntity e : entities) {
                    e.getBrain().setMemory(MemoryModuleType.HEARD_BELL_TIME, level.getGameTime());
                }
            }

            level.playSound(null, pos, SoundEvents.BELL_BLOCK, SoundSource.BLOCKS, 2.0F, 1.0F);
            return true;
        } else {
            return false;
        }
    }

    protected abstract boolean entityFilter(LivingEntity entity, ItemStack stack);

    protected abstract boolean notifyNearbyEntities();

    @Override
    public void appendHoverText(@Nonnull ItemStack stack, @Nullable Level level, @Nonnull List<Component> tooltip, @Nonnull TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);
        if (ModList.get().isLoaded("emojiful")) {
            tooltip.add(Component.literal(":DinkDonk:"));
        }
    }
}
