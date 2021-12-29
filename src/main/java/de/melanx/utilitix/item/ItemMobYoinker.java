package de.melanx.utilitix.item;

import de.melanx.utilitix.UtilitiX;
import de.melanx.utilitix.content.bell.ItemMobBell;
import de.melanx.utilitix.util.MobUtil;
import io.github.noeppi_noeppi.libx.base.ItemBase;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;

public class ItemMobYoinker extends ItemBase {

    public ItemMobYoinker(Properties properties) {
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
        CompoundTag nbt = stack.getOrCreateTag();
        if (nbt.contains(MobUtil.ENTITY_TYPE_TAG)) {
            String entityKey = nbt.getString(MobUtil.ENTITY_TYPE_TAG);
            Optional<EntityType<?>> entityType = EntityType.byString(entityKey);
            if (entityType.isPresent()) {
                Entity mob = entityType.get().create(player.level);
                if (mob == null) {
                    ItemMobYoinker.reset(stack);
                    return InteractionResult.PASS;
                }

                mob.load(nbt.getCompound(MobUtil.ENTITY_DATA_TAG));
                mob.setPos(context.getClickLocation());
                if (player.level.addFreshEntity(mob)) {
                    ItemMobYoinker.reset(stack);
                    return InteractionResult.SUCCESS;
                }
            } else {
                ItemMobYoinker.reset(stack);
            }
        }

        return super.useOn(context);
    }

    @Override
    public void appendHoverText(@Nonnull ItemStack stack, @Nullable Level level, @Nonnull List<Component> tooltip, @Nonnull TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);
        MutableComponent component = ItemMobBell.getCurrentMob(stack);
        tooltip.add(component != null ? component : MobUtil.NO_MOB);
    }

    private static void reset(ItemStack stack) {
        CompoundTag nbt = stack.getOrCreateTag();
        nbt.remove(MobUtil.ENTITY_TYPE_TAG);
        nbt.remove(MobUtil.ENTITY_DATA_TAG);
        stack.setTag(nbt);
    }
}
