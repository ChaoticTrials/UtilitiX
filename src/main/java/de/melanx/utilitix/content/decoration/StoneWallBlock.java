package de.melanx.utilitix.content.decoration;

import de.melanx.utilitix.config.FeatureConfig;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.WallBlock;
import org.moddingx.libx.registration.Registerable;
import org.moddingx.libx.registration.RegistrationContext;

import javax.annotation.Nonnull;

public class StoneWallBlock extends WallBlock implements Registerable {

    private final BlockItem item;

    public StoneWallBlock(Properties properties, Item.Properties itemProperties) {
        super(properties);
        this.item = new BlockItem(this, itemProperties) {

            @Override
            public boolean isEnabled(@Nonnull FeatureFlagSet enabledFeatures) {
                return StoneWallBlock.this.isEnabled(enabledFeatures);
            }
        };
    }

    @Override
    public void registerAdditional(RegistrationContext ctx, EntryCollector builder) {
        builder.register(Registries.ITEM, this.item);
    }

    @Override
    public boolean isEnabled(@Nonnull FeatureFlagSet enabledFeatures) {
        return FeatureConfig.Misc.decoration;
    }
}
