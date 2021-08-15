//package de.melanx.utilitix.mixin;
//
//import de.melanx.utilitix.registration.ModItems;
//import net.minecraft.world.level.block.Block;
//import net.minecraft.world.entity.decoration.ArmorStand;
//import net.minecraft.world.item.ItemStack;
//import net.minecraft.world.damagesource.DamageSource;
//import org.spongepowered.asm.mixin.Mixin;
//import org.spongepowered.asm.mixin.injection.At;
//import org.spongepowered.asm.mixin.injection.Inject;
//import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
//
//@Mixin(ArmorStand.class)
//public class MixinArmorStandEntity {
//
//    @Inject(
//            method = "Lnet/minecraft/entity/item/ArmorStandEntity;breakArmorStand(Lnet/minecraft/util/DamageSource;)V",
//            at = @At("HEAD"),
//            cancellable = true
//    )
//    private void breakArmorStand(DamageSource source, CallbackInfo ci) {
//        if (((ArmorStand) (Object) this).getPersistentData().getBoolean("UtilitiXArmorStand")) {
//            Block.popResource(((ArmorStand) (Object) this).level, ((ArmorStand) (Object) this).blockPosition(), new ItemStack(ModItems.armedStand));
//            ((ArmorStand) (Object) this).brokenByAnything(source);
//            ci.cancel();
//        }
//    }
//}
