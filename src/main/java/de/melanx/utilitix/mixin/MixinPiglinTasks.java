package de.melanx.utilitix.mixin;

import de.melanx.utilitix.content.gildingarmor.GildingArmorRecipe;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.monster.piglin.PiglinTasks;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PiglinTasks.class)
public abstract class MixinPiglinTasks {

    @Inject(method = "func_234460_a_(Lnet/minecraft/entity/LivingEntity;)Z", at = @At("RETURN"), cancellable = true)
    private static void makesPiglinNeutral(LivingEntity player, CallbackInfoReturnable<Boolean> cir) {
        for (ItemStack stack : player.getArmorInventoryList()) {
            if (GildingArmorRecipe.isGilded(stack)) {
                cir.setReturnValue(true);
            }
        }
    }
}
