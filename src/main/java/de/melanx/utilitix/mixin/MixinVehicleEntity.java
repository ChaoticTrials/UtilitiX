package de.melanx.utilitix.mixin;

import de.melanx.utilitix.content.track.MinecartTinkererItem;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.vehicle.VehicleEntity;
import net.minecraft.world.entity.vehicle.minecart.AbstractMinecart;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(VehicleEntity.class)
public class MixinVehicleEntity {

    @Inject(
            method = "destroy(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/item/Item;)V",
            at = @At("RETURN")
    )
    public void utilitix$killMinecart(ServerLevel level, Item dropItem, CallbackInfo ci) {
        if ((Object) this instanceof AbstractMinecart minecart) {
            ItemStack stack = MinecartTinkererItem.getLabelStack(minecart);
            if (!stack.isEmpty()) {
                minecart.spawnAtLocation(level, stack);
            }
        }
    }
}
