package de.melanx.utilitix.block;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.RailShape;
import net.minecraft.world.level.material.Fluid;

public class ModProperties {

    public static final TagKey<Fluid> XP_FLUID_TAG = TagKey.create(Registry.FLUID_REGISTRY, new ResourceLocation("experience"));

    public static final EnumProperty<RailShape> RAIL_SHAPE_FLAT = EnumProperty.create("shape", RailShape.class, (shape) -> !shape.isAscending());
    public static final EnumProperty<RailShape> RAIL_SHAPE_FLAT_STRAIGHT = EnumProperty.create("shape", RailShape.class, (shape) -> shape == RailShape.NORTH_SOUTH || shape == RailShape.EAST_WEST);
    public static final BooleanProperty REVERSE = BooleanProperty.create("reverse");
    public static final BooleanProperty RAIL_SIDE = BooleanProperty.create("rail_side");
}
