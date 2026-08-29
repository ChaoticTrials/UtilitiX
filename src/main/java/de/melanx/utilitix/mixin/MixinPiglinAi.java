package de.melanx.utilitix.mixin;

import de.melanx.utilitix.content.gildingarmor.GildingArmorRecipe;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlotGroup;
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
            method = "isWearingSafeArmor",
            at = @At("HEAD"),
            cancellable = true
    )
    private static void utilitix$makesPiglinNeutral(LivingEntity player, CallbackInfoReturnable<Boolean> cir) {
        for (EquipmentSlot slot : EquipmentSlotGroup.ARMOR.slots()) {
            ItemStack stack = player.getItemBySlot(slot);
            if (GildingArmorRecipe.isGilded(stack)) {
                cir.setReturnValue(true);
                return;
            }
        }
    }
}
