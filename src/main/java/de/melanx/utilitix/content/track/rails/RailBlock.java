package de.melanx.utilitix.content.track.rails;

import com.google.common.collect.ImmutableList;
import de.melanx.utilitix.config.FeatureConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.BaseRailBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.state.properties.RailShape;
import org.moddingx.libx.mod.ModX;
import org.moddingx.libx.registration.Registerable;
import org.moddingx.libx.registration.RegistrationContext;

import javax.annotation.Nonnull;

public abstract class RailBlock extends BaseRailBlock implements Registerable {

    protected final ModX mod;
    private final Item item;
    private final boolean hasCorners;
    private final boolean hasSlopes;

    public RailBlock(ModX mod, boolean corners, Properties properties) {
        this(mod, corners, properties, new Item.Properties());
    }

    public RailBlock(ModX mod, boolean corners, Properties properties, Item.Properties itemProperties) {
        super(!corners, properties);
        this.mod = mod;
        this.item = new BlockItem(this, itemProperties) {

            @Override
            public boolean isEnabled(@Nonnull FeatureFlagSet enabledFeatures) {
                return RailBlock.this.isEnabled(enabledFeatures);
            }
        };
        this.hasCorners = this.getShapeProperty().getPossibleValues().containsAll(ImmutableList.of(RailShape.NORTH_EAST, RailShape.NORTH_WEST, RailShape.SOUTH_EAST, RailShape.SOUTH_WEST));
        this.hasSlopes = this.getShapeProperty().getPossibleValues().containsAll(ImmutableList.of(RailShape.ASCENDING_NORTH, RailShape.ASCENDING_SOUTH, RailShape.ASCENDING_EAST, RailShape.ASCENDING_WEST));
        this.registerDefaultState(this.defaultBlockState()
                .setValue(BlockStateProperties.WATERLOGGED, false));
    }

    @Override
    public void registerAdditional(RegistrationContext ctx, EntryCollector builder) {
        builder.register(Registries.ITEM, this.item);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(this.getShapeProperty());
        builder.add(BlockStateProperties.WATERLOGGED);
    }
    
    @Nonnull
    @Override
    public abstract Property<RailShape> getShapeProperty();

    @Nonnull
    @Override
    public BlockState rotate(@Nonnull BlockState state, @Nonnull Rotation rotation) {
        return switch (rotation) {
            case CLOCKWISE_180 -> switch (state.getValue(this.getShapeProperty())) {
                case ASCENDING_EAST -> state.setValue(this.getShapeProperty(), RailShape.ASCENDING_WEST);
                case ASCENDING_WEST -> state.setValue(this.getShapeProperty(), RailShape.ASCENDING_EAST);
                case ASCENDING_NORTH -> state.setValue(this.getShapeProperty(), RailShape.ASCENDING_SOUTH);
                case ASCENDING_SOUTH -> state.setValue(this.getShapeProperty(), RailShape.ASCENDING_NORTH);
                case SOUTH_EAST -> state.setValue(this.getShapeProperty(), RailShape.NORTH_WEST);
                case SOUTH_WEST -> state.setValue(this.getShapeProperty(), RailShape.NORTH_EAST);
                case NORTH_WEST -> state.setValue(this.getShapeProperty(), RailShape.SOUTH_EAST);
                case NORTH_EAST -> state.setValue(this.getShapeProperty(), RailShape.SOUTH_WEST);
                case NORTH_SOUTH, EAST_WEST -> state; //Forge fix: MC-196102
            };
            case COUNTERCLOCKWISE_90 -> switch (state.getValue(this.getShapeProperty())) {
                case NORTH_SOUTH -> state.setValue(this.getShapeProperty(), RailShape.EAST_WEST);
                case EAST_WEST -> state.setValue(this.getShapeProperty(), RailShape.NORTH_SOUTH);
                case ASCENDING_EAST -> state.setValue(this.getShapeProperty(), RailShape.ASCENDING_NORTH);
                case ASCENDING_WEST -> state.setValue(this.getShapeProperty(), RailShape.ASCENDING_SOUTH);
                case ASCENDING_NORTH -> state.setValue(this.getShapeProperty(), RailShape.ASCENDING_WEST);
                case ASCENDING_SOUTH -> state.setValue(this.getShapeProperty(), RailShape.ASCENDING_EAST);
                case SOUTH_EAST -> state.setValue(this.getShapeProperty(), RailShape.NORTH_EAST);
                case SOUTH_WEST -> state.setValue(this.getShapeProperty(), RailShape.SOUTH_EAST);
                case NORTH_WEST -> state.setValue(this.getShapeProperty(), RailShape.SOUTH_WEST);
                case NORTH_EAST -> state.setValue(this.getShapeProperty(), RailShape.NORTH_WEST);
            };
            case CLOCKWISE_90 -> switch (state.getValue(this.getShapeProperty())) {
                case NORTH_SOUTH -> state.setValue(this.getShapeProperty(), RailShape.EAST_WEST);
                case EAST_WEST -> state.setValue(this.getShapeProperty(), RailShape.NORTH_SOUTH);
                case ASCENDING_EAST -> state.setValue(this.getShapeProperty(), RailShape.ASCENDING_SOUTH);
                case ASCENDING_WEST -> state.setValue(this.getShapeProperty(), RailShape.ASCENDING_NORTH);
                case ASCENDING_NORTH -> state.setValue(this.getShapeProperty(), RailShape.ASCENDING_EAST);
                case ASCENDING_SOUTH -> state.setValue(this.getShapeProperty(), RailShape.ASCENDING_WEST);
                case SOUTH_EAST -> state.setValue(this.getShapeProperty(), RailShape.SOUTH_WEST);
                case SOUTH_WEST -> state.setValue(this.getShapeProperty(), RailShape.NORTH_WEST);
                case NORTH_WEST -> state.setValue(this.getShapeProperty(), RailShape.NORTH_EAST);
                case NORTH_EAST -> state.setValue(this.getShapeProperty(), RailShape.SOUTH_EAST);
            };
            default -> state;
        };
    }

    @Nonnull
    @Override
    public BlockState mirror(BlockState state, Mirror mirror) {
        RailShape railshape = state.getValue(this.getShapeProperty());
        switch (mirror) {
            case LEFT_RIGHT -> {
                return switch (railshape) {
                    case ASCENDING_NORTH -> state.setValue(this.getShapeProperty(), RailShape.ASCENDING_SOUTH);
                    case ASCENDING_SOUTH -> state.setValue(this.getShapeProperty(), RailShape.ASCENDING_NORTH);
                    case SOUTH_EAST -> state.setValue(this.getShapeProperty(), RailShape.NORTH_EAST);
                    case SOUTH_WEST -> state.setValue(this.getShapeProperty(), RailShape.NORTH_WEST);
                    case NORTH_WEST -> state.setValue(this.getShapeProperty(), RailShape.SOUTH_WEST);
                    case NORTH_EAST -> state.setValue(this.getShapeProperty(), RailShape.SOUTH_EAST);
                    default -> super.mirror(state, mirror);
                };
            }
            case FRONT_BACK -> {
                switch (railshape) {
                    case ASCENDING_EAST -> state.setValue(this.getShapeProperty(), RailShape.ASCENDING_WEST);
                    case ASCENDING_WEST -> state.setValue(this.getShapeProperty(), RailShape.ASCENDING_EAST);
                    case SOUTH_EAST -> state.setValue(this.getShapeProperty(), RailShape.SOUTH_WEST);
                    case SOUTH_WEST -> state.setValue(this.getShapeProperty(), RailShape.SOUTH_EAST);
                    case NORTH_WEST -> state.setValue(this.getShapeProperty(), RailShape.NORTH_EAST);
                    case NORTH_EAST -> state.setValue(this.getShapeProperty(), RailShape.NORTH_WEST);
                }
            }
        }

        return super.mirror(state, mirror);
    }

    @Override
    public boolean isStraight() {
        return !this.hasCorners;
    }

    @Override
    public boolean canMakeSlopes(@Nonnull BlockState state, @Nonnull BlockGetter level, @Nonnull BlockPos pos) {
        return this.hasSlopes;
    }

    @Override
    public boolean isEnabled(@Nonnull FeatureFlagSet enabledFeatures) {
        return FeatureConfig.Transportation.moreRails;
    }
}
