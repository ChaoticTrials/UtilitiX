package de.melanx.utilitix.block;

import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.RailShape;

public class ModProperties {
    
    public static final EnumProperty<RailShape> RAIL_SHAPE_FLAT = EnumProperty.create("shape", RailShape.class, (shape) -> !shape.isAscending());
    public static final EnumProperty<RailShape> RAIL_SHAPE_FLAT_STRAIGHT = EnumProperty.create("shape", RailShape.class, (shape) -> shape == RailShape.NORTH_SOUTH || shape == RailShape.EAST_WEST);
    public static final BooleanProperty REVERSE = BooleanProperty.create("reverse");
    public static final BooleanProperty RAIL_SIDE = BooleanProperty.create("rail_side");
}
