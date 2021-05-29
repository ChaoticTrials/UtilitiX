package de.melanx.utilitix.content.track.rails;

import de.melanx.utilitix.block.ModProperties;
import io.github.noeppi_noeppi.libx.mod.ModX;
import net.minecraft.block.BlockState;
import net.minecraft.entity.item.minecart.AbstractMinecartEntity;
import net.minecraft.item.Item;
import net.minecraft.state.Property;
import net.minecraft.state.properties.RailShape;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

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
    public RailShape getRailDirection(@Nonnull BlockState state, @Nonnull IBlockReader world, @Nonnull BlockPos pos, @Nullable AbstractMinecartEntity cart) {
        if (cart != null) {
            switch (Direction.fromAngle(cart.rotationYaw + 90)) {
                case NORTH:
                case SOUTH:
                    return RailShape.NORTH_SOUTH;
                case WEST:
                case EAST:
                    return RailShape.EAST_WEST;
            }
        }
        return super.getRailDirection(state, world, pos, cart);
    }

    @Override
    public float getRailMaxSpeed(BlockState state, World world, BlockPos pos, AbstractMinecartEntity cart) {
        return this.reinforced ? 0.7f : 0.4f;
    }
}
