package de.melanx.utilitix.content.wireless;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.moddingx.libx.base.ItemBase;
import org.moddingx.libx.mod.ModX;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

public class ItemLinkedCrystal extends ItemBase {

    public ItemLinkedCrystal(ModX mod, Properties properties) {
        super(mod, properties);
    }

    @Nonnull
    @Override
    public InteractionResultHolder<ItemStack> use(@Nonnull Level level, @Nonnull Player player, @Nonnull InteractionHand hand) {
        ItemStack held = player.getItemInHand(hand);
        UUID uid = getId(held);
        if (uid != null) {
            if (!level.isClientSide) {
                WirelessStorage storage = WirelessStorage.get(level);
                int strength = storage.getStrength(uid);
                player.sendSystemMessage(Component.translatable("tooltip.utilitix.signal_strength", Component.literal(Integer.toString(strength)).withStyle(ChatFormatting.RED)));
            }
            return InteractionResultHolder.sidedSuccess(held, level.isClientSide);
        } else if (held.getCount() < 2) {
            if (level.isClientSide) {
                player.displayClientMessage(Component.translatable("tooltip.utilitix.link_failed"), true);
            }
            return new InteractionResultHolder<>(InteractionResult.FAIL, held);
        } else {
            if (!level.isClientSide) {
                ItemStack stack = held.copy();
                stack.getOrCreateTag().putUUID("redstone_id", UUID.randomUUID());
                player.drop(stack, false);
            }
            player.setItemInHand(hand, ItemStack.EMPTY);
            return InteractionResultHolder.sidedSuccess(ItemStack.EMPTY, level.isClientSide);
        }
    }

    @Override
    public void appendHoverText(@Nonnull ItemStack stack, @Nullable Level level, @Nonnull List<Component> tooltip, @Nonnull TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);
        UUID uid = getId(stack);
        if (uid == null) {
            tooltip.add(Component.translatable("tooltip.utilitix.invalid_link").withStyle(ChatFormatting.RED));
        } else {
            tooltip.add(Component.translatable("tooltip.utilitix.valid_link", Component.literal(uid.toString()).withStyle(ChatFormatting.GREEN)).withStyle(ChatFormatting.RED));
        }
    }
    
    @Nullable
    public static UUID getId(ItemStack stack) {
        if (!stack.hasTag()) {
            return null;
        } else {
            try {
                return stack.getOrCreateTag().getUUID("redstone_id");
            } catch (Exception e) {
                return null;
            }
        }
    }
}
