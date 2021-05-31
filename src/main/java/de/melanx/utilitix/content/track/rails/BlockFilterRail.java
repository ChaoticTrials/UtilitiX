package de.melanx.utilitix.content.track.rails;

import de.melanx.utilitix.block.ModProperties;
import de.melanx.utilitix.content.track.ItemMinecartTinkerer;
import de.melanx.utilitix.content.track.TrackUtil;
import io.github.noeppi_noeppi.libx.mod.ModX;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.item.minecart.AbstractMinecartEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.state.Property;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.RailShape;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.IBlockReader;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class BlockFilterRail extends BlockControllerRail<TileFilterRail> {
    
    public BlockFilterRail(ModX mod, boolean reinforced, Properties properties) {
        super(mod, TileFilterRail::new, reinforced, properties);
    }

    public BlockFilterRail(ModX mod, boolean reinforced, Properties properties, Item.Properties itemProperties) {
        super(mod, TileFilterRail::new, reinforced, properties, itemProperties);
    }

    @Nonnull
    @Override
    public Property<RailShape> getShapeProperty() {
        return ModProperties.RAIL_SHAPE_FLAT_STRAIGHT;
    }
    
    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        super.fillStateContainer(builder);
        builder.add(ModProperties.REVERSE);
        builder.add(ModProperties.RAIL_SIDE);
    }
    
    @Override
    public BlockState getStateForPlacement(@Nonnull BlockItemUseContext context) {
        BlockState state = super.getStateForPlacement(context);
        if (state == null) return null;
        Direction direction = context.getPlacementHorizontalFacing();
        Pair<RailShape, Boolean> properties = TrackUtil.getForPlacement(direction);
        state = state.with(this.getShapeProperty(), properties.getLeft())
                .with(ModProperties.REVERSE, properties.getRight());
        Vector3d hitVec = context.getHitVec();
        double xd = hitVec.x - context.getPos().getX();
        double zd = hitVec.z - context.getPos().getZ();
        boolean side = (direction.getXOffset() >= 0 || !(zd < 0.5)) && (direction.getXOffset() <= 0 || !(zd > 0.5)) && (direction.getZOffset() >= 0 || !(xd > 0.5)) && (direction.getZOffset() <= 0 || !(xd < 0.5));
        return state.with(ModProperties.RAIL_SIDE, !side);
    }

    @Nonnull
    @Override
    public RailShape getRailDirection(@Nonnull BlockState state, @Nonnull IBlockReader world, @Nonnull BlockPos pos, @Nullable AbstractMinecartEntity cart) {
        RailShape baseShape = state.get(this.getShapeProperty());
        ItemStack filterCart = cart == null ? ItemStack.EMPTY : ItemMinecartTinkerer.getLabelStack(cart);
        if (filterCart.isEmpty()) return baseShape;
        ItemStack filterThis = this.getTile(world, pos).getFilterStack();
        if (filterThis.isEmpty()) return baseShape;
        if (!ItemStack.areItemsEqual(filterThis, filterCart) || !ItemStack.areItemStackTagsEqual(filterThis, filterCart)) {
            return baseShape;
        }
        boolean reverse = state.get(ModProperties.REVERSE);
        boolean side = state.get(ModProperties.RAIL_SIDE);
        if (baseShape == RailShape.NORTH_SOUTH) {
            if (reverse && side) {
                return RailShape.NORTH_WEST;
            } else if (reverse) {
                return RailShape.NORTH_EAST;
            } else if (side) {
                return RailShape.SOUTH_WEST;
            } else {
                return RailShape.SOUTH_EAST;
            }
        } else {
            if (reverse && side) {
                return RailShape.NORTH_WEST;
            } else if (reverse) {
                return RailShape.SOUTH_WEST;
            } else if (side) {
                return RailShape.NORTH_EAST;
            } else {
                return RailShape.SOUTH_EAST;
            }
        }
    }
}
