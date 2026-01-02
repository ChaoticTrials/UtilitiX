package de.melanx.utilitix.content;

import de.melanx.utilitix.UtilitiX;
import de.melanx.utilitix.config.FeatureConfig;
import de.melanx.utilitix.registration.ModDataComponentTypes;
import de.melanx.utilitix.util.MobData;
import de.melanx.utilitix.util.MobUtil;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import org.moddingx.libx.base.ItemBase;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Optional;

public class MobYoinkerItem extends ItemBase {

    public MobYoinkerItem(Properties properties) {
        super(UtilitiX.getInstance(), properties);
    }

    @Nonnull
    @Override
    public InteractionResult useOn(@Nonnull UseOnContext context) {
        InteractionHand hand = context.getHand();
        Player player = context.getPlayer();
        if (player == null) {
            return InteractionResult.PASS;
        }

        ItemStack stack = player.getItemInHand(hand);
        MobData mobData = stack.get(ModDataComponentTypes.mobData);
        if (mobData == null) {
            return super.useOn(context);
        }

        Optional<EntityType<?>> entityType = EntityType.byString(mobData.entityType());
        if (entityType.isEmpty()) {
            MobYoinkerItem.reset(stack);
            return super.useOn(context);
        }

        Entity mob = entityType.get().create(player.level());
        if (mob == null) {
            MobYoinkerItem.reset(stack);
            return InteractionResult.PASS;
        }

        mob.load(mobData.entityData());
        mob.setPos(context.getClickLocation());
        if (player.level().addFreshEntity(mob)) {
            MobYoinkerItem.reset(stack);

            return InteractionResult.SUCCESS;
        }

        return super.useOn(context);
    }

    @Override
    public void inventoryTick(@Nonnull ItemStack stack, @Nonnull Level level, @Nonnull Entity entity, int slotId, boolean isSelected) {
        boolean filled = MobUtil.getCurrentMob(stack) != null;
        stack.set(ModDataComponentTypes.filled, filled);
    }

    @Override
    public void appendHoverText(@Nonnull ItemStack stack, @Nonnull TooltipContext context, @Nonnull List<Component> tooltipComponents, @Nonnull TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);

        MutableComponent component = MobUtil.getCurrentMob(stack);
        tooltipComponents.add(component != null ? component : MobUtil.NO_MOB);
    }

    @Override
    public boolean isEnabled(@Nonnull FeatureFlagSet enabledFeatures) {
        return FeatureConfig.Items.mobYoinker;
    }

    private static void reset(ItemStack stack) {
        stack.remove(ModDataComponentTypes.mobData);
    }

    public enum ExperienceMode {
        LEVEL,
        POINTS
    }
}
