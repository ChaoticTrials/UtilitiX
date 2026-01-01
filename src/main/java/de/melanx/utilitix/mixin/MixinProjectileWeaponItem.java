package de.melanx.utilitix.mixin;

import de.melanx.utilitix.registration.ModDataComponentTypes;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ProjectileWeaponItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ProjectileWeaponItem.class)
public class MixinProjectileWeaponItem {

    @Inject(method = "customArrow", at = @At("RETURN"))
    public void lol(AbstractArrow arrow, ItemStack projectileStack, ItemStack weaponStack, CallbackInfoReturnable<AbstractArrow> cir) {
        arrow.pickup = projectileStack.getOrDefault(ModDataComponentTypes.pickupType, arrow.pickup);
    }
}
