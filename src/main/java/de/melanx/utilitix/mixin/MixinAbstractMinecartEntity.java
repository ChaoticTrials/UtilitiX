//package de.melanx.utilitix.mixin;
//
//import de.melanx.utilitix.content.track.ItemMinecartTinkerer;
//import net.minecraft.world.entity.vehicle.AbstractMinecart;
//import net.minecraft.world.item.ItemStack;
//import net.minecraft.world.damagesource.DamageSource;
//import org.spongepowered.asm.mixin.Mixin;
//import org.spongepowered.asm.mixin.injection.At;
//import org.spongepowered.asm.mixin.injection.Inject;
//import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
//
//@Mixin(AbstractMinecart.class)
//public class MixinAbstractMinecartEntity {
//
//    @Inject(
//            method = "Lnet/minecraft/entity/item/minecart/AbstractMinecartEntity;killMinecart(Lnet/minecraft/util/DamageSource;)V",
//            at = @At("RETURN")
//    )
//    public void killMinecart(DamageSource source, CallbackInfo ci) {
//        ItemStack stack = ItemMinecartTinkerer.getLabelStack(((AbstractMinecart) (Object) this));
//        if (!stack.isEmpty()) {
//            ((AbstractMinecart) (Object) this).spawnAtLocation(stack);
//        }
//    }
//}
