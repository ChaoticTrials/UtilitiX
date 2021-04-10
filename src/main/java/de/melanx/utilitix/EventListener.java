package de.melanx.utilitix;

import de.melanx.utilitix.item.Quiver;
import de.melanx.utilitix.item.bells.MobBell;
import de.melanx.utilitix.registration.ModItems;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.ArrowNockEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.items.IItemHandlerModifiable;

import java.util.stream.Stream;

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

    @SubscribeEvent
    public void onBowMimimi(ArrowNockEvent event) {
        World world = event.getWorld();

        if (!event.hasAmmo()) {
            PlayerEntity player = event.getPlayer();
            Stream.concat(Stream.of(player.getHeldItemOffhand()), player.inventory.mainInventory.stream())
                    .filter(stack -> stack.getItem() == ModItems.quiver)
                    .filter(stack -> !Quiver.isEmpty(stack))
                    .findFirst()
                    .ifPresent(stack -> {
                        IItemHandlerModifiable inventory = Quiver.getInventory(stack);
                        assert inventory != null;
                        int enchantmentLevel = EnchantmentHelper.getEnchantmentLevel(Enchantments.INFINITY, stack);
                        if (enchantmentLevel >= 1) {
                            event.setAction(ActionResult.resultConsume(event.getBow()));
                        } else {
                            for (int i = 0; i < inventory.getSlots(); i++) {
                                if (inventory.extractItem(i, 1, player.isCreative()).isEmpty()) {
                                    event.setAction(ActionResult.resultConsume(event.getBow()));
                                    return;
                                }
                            }
                        }
                    });
        }
    }
}
