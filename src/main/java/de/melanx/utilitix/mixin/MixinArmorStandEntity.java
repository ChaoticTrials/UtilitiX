package de.melanx.utilitix.mixin;

import de.melanx.utilitix.registration.ModItems;
import net.minecraft.block.Block;
import net.minecraft.entity.item.ArmorStandEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ArmorStandEntity.class)
public class MixinArmorStandEntity {

    @Inject(
            method = "Lnet/minecraft/entity/item/ArmorStandEntity;breakArmorStand(Lnet/minecraft/util/DamageSource;)V",
            at = @At("HEAD"),
            cancellable = true
    )
    private void breakArmorStand(DamageSource source, CallbackInfo ci) {
        if (((ArmorStandEntity) (Object) this).getPersistentData().getBoolean("UtilitiXArmorStand")) {
            Block.spawnAsEntity(((ArmorStandEntity) (Object) this).world, ((ArmorStandEntity) (Object) this).getPosition(), new ItemStack(ModItems.armedStand));
            ((ArmorStandEntity) (Object) this).handleArmorStandBreak(source);
            ci.cancel();
        }
    }
}
