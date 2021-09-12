package de.melanx.utilitix.mixin;

import de.melanx.utilitix.content.gildingarmor.GildingArmorRecipe;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.piglin.PiglinAi;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PiglinAi.class)
public abstract class MixinPiglinAi {

    @Inject(
            method = "isWearingGold",
            at = @At("HEAD"),
            cancellable = true
    )
    private static void makesPiglinNeutral(LivingEntity player, CallbackInfoReturnable<Boolean> cir) {
        for (ItemStack stack : player.getArmorSlots()) {
            if (GildingArmorRecipe.isGilded(stack)) {
                cir.setReturnValue(true);
            }
        }
    }
}
