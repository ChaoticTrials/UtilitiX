package de.melanx.utilitix.mixin;

import de.melanx.utilitix.registration.ModItems;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ArmorStand.class)
public class MixinArmorStand {

    @Inject(
            method = "brokenByPlayer",
            at = @At("HEAD"),
            cancellable = true
    )
    private void breakArmorStand(ServerLevel level, DamageSource damageSource, CallbackInfo ci) {
        ArmorStand armorStand = (ArmorStand) (Object) this;
        if (armorStand.getPersistentData().getBoolean("UtilitiXArmorStand")) {
            Block.popResource(armorStand.level(), armorStand.blockPosition(), new ItemStack(ModItems.armedStand));
            armorStand.brokenByAnything(level, damageSource);
            ci.cancel();
        }
    }
}
