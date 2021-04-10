package de.melanx.utilitix;

import de.melanx.utilitix.item.Quiver;
import de.melanx.utilitix.registration.ModItems;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.ArrowNockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.items.IItemHandlerModifiable;

import java.util.stream.Stream;

public class EventListener {

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
