package de.melanx.utilitix.mixin;

import net.minecraft.world.entity.decoration.ArmorStand;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(ArmorStand.class)
public class MixinArmorStand {

//    @Inject( todo
//            method = "brokenByPlayer",
//            at = @At("HEAD"),
//            cancellable = true
//    )
//    private void breakArmorStand(DamageSource source, CallbackInfo ci) {
//        if (((ArmorStand) (Object) this).getPersistentData().getBoolean("UtilitiXArmorStand")) {
//            Block.popResource(((ArmorStand) (Object) this).level(), ((ArmorStand) (Object) this).blockPosition(), new ItemStack(ModItems.armedStand));
//            ((ArmorStand) (Object) this).brokenByAnything(source);
//            ci.cancel();
//        }
//    }
}
