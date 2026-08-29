package de.melanx.utilitix.content.quiver;

import de.melanx.utilitix.config.FeatureConfig;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import org.moddingx.libx.base.ItemBase;
import org.moddingx.libx.mod.ModX;
import org.moddingx.libx.registration.Registerable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class QuiverItem extends ItemBase implements Registerable {

    public static final int SLOT_SIZE = 9;

    public QuiverItem(ModX mod, Properties properties) {
        super(mod, properties);
    }

    @Nonnull
    @Override
    public InteractionResult use(@Nonnull Level level, @Nonnull Player player, @Nonnull InteractionHand hand) {
        if (!level.isClientSide() && player instanceof ServerPlayer serverPlayer) {
            QuiverMenu.open(serverPlayer, hand);
        }

        return InteractionResult.SUCCESS;
    }

    @Nullable
    public static QuiverContainer getInventory(ItemStack stack) {
        if (stack.isEmpty()) {
            return null;
        }

        return new QuiverContainer(stack);
    }

    public static boolean isEmpty(ItemStack stack) {
        QuiverContainer inv = QuiverItem.getInventory(stack);

        return inv == null || inv.isEmpty();
    }

    @Override
    public boolean supportsEnchantment(@Nonnull ItemStack stack, @Nonnull Holder<Enchantment> enchantment) {
        return enchantment.is(Enchantments.INFINITY);
    }

    @Override
    public boolean isEnabled(@Nonnull FeatureFlagSet enabledFeatures) {
        return FeatureConfig.Items.quiver;
    }
}
