package de.melanx.utilitix.content.track.rails;

import de.melanx.utilitix.block.ModProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.state.properties.RailShape;
import org.moddingx.libx.mod.ModX;

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
