package de.melanx.utilitix.mixin;

import de.melanx.utilitix.config.CommonConfig;
import de.melanx.utilitix.config.FeatureConfig;
import net.minecraft.world.entity.npc.villager.AbstractVillager;
import net.minecraft.world.entity.npc.wanderingtrader.WanderingTrader;
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
    public void utilitix$notifyTrade2(MerchantOffer offer, CallbackInfo ci) {
        if (!FeatureConfig.Misc.InWorldChanges.wanderingTrader) {
            return;
        }

        if (((AbstractVillager) (Object) this) instanceof WanderingTrader trader) {
            trader.setDespawnDelay(trader.getDespawnDelay() + CommonConfig.wanderingTraderExtraTime);
        }
    }
}
