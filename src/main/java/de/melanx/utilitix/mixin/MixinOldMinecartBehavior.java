package de.melanx.utilitix.mixin;

import de.melanx.utilitix.content.track.TrackUtil;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.vehicle.minecart.MinecartBehavior;
import net.minecraft.world.entity.vehicle.minecart.OldMinecartBehavior;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(OldMinecartBehavior.class)
public abstract class MixinOldMinecartBehavior {

    @Inject(method = "getMaxSpeed", at = @At("RETURN"), cancellable = true)
    private void utilitix$raiseRailSpeedCap(ServerLevel level, CallbackInfoReturnable<Double> cir) {
        double railSpeed = TrackUtil.railMaxSpeed(((MinecartBehavior) (Object) this).minecart);

        if (railSpeed > cir.getReturnValueD()) {
            cir.setReturnValue(railSpeed);
        }
    }

    @Inject(method = "getKnownMovement", at = @At("HEAD"), cancellable = true)
    private void utilitix$raiseKnownMovementCap(Vec3 knownMovement, CallbackInfoReturnable<Vec3> cir) {
        double railSpeed = TrackUtil.railMaxSpeed(((MinecartBehavior) (Object) this).minecart);

        if (railSpeed > 0.4D && !Double.isNaN(knownMovement.x) && !Double.isNaN(knownMovement.y) && !Double.isNaN(knownMovement.z)) {
            cir.setReturnValue(new Vec3(
                    Mth.clamp(knownMovement.x, -railSpeed, railSpeed),
                    knownMovement.y,
                    Mth.clamp(knownMovement.z, -railSpeed, railSpeed)));
        }
    }
}
