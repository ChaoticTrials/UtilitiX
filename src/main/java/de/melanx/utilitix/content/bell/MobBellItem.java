package de.melanx.utilitix.content.bell;

import de.melanx.utilitix.registration.ModDataComponentTypes;
import de.melanx.utilitix.util.MobData;
import de.melanx.utilitix.util.MobUtil;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.item.TooltipFlag;
import org.moddingx.libx.mod.ModX;

import javax.annotation.Nonnull;
import java.util.List;

public class MobBellItem extends BellBase {

    private static final int NO_COLOR = 0xFFFFFF;

    public MobBellItem(ModX mod, Item.Properties properties) {
        super(mod, properties);
    }

    @Override
    protected boolean entityFilter(LivingEntity entity, ItemStack stack) {
        if (!stack.has(ModDataComponentTypes.mobData)) {
            return false;
        }

        MobData mobData = stack.get(ModDataComponentTypes.mobData);

        //noinspection DataFlowIssue
        return entity.getType() == mobData.getEntityType();
    }

    @Override
    protected boolean notifyNearbyEntities() {
        return false;
    }

    @Override
    public void appendHoverText(@Nonnull ItemStack stack, @Nonnull TooltipContext context, @Nonnull List<Component> tooltipComponents, @Nonnull TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
        MutableComponent component = MobUtil.getCurrentMob(stack);

        tooltipComponents.add(component != null ? component : MobUtil.NO_MOB);
    }

    public static int getColor(ItemStack stack) {
        if (!stack.has(ModDataComponentTypes.mobData)) {
            return NO_COLOR;
        }

        MobData mobData = stack.get(ModDataComponentTypes.mobData);
        //noinspection DataFlowIssue
        EntityType<?> entityType = mobData.getEntityType();
        if (entityType == null) {
            return NO_COLOR;
        }

        SpawnEggItem egg = SpawnEggItem.byId(entityType);
        if (egg != null) {
            return egg.getColor(0);
        }

        return NO_COLOR;
    }
}
