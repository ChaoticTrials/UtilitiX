package de.melanx.utilitix.content.quiver;

import de.melanx.utilitix.registration.ModDataComponentTypes;
import de.melanx.utilitix.registration.ModItems;
import net.minecraft.core.Holder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingGetProjectileEvent;
import net.neoforged.neoforge.event.entity.player.ArrowLooseEvent;

import javax.annotation.Nullable;

@EventBusSubscriber
public class QuiverEventHandler {

    private static final String QUIVER_PENDING = "UtilitiXQuiverPending";
    private static final String KEY_INV_SLOT = "InvSlot";
    private static final String KEY_QUIVER_SLOT = "QuiverSlot";

    @SubscribeEvent
    public static void onGetProjectile(LivingGetProjectileEvent event) {
        if (!(event.getEntity() instanceof Player player)) {
            return;
        }

        if (!event.getProjectileItemStack().isEmpty()) {
            if (!player.level().isClientSide) {
                player.getPersistentData().remove(QUIVER_PENDING);
            }

            return;
        }

        FoundArrow found = QuiverEventHandler.findArrowInAnyQuiver(player);
        if (found == null) {
            return;
        }

        Holder<Enchantment> infinity = player.level().registryAccess().holderOrThrow(Enchantments.INFINITY);
        boolean appliesInfinity = QuiverEventHandler.appliesInfinity(infinity, player.getInventory().getItem(found.invSlot), event.getProjectileWeaponItemStack(), found.arrowStack);

        ItemStack projectileItemStack = found.arrowStack.copyWithCount(1);
        if (appliesInfinity) {
            projectileItemStack.set(ModDataComponentTypes.pickupType, AbstractArrow.Pickup.DISALLOWED);
        }

        event.setProjectileItemStack(projectileItemStack);

        if (!player.level().isClientSide) {
            CompoundTag tag = new CompoundTag();
            tag.putInt(KEY_INV_SLOT, found.invSlot);
            tag.putInt(KEY_QUIVER_SLOT, found.quiverSlot);
            player.getPersistentData().put(QUIVER_PENDING, tag);
        }
    }

    @SubscribeEvent
    public static void onArrowLoose(ArrowLooseEvent event) {
        Player player = event.getEntity();
        if (player.level().isClientSide) {
            return;
        }

        if (event.getCharge() <= 0) {
            player.getPersistentData().remove(QUIVER_PENDING);
            return;
        }

        CompoundTag tag = player.getPersistentData().getCompound(QUIVER_PENDING);
        if (tag.isEmpty()) {
            return;
        }

        ItemStack quiverStack = player.getInventory().getItem(tag.getInt(KEY_INV_SLOT));
        if (quiverStack.isEmpty() || quiverStack.getItem() != ModItems.quiver) {
            player.getPersistentData().remove(QUIVER_PENDING);
            return;
        }

        QuiverContainer container = QuiverItem.getInventory(quiverStack);
        if (container == null) {
            player.getPersistentData().remove(QUIVER_PENDING);
            return;
        }

        int quiverSlot = tag.getInt(KEY_QUIVER_SLOT);
        ItemStack ammo = container.getItem(quiverSlot);
        if (ammo.isEmpty()) {
            player.getPersistentData().remove(QUIVER_PENDING);
            return;
        }

        Holder<Enchantment> infinity = player.level().registryAccess().holderOrThrow(Enchantments.INFINITY);
        boolean infinityApplies = QuiverEventHandler.appliesInfinity(infinity, quiverStack, event.getBow(), ammo);

        if (!player.isCreative() && !infinityApplies) {
            ammo.shrink(1);
            container.setChanged();
        }

        player.getPersistentData().remove(QUIVER_PENDING);
    }

    @Nullable
    private static FoundArrow findArrowInAnyQuiver(Player player) {
        for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
            FoundArrow arrow = QuiverEventHandler.findInQuiverStack(player.getInventory().getItem(i), i);
            if (arrow != null) {
                return arrow;
            }
        }

        return null;
    }

    @Nullable
    private static FoundArrow findInQuiverStack(ItemStack stack, int invSlot) {
        if (stack.isEmpty() || stack.getItem() != ModItems.quiver) {
            return null;
        }

        QuiverContainer inv = QuiverItem.getInventory(stack);
        if (inv == null) {
            return null;
        }

        for (int i = 0; i < QuiverItem.SLOT_SIZE; i++) {
            ItemStack arrow = inv.getItem(i);
            if (!arrow.isEmpty()) {
                return new FoundArrow(invSlot, i, arrow);
            }
        }

        return null;
    }

    public static boolean appliesInfinity(Holder<Enchantment> infinity, ItemStack quiver, ItemStack bow, ItemStack arrow) {
        return arrow.is(Items.ARROW)
                && (quiver.getEnchantmentLevel(infinity) > 0 || bow.getEnchantmentLevel(infinity) > 0);
    }

    private record FoundArrow(int invSlot, int quiverSlot, ItemStack arrowStack) {}
}
