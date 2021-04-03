package de.melanx.utilitix.item.bells;

import de.melanx.utilitix.UtilitiXConfig;
import io.github.noeppi_noeppi.libx.mod.ModX;
import io.github.noeppi_noeppi.libx.mod.registration.ItemBase;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.UseAction;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.stats.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.fml.ModList;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public abstract class BellBase extends ItemBase {

    public BellBase(ModX mod, Item.Properties properties) {
        super(mod, properties);
    }

    @Override
    public void onUsingTick(ItemStack stack, LivingEntity entity, int count) {
        if (count % 4 == 0) {
            boolean ringed = this.dinkDonk(entity);
            if (ringed && entity instanceof PlayerEntity) {
                ((PlayerEntity) entity).addStat(Stats.BELL_RING);
            }
        }
    }

    @Override
    public int getUseDuration(@Nonnull ItemStack stack) {
        return UtilitiXConfig.HandBells.ringTime;
    }

    @Nonnull
    @Override
    public ItemStack onItemUseFinish(@Nonnull ItemStack stack, @Nonnull World world, @Nonnull LivingEntity entity) {
        List<LivingEntity> entities = world.getEntitiesWithinAABB(LivingEntity.class, new AxisAlignedBB(entity.getPosX() - UtilitiXConfig.HandBells.glowRadius, entity.getPosY() - UtilitiXConfig.HandBells.glowRadius, entity.getPosZ() - UtilitiXConfig.HandBells.glowRadius, entity.getPosX() + UtilitiXConfig.HandBells.glowRadius, entity.getPosY() + UtilitiXConfig.HandBells.glowRadius, entity.getPosZ() + UtilitiXConfig.HandBells.glowRadius), livingEntity -> this.entityFilter(livingEntity, stack));
        entities.forEach(e -> {
            e.addPotionEffect(new EffectInstance(Effects.GLOWING, UtilitiXConfig.HandBells.glowTime));
        });

        return super.onItemUseFinish(stack, world, entity);
    }

    @Nonnull
    @Override
    public ActionResult<ItemStack> onItemRightClick(@Nonnull World world, @Nonnull PlayerEntity player, @Nonnull Hand hand) {
        player.setActiveHand(hand);
        return ActionResult.resultConsume(player.getHeldItem(hand));
    }

    @Nonnull
    @Override
    public UseAction getUseAction(@Nonnull ItemStack stack) {
        return UseAction.BLOCK;
    }

    public boolean dinkDonk(LivingEntity entity) {
        World world = entity.getEntityWorld();
        BlockPos pos = entity.getPosition();

        if (!world.isRemote && this.notifyNearbyEntities()) {
            List<LivingEntity> entities = entity.getEntityWorld().getEntitiesWithinAABB(LivingEntity.class, new AxisAlignedBB(entity.getPosX() - UtilitiXConfig.HandBells.notifyRadius, entity.getPosY() - UtilitiXConfig.HandBells.notifyRadius, entity.getPosZ() - UtilitiXConfig.HandBells.notifyRadius, entity.getPosX() + UtilitiXConfig.HandBells.notifyRadius, entity.getPosY() + UtilitiXConfig.HandBells.notifyRadius, entity.getPosZ() + UtilitiXConfig.HandBells.notifyRadius));
            for (LivingEntity e : entities) {
                e.getBrain().setMemory(MemoryModuleType.HEARD_BELL_TIME, world.getGameTime());
            }

            world.playSound(null, pos, SoundEvents.BLOCK_BELL_USE, SoundCategory.BLOCKS, 2.0F, 1.0F);
            return true;
        } else {
            return false;
        }
    }

    abstract boolean entityFilter(LivingEntity entity, ItemStack stack);
    abstract boolean notifyNearbyEntities();

    @Override
    public void addInformation(@Nonnull ItemStack stack, @Nullable World world, @Nonnull List<ITextComponent> tooltip, @Nonnull ITooltipFlag flag) {
        super.addInformation(stack, world, tooltip, flag);
        if (ModList.get().isLoaded("emojiful")) {
            tooltip.add(new StringTextComponent(":DinkDonk:"));
        }
    }
}
