package de.melanx.utilitix.registration;

import com.google.common.collect.ImmutableSet;
import de.melanx.utilitix.config.FeatureConfig;
import de.melanx.utilitix.content.shulkerboat.ShulkerBoat;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.flag.FeatureFlags;
import org.moddingx.libx.annotation.registration.RegisterClass;

import javax.annotation.Nonnull;

@RegisterClass(registry = "ENTITY_TYPE")
public class ModEntities {

    public static final EntityType<ShulkerBoat> shulkerBoat = new EntityType<>(ShulkerBoat::new, MobCategory.MISC, true, true, false, false, ImmutableSet.of(), EntityDimensions.scalable(1.375F, 0.5625F), 1.0F, 10, 3, FeatureFlags.VANILLA_SET) {
        @Override
        public boolean isEnabled(@Nonnull FeatureFlagSet enabledFeatures) {
            return FeatureConfig.Transportation.shulkerBoats;
        }
    };
}
