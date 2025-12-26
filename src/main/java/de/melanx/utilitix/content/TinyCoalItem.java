package de.melanx.utilitix.content;

import de.melanx.utilitix.config.FeatureConfig;
import de.melanx.utilitix.item.BurnableItemBase;
import net.minecraft.world.flag.FeatureFlagSet;
import org.moddingx.libx.mod.ModX;

import javax.annotation.Nonnull;

public class TinyCoalItem extends BurnableItemBase {

    public TinyCoalItem(ModX mod, Properties properties, int burnTime) {
        super(mod, properties, burnTime);
    }

    @Override
    public boolean isEnabled(@Nonnull FeatureFlagSet enabledFeatures) {
        return FeatureConfig.Items.tinyCoal;
    }
}
