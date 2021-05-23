package de.melanx.utilitix;

import de.melanx.utilitix.item.bells.MobBell;
import de.melanx.utilitix.registration.ModItems;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ArmorStandEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Rotations;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class EventListener {

    private static final IFormattableTextComponent BLACKLISTED_MOB = new TranslationTextComponent("tooltip." + UtilitiX.getInstance().modid + ".blacklisted_mob").mergeStyle(TextFormatting.DARK_RED);

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onEntityInteract(PlayerInteractEvent.EntityInteract event) {
        PlayerEntity player = event.getPlayer();

        if (player.isSneaking() && player.getHeldItem(event.getHand()).getItem() == ModItems.mobBell && event.getTarget() instanceof LivingEntity) {
            LivingEntity target = (LivingEntity) event.getTarget();
            Hand hand = event.getHand();
            ItemStack stack = player.getHeldItem(hand);
            ResourceLocation entityKey = EntityType.getKey(target.getType());
            if (entityKey.toString().equals(stack.getOrCreateTag().getString("Entity"))) {
                return;
            }

            if (UtilitiXConfig.HandBells.blacklist.contains(entityKey)) {
                player.sendStatusMessage(BLACKLISTED_MOB, true);
                return;
            }

            stack.getOrCreateTag().putString("Entity", entityKey.toString());
            player.setHeldItem(hand, stack);
            player.sendStatusMessage(MobBell.getCurrentMob(target.getType()), true);
            event.setCancellationResult(ActionResultType.SUCCESS);
            event.setCanceled(true);
        }
    }

    // TODO wait for https://github.com/MinecraftForge/MinecraftForge/pull/7715
//    @SubscribeEvent
//    public void onBowFindAmmo(PlayerFindProjectileEvent event) {
//        if (event.getFoundAmmo().isEmpty()) {
//            PlayerEntity player = event.getPlayer();
//            Stream.concat(Stream.of(player.getHeldItemOffhand()), player.inventory.mainInventory.stream())
//                    .filter(stack -> stack.getItem() == ModItems.quiver)
//                    .filter(stack -> !Quiver.isEmpty(stack))
//                    .findFirst()
//                    .ifPresent(stack -> {
//                        IItemHandlerModifiable inventory = Quiver.getInventory(stack);
//                        assert inventory != null;
//                        int enchantmentLevel = EnchantmentHelper.getEnchantmentLevel(Enchantments.INFINITY, stack);
//                        if (enchantmentLevel >= 1) {
//                            for (int i = 0; i < inventory.getSlots(); i++) {
//                                ItemStack arrow = inventory.getStackInSlot(i);
//                                if (!arrow.isEmpty()) {
//                                    event.setAmmo(arrow.copy());
//                                    return;
//                                }
//                            }
//                        } else {
//                            for (int i = 0; i < inventory.getSlots(); i++) {
//                                ItemStack arrow = inventory.getStackInSlot(i);
//                                if (!arrow.isEmpty()) {
//                                    arrow = player.isCreative() ? arrow.copy() : arrow;
//                                    event.setAmmo(arrow);
//                                    return;
//                                }
//                            }
//                        }
//                    });
//        }
//    }
    
    @SubscribeEvent
    public void entityInteract(PlayerInteractEvent.EntityInteractSpecific event) {
        if (event.getTarget() instanceof ArmorStandEntity && event.getTarget().getPersistentData().getBoolean("UtilitiXArmorStand")) {
            if (event.getItemStack().getItem() == Items.FLINT && event.getPlayer().isSneaking()) {
                ArmorStandEntity entity = (ArmorStandEntity) event.getTarget();
                if (UtilitiXConfig.armorStandPoses.size() >= 2) {
                    int newIdx = (entity.getPersistentData().getInt("UtilitiXPoseIdx") + 1) % UtilitiXConfig.armorStandPoses.size();
                    entity.getPersistentData().putInt("UtilitiXPoseIdx", newIdx);
                    UtilitiXConfig.armorStandPoses.get(newIdx).apply(entity);
                }
                event.setCanceled(true);
                event.setCancellationResult(ActionResultType.SUCCESS);
            }
        }
    }
}
