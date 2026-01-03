package de.melanx.utilitix.content.gildingarmor;

import de.melanx.utilitix.config.FeatureConfig;
import net.minecraft.world.flag.FeatureFlagSet;
import org.moddingx.libx.base.ItemBase;
import org.moddingx.libx.mod.ModX;

import javax.annotation.Nonnull;

public class GildingCrystalItem extends ItemBase {

    public GildingCrystalItem(ModX mod, Properties properties) {
        super(mod, properties);
    }

    @Override
    public boolean isEnabled(@Nonnull FeatureFlagSet enabledFeatures) {
        return FeatureConfig.Misc.InWorldChanges.gilding;
    }
}
