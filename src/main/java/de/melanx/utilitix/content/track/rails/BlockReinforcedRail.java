package de.melanx.utilitix.content.track.rails;

import de.melanx.utilitix.block.ModProperties;
import io.github.noeppi_noeppi.libx.mod.ModX;
import net.minecraft.item.Item;
import net.minecraft.state.Property;
import net.minecraft.state.properties.RailShape;

import javax.annotation.Nonnull;

public class BlockReinforcedRail extends BlockRail {

    public BlockReinforcedRail(ModX mod, Properties properties) {
        super(mod, true, properties);
    }

    public BlockReinforcedRail(ModX mod, Properties properties, Item.Properties itemProperties) {
        super(mod, true, properties, itemProperties);
    }

    @Nonnull
    @Override
    public Property<RailShape> getShapeProperty() {
        return ModProperties.RAIL_SHAPE_FLAT;
    }
}
