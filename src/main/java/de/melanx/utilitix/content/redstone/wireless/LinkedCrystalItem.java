package de.melanx.utilitix.content.redstone.wireless;

import de.melanx.utilitix.config.FeatureConfig;
import de.melanx.utilitix.registration.ModDataComponentTypes;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.Level;
import org.moddingx.libx.base.ItemBase;
import org.moddingx.libx.mod.ModX;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.UUID;
import java.util.function.Consumer;

public class LinkedCrystalItem extends ItemBase {

    public LinkedCrystalItem(ModX mod, Properties properties) {
        super(mod, properties);
    }

    @Nonnull
    @Override
    public InteractionResult use(@Nonnull Level level, @Nonnull Player player, @Nonnull InteractionHand hand) {
        ItemStack held = player.getItemInHand(hand);
        UUID uid = LinkedCrystalItem.getId(held);
        if (uid != null) {
            if (!level.isClientSide()) {
                WirelessRedstoneSavedData storage = WirelessRedstoneSavedData.get(level);
                int strength = storage.getStrength(uid);
                player.sendSystemMessage(Component.translatable("tooltip.utilitix.signal_strength", Component.literal(Integer.toString(strength)).withStyle(ChatFormatting.RED)));
            }

            return level.isClientSide() ? InteractionResult.SUCCESS : InteractionResult.SUCCESS_SERVER;
        }

        if (held.getCount() < 2) {
            if (level.isClientSide()) {
                player.sendOverlayMessage(Component.translatable("tooltip.utilitix.link_failed"));
            }

            return InteractionResult.FAIL;
        }

        if (!level.isClientSide()) {
            ItemStack stack = held.copy();
            stack.set(ModDataComponentTypes.redstoneId, UUID.randomUUID());
            player.drop(stack, false);
        }

        player.setItemInHand(hand, ItemStack.EMPTY);

        return level.isClientSide() ? InteractionResult.SUCCESS : InteractionResult.SUCCESS_SERVER;
    }

    @Override
    public void appendHoverText(@Nonnull ItemStack stack, @Nonnull TooltipContext context, @Nonnull TooltipDisplay display, @Nonnull Consumer<Component> builder, @Nonnull TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, context, display, builder, tooltipFlag);
        UUID uid = LinkedCrystalItem.getId(stack);

        builder.accept(uid == null
                ? Component.translatable("tooltip.utilitix.invalid_link").withStyle(ChatFormatting.RED)
                : Component.translatable("tooltip.utilitix.valid_link", Component.literal(uid.toString()).withStyle(ChatFormatting.GREEN)).withStyle(ChatFormatting.RED)
        );
    }

    @Nullable
    public static UUID getId(ItemStack stack) {
        return stack.get(ModDataComponentTypes.redstoneId);
    }

    @Override
    public boolean isEnabled(@Nonnull FeatureFlagSet enabledFeatures) {
        return FeatureConfig.Misc.Redstone.wirelessRedstone;
    }
}
