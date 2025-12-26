package de.melanx.utilitix.content.track.rails;

import com.mojang.serialization.MapCodec;
import de.melanx.utilitix.UtilitiX;
import de.melanx.utilitix.block.ModProperties;
import de.melanx.utilitix.content.track.MinecartTinkererItem;
import de.melanx.utilitix.content.track.TrackUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.BaseRailBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.state.properties.RailShape;
import net.minecraft.world.phys.Vec3;
import org.apache.commons.lang3.tuple.Pair;
import org.moddingx.libx.mod.ModX;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class FilterRailBlock extends ControllerRailBlock<FilterRailBlockEntity> {

    public static final MapCodec<FilterRailBlock> CODEC = Block.simpleCodec(FilterRailBlock::new);

    public FilterRailBlock(Properties properties) {
        this(UtilitiX.getInstance(), false, properties);
    }

    public FilterRailBlock(ModX mod, boolean reinforced, Properties properties) {
        super(mod, FilterRailBlockEntity::new, reinforced, properties);
    }

    public FilterRailBlock(ModX mod, boolean reinforced, Properties properties, Item.Properties itemProperties) {
        super(mod, FilterRailBlockEntity::new, reinforced, properties, itemProperties);
    }

    @Nonnull
    @Override
    public Property<RailShape> getShapeProperty() {
        return ModProperties.RAIL_SHAPE_FLAT_STRAIGHT;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(ModProperties.REVERSE);
        builder.add(ModProperties.RAIL_SIDE);
    }

    @Nonnull
    @Override
    public BlockState getStateForPlacement(@Nonnull BlockPlaceContext context) {
        BlockState state = super.getStateForPlacement(context);
        Direction direction = context.getHorizontalDirection();
        Pair<RailShape, Boolean> properties = TrackUtil.getForPlacement(direction);
        state = state.setValue(this.getShapeProperty(), properties.getLeft())
                .setValue(ModProperties.REVERSE, properties.getRight());
        Vec3 hitVec = context.getClickLocation();
        double xd = hitVec.x - context.getClickedPos().getX();
        double zd = hitVec.z - context.getClickedPos().getZ();
        boolean side = (direction.getStepX() >= 0 || !(zd < 0.5)) && (direction.getStepX() <= 0 || !(zd > 0.5)) && (direction.getStepZ() >= 0 || !(xd > 0.5)) && (direction.getStepZ() <= 0 || !(xd < 0.5));

        return state.setValue(ModProperties.RAIL_SIDE, !side);
    }

    @Nonnull
    @Override
    public RailShape getRailDirection(@Nonnull BlockState state, @Nonnull BlockGetter level, @Nonnull BlockPos pos, @Nullable AbstractMinecart cart) {
        RailShape baseShape = state.getValue(this.getShapeProperty());

        ItemStack filterCart = cart == null ? ItemStack.EMPTY : MinecartTinkererItem.getLabelStack(cart);
        if (filterCart.isEmpty()) {
            return baseShape;
        }

        ItemStack filterThis = this.getTile(level, pos).getFilterStack();
        if (filterThis.isEmpty()) {
            return baseShape;
        }

        if (!ItemStack.isSameItemSameComponents(filterThis, filterCart)) {
            return baseShape;
        }

        boolean reverse = state.getValue(ModProperties.REVERSE);
        boolean side = state.getValue(ModProperties.RAIL_SIDE);
        if (baseShape == RailShape.NORTH_SOUTH) {
            if (reverse && side) {
                return RailShape.NORTH_WEST;
            }

            if (reverse) {
                return RailShape.NORTH_EAST;
            }

            if (side) {
                return RailShape.SOUTH_WEST;
            }

            return RailShape.SOUTH_EAST;
        } else {
            if (reverse && side) {
                return RailShape.NORTH_WEST;
            }

            if (reverse) {
                return RailShape.SOUTH_WEST;
            }

            if (side) {
                return RailShape.NORTH_EAST;
            }

            return RailShape.SOUTH_EAST;
        }
    }

    @Nonnull
    @Override
    protected MapCodec<? extends BaseRailBlock> codec() {
        return CODEC;
    }
}
