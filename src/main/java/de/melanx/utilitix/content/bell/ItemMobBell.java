package de.melanx.utilitix.content.bell;

import de.melanx.utilitix.item.ItemMobYoinker;
import de.melanx.utilitix.registration.ModDataComponentTypes;
import de.melanx.utilitix.util.MobUtil;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import org.moddingx.libx.mod.ModX;

import javax.annotation.Nonnull;
import java.util.List;

public class ItemMobBell extends BellBase {

    public ItemMobBell(ModX mod, Item.Properties properties) {
        super(mod, properties);
    }

    @Override
    protected boolean entityFilter(LivingEntity entity, ItemStack stack) {
        if (!stack.has(ModDataComponentTypes.mobData)) {
            return false;
        }

        ItemMobYoinker.MobData mobData = stack.get(ModDataComponentTypes.mobData);

        String s = mobData.entityType();
        return EntityType.getKey(entity.getType()).equals(ResourceLocation.tryParse(s));
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
//        if (stack.getTag() != null && stack.getTag().contains(MobUtil.ENTITY_TYPE_TAG, Tag.TAG_STRING)) { todo
//            ResourceLocation rl = ResourceLocation.tryParse(stack.getTag().getString(MobUtil.ENTITY_TYPE_TAG));
//            EntityType<?> entityType = rl == null ? null : ForgeRegistries.ENTITY_TYPES.getValue(rl);
//            SpawnEggItem egg = entityType == null ? null : ForgeSpawnEggItem.fromEntityType(entityType);
//            if (egg != null) {
//                return Objects.requireNonNull(egg).getColor(0);
//            }
//        }

        return 0xFFFFFF;
    }
}
