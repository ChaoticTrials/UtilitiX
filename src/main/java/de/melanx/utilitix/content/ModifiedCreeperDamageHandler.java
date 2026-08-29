package de.melanx.utilitix.content;

import de.melanx.utilitix.config.FeatureConfig;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.level.ServerExplosion;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.level.ExplosionEvent;

@EventBusSubscriber
public class ModifiedCreeperDamageHandler {

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onExplosionStart(ExplosionEvent.Start event) {
        if (event.isCanceled() || !FeatureConfig.Misc.InWorldChanges.dynamicCreeperExplosions) {
            return;
        }

        ServerExplosion explosion = event.getExplosion();

        if (explosion.getDirectSourceEntity() instanceof Creeper creeper) {
            float health = creeper.getHealth();
            float maxHealth = creeper.getMaxHealth();

            explosion.radius = explosion.radius * (health / maxHealth);
        }
    }
}
