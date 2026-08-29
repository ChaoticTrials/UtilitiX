package de.melanx.utilitix.mixin;

import de.melanx.utilitix.content.track.TrackUtil;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.vehicle.minecart.MinecartBehavior;
import net.minecraft.world.entity.vehicle.minecart.NewMinecartBehavior;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(NewMinecartBehavior.class)
public abstract class MixinNewMinecartBehavior {

    @Inject(method = "getMaxSpeed", at = @At("RETURN"), cancellable = true)
    private void utilitix$raiseRailSpeedCap(ServerLevel level, CallbackInfoReturnable<Double> cir) {
        double railSpeed = TrackUtil.railMaxSpeed(((MinecartBehavior) (Object) this).minecart);

        if (railSpeed > cir.getReturnValueD()) {
            cir.setReturnValue(railSpeed);
        }
    }
}
