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
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import org.moddingx.libx.mod.ModX;

import javax.annotation.Nonnull;
import java.util.function.Consumer;

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
    public void appendHoverText(@Nonnull ItemStack stack, @Nonnull TooltipContext context, @Nonnull TooltipDisplay display, @Nonnull Consumer<Component> tooltipComponents, @Nonnull TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, context, display, tooltipComponents, tooltipFlag);
        MutableComponent component = MobUtil.getCurrentMob(stack);

        tooltipComponents.accept(component != null ? component : MobUtil.NO_MOB);
    }

    public static int getColor(ItemStack stack) {
        MobData mobData = stack.get(ModDataComponentTypes.mobData);

        if (mobData == null) {
            return NO_COLOR;
        }

        EntityType<?> entityType = mobData.getEntityType();
        if (entityType == null) {
            return NO_COLOR;
        }

        return switch(entityType.getCategory()) {
            case MONSTER -> 0x00AFAF;
            case CREATURE -> 0xEDC343;
            case AMBIENT -> 0x4C3E30;
            case AXOLOTLS -> 0xFBC1E3;
            case UNDERGROUND_WATER_CREATURE -> 0x0613A3;
            case WATER_CREATURE -> 0x0661A3;
            case WATER_AMBIENT -> 0xEF6915;
            default -> 0xDBCDC2;
        };
    }
}
