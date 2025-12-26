package de.melanx.utilitix.content;

import de.melanx.utilitix.config.FeatureConfig;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.item.ShearsItem;

import javax.annotation.Nonnull;

public class ItemDiamondShears extends ShearsItem {

    public ItemDiamondShears(Properties properties) {
        super(properties);
    }

    @Override
    public boolean isEnabled(@Nonnull FeatureFlagSet enabledFeatures) {
        return FeatureConfig.Items.diamondShears;
    }
}
