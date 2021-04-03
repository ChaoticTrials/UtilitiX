package de.melanx.utilitix.item.bells;

import de.melanx.utilitix.UtilitiX;
import de.melanx.utilitix.UtilitiXConfig;
import io.github.noeppi_noeppi.libx.mod.ModX;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;

public class MobBell extends BellBase {

    private static final IFormattableTextComponent NO_MOB = new TranslationTextComponent("tooltip." + UtilitiX.getInstance().modid + ".no_mob").mergeStyle(TextFormatting.DARK_RED);
    private static final IFormattableTextComponent BLACKLISTED_MOB = new TranslationTextComponent("tooltip." + UtilitiX.getInstance().modid + ".blacklisted_mob").mergeStyle(TextFormatting.DARK_RED);

    public MobBell(ModX mod, Item.Properties properties) {
        super(mod, properties);
    }

    @Nonnull
    @Override
    public ActionResultType itemInteractionForEntity(@Nonnull ItemStack stack, @Nonnull PlayerEntity player, @Nonnull LivingEntity target, @Nonnull Hand hand) {
        if (target.getEntityWorld().isRemote || !target.isAlive() || !player.isSneaking()) {
            return super.itemInteractionForEntity(stack, player, target, hand);
        }

        ResourceLocation entityKey = EntityType.getKey(target.getType());
        if (entityKey.toString().equals(stack.getOrCreateTag().getString("entity"))) {
            return ActionResultType.FAIL;
        }

        if (UtilitiXConfig.HandBells.blacklist.contains(entityKey)) {
            player.sendStatusMessage(BLACKLISTED_MOB, true);
            return ActionResultType.FAIL;
        }

        stack.getOrCreateTag().putString("entity", entityKey.toString());
        player.setHeldItem(hand, stack);
        player.sendStatusMessage(getCurrentMob(stack, target.getType()), true);
        return ActionResultType.SUCCESS;
    }

    @Override
    boolean entityFilter(LivingEntity entity, ItemStack stack) {
        if (!stack.hasTag()) {
            return false;
        }

        if (!stack.getOrCreateTag().contains("entity")) {
            return false;
        }

        String s = stack.getOrCreateTag().getString("entity");
        return EntityType.getKey(entity.getType()).toString().equals(s);
    }

    @Override
    boolean notifyNearbyEntities() {
        return false;
    }

    @Override
    public void addInformation(@Nonnull ItemStack stack, @Nullable World world, @Nonnull List<ITextComponent> tooltip, @Nonnull ITooltipFlag flag) {
        super.addInformation(stack, world, tooltip, flag);
        IFormattableTextComponent component = getCurrentMob(stack);
        tooltip.add(component != null ? component : NO_MOB);
    }

    @Nullable
    private static IFormattableTextComponent getCurrentMob(ItemStack stack) {
        String s = stack.getOrCreateTag().getString("entity");
        Optional<EntityType<?>> entityType = EntityType.byKey(s);

        return entityType.map(type -> getCurrentMob(stack, type)).orElse(null);
    }

    @Nonnull
    private static IFormattableTextComponent getCurrentMob(ItemStack stack, EntityType<?> entityType) {
        ITextComponent name = entityType.getName();
        IFormattableTextComponent component = new TranslationTextComponent("tooltip." + UtilitiX.getInstance().modid + ".current_mob");
        component.mergeStyle(entityType.getClassification() == EntityClassification.MONSTER ? TextFormatting.RED : TextFormatting.GOLD);

        return component.appendString(": ").append(name);
    }
}
