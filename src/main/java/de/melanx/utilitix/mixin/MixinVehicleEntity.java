package de.melanx.utilitix.mixin;

import de.melanx.utilitix.content.track.ItemMinecartTinkerer;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.entity.vehicle.VehicleEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(VehicleEntity.class)
public class MixinVehicleEntity {

    @Inject(
            method = "destroy(Lnet/minecraft/world/item/Item;)V",
            at = @At("RETURN")
    )
    public void killMinecart(Item dropItem, CallbackInfo ci) {
        if ((Object) this instanceof AbstractMinecart minecart) {
            ItemStack stack = ItemMinecartTinkerer.getLabelStack(minecart);
            if (!stack.isEmpty()) {
                minecart.spawnAtLocation(stack);
            }
        }
    }
}
