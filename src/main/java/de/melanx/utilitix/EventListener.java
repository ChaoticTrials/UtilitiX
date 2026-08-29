package de.melanx.utilitix;

import de.melanx.utilitix.config.CommonConfig;
import de.melanx.utilitix.content.MobYoinkerItem;
import de.melanx.utilitix.content.brewery.AdvancedBreweryBlockEntity;
import de.melanx.utilitix.content.crudefurnace.CrudeFurnaceBlockEntity;
import de.melanx.utilitix.content.experiencecrystal.ExperienceCrystalBlockEntity;
import de.melanx.utilitix.registration.ModBlocks;
import de.melanx.utilitix.registration.ModItems;
import de.melanx.utilitix.util.MobUtil;
import de.melanx.utilitix.util.XPUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

@EventBusSubscriber(modid = "utilitix")
public class EventListener {

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onEntityInteract(PlayerInteractEvent.EntityInteract event) {
        Player player = event.getEntity();

        if (!player.isShiftKeyDown() || !(event.getTarget() instanceof LivingEntity target)) {
            return;
        }

        InteractionHand hand = event.getHand();
        ItemStack stack = player.getItemInHand(hand);
        if (stack.getItem() == ModItems.mobBell) {
            if (MobUtil.storeEntityData(player, hand, target, CommonConfig.HandBells.mobBellEntities, true)) {
                event.setCancellationResult(InteractionResult.SUCCESS);
                event.setCanceled(true);
            }

            return;
        }

        if (stack.getItem() == ModItems.mobYoinker) {
            if (!player.isCreative()) {
                int xp = player.experienceLevel;
                if (CommonConfig.MobYoinker.experienceMode == MobYoinkerItem.ExperienceMode.POINTS) {
                    xp = XPUtils.getExpPoints(player.experienceLevel, player.experienceProgress);
                }

                int health = (int) target.getHealth();
                int diff = xp - health;
                if (diff < 0) {
                    player.sendOverlayMessage(Component.translatable("message.utilitix.mob_yoinker", -diff, CommonConfig.MobYoinker.experienceMode == MobYoinkerItem.ExperienceMode.LEVEL ? Component.translatable("levels") : Component.translatable("points")));
                    return;
                }
            }

            if (MobUtil.storeEntityData(player, hand, target, CommonConfig.MobYoinker.mobYoinkerEntities, false)) {
                if (CommonConfig.MobYoinker.experienceMode == MobYoinkerItem.ExperienceMode.LEVEL) {
                    player.giveExperienceLevels((int) -target.getHealth());
                } else {
                    player.giveExperiencePoints((int) -target.getHealth());
                }
                target.remove(Entity.RemovalReason.DISCARDED);

                event.setCancellationResult(InteractionResult.SUCCESS);
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(Capabilities.Item.BLOCK, ModBlocks.advancedBrewery.getBlockEntityType(), AdvancedBreweryBlockEntity::getCapability);
        event.registerBlockEntity(Capabilities.Item.BLOCK, ModBlocks.crudeFurnace.getBlockEntityType(), CrudeFurnaceBlockEntity::getCapability);
        event.registerBlockEntity(Capabilities.Fluid.BLOCK, ModBlocks.experienceCrystal.getBlockEntityType(), ExperienceCrystalBlockEntity::getCapability);
    }
}
