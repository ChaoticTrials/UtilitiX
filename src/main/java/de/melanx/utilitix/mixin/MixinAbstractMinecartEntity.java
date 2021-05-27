package de.melanx.utilitix.mixin;

import de.melanx.utilitix.content.rails.ItemMinecartTinkerer;
import net.minecraft.entity.item.minecart.AbstractMinecartEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractMinecartEntity.class)
public class MixinAbstractMinecartEntity {

    @Inject(
            method = "Lnet/minecraft/entity/item/minecart/AbstractMinecartEntity;killMinecart(Lnet/minecraft/util/DamageSource;)V",
            at = @At("RETURN")
    )
    public void killMinecart(DamageSource source, CallbackInfo ci) {
        ItemStack stack = ItemMinecartTinkerer.getLabelStack(((AbstractMinecartEntity) (Object) this));
        if (!stack.isEmpty()) {
            ((AbstractMinecartEntity) (Object) this).entityDropItem(stack);
        }
    }
}
