package de.melanx.utilitix.content.track.rails;

import de.melanx.utilitix.block.ModProperties;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.state.properties.RailShape;
import org.moddingx.libx.mod.ModX;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class BlockCrossingRail extends BlockRail {

    public final boolean reinforced;

    public BlockCrossingRail(ModX mod, boolean reinforced, Properties properties) {
        super(mod, false, properties);
        this.reinforced = reinforced;
    }

    public BlockCrossingRail(ModX mod, boolean reinforced, Properties properties, Item.Properties itemProperties) {
        super(mod, false, properties, itemProperties);
        this.reinforced = reinforced;
    }

    @Nonnull
    @Override
    public Property<RailShape> getShapeProperty() {
        return ModProperties.RAIL_SHAPE_FLAT_STRAIGHT;
    }

    @Nonnull
    @Override
    public RailShape getRailDirection(@Nonnull BlockState state, @Nonnull BlockGetter level, @Nonnull BlockPos pos, @Nullable AbstractMinecart cart) {
        if (cart != null) {
            switch (Direction.fromYRot(cart.yRot + 90)) {
                case NORTH:
                case SOUTH:
                    return RailShape.NORTH_SOUTH;
                case WEST:
                case EAST:
                    return RailShape.EAST_WEST;
            }
        }
        return super.getRailDirection(state, level, pos, cart);
    }

    @Override
    public float getRailMaxSpeed(BlockState state, Level level, BlockPos pos, AbstractMinecart cart) {
        return this.reinforced ? 0.7f : 0.4f;
    }
}
