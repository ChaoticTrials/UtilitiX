package de.melanx.utilitix.mixin;

import de.melanx.utilitix.UtilitiXConfig;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.npc.WanderingTrader;
import net.minecraft.world.item.trading.MerchantOffer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractVillager.class)
public class MixinAbstractVillager {

    @Inject(
            method = "notifyTrade",
            at = @At("TAIL")
    )
    public void notifyTrade2(MerchantOffer offer, CallbackInfo ci) {
        if (((AbstractVillager) (Object) this) instanceof WanderingTrader trader) {
            trader.setDespawnDelay(trader.getDespawnDelay() + UtilitiXConfig.wanderingTraderExtraTime);
        }
    }
}
