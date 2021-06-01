package de.melanx.utilitix.content.track.rails;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import io.github.noeppi_noeppi.libx.mod.ModX;
import io.github.noeppi_noeppi.libx.mod.registration.Registerable;
import net.minecraft.block.AbstractRailBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.state.Property;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.RailShape;
import net.minecraft.util.Mirror;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import java.util.Set;
import java.util.function.Consumer;

public abstract class BlockRail extends AbstractRailBlock implements Registerable {

    protected final ModX mod;
    private final Item item;
    private final boolean hasCorners;
    private final boolean hasSlopes;

    public BlockRail(ModX mod, boolean corners, Properties properties) {
        this(mod, corners, properties, new net.minecraft.item.Item.Properties());
    }

    public BlockRail(ModX mod, boolean corners, Properties properties, net.minecraft.item.Item.Properties itemProperties) {
        super(!corners, properties);
        this.mod = mod;
        if (mod.tab != null) {
            itemProperties.group(mod.tab);
        }
        this.item = new BlockItem(this, itemProperties);
        this.hasCorners = this.getShapeProperty().getAllowedValues().containsAll(ImmutableList.of(RailShape.NORTH_EAST, RailShape.NORTH_WEST, RailShape.SOUTH_EAST, RailShape.SOUTH_WEST));
        this.hasSlopes = this.getShapeProperty().getAllowedValues().containsAll(ImmutableList.of(RailShape.ASCENDING_NORTH, RailShape.ASCENDING_SOUTH, RailShape.ASCENDING_EAST, RailShape.ASCENDING_WEST));
    }

    @Override
    public Set<Object> getAdditionalRegisters() {
        return ImmutableSet.of(this.item);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void registerClient(ResourceLocation id, Consumer<Runnable> defer) {
        RenderTypeLookup.setRenderLayer(this, RenderType.getCutout());
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(this.getShapeProperty());
    }
    
    @Nonnull
    @Override
    public abstract Property<RailShape> getShapeProperty();

    @Nonnull
    @Override
    @SuppressWarnings("deprecation")
    public BlockState rotate(@Nonnull BlockState state, @Nonnull Rotation rot) {
        switch(rot) {
            case CLOCKWISE_180:
                switch(state.get(this.getShapeProperty())) {
                    case ASCENDING_EAST:
                        return state.with(this.getShapeProperty(), RailShape.ASCENDING_WEST);
                    case ASCENDING_WEST:
                        return state.with(this.getShapeProperty(), RailShape.ASCENDING_EAST);
                    case ASCENDING_NORTH:
                        return state.with(this.getShapeProperty(), RailShape.ASCENDING_SOUTH);
                    case ASCENDING_SOUTH:
                        return state.with(this.getShapeProperty(), RailShape.ASCENDING_NORTH);
                    case SOUTH_EAST:
                        return state.with(this.getShapeProperty(), RailShape.NORTH_WEST);
                    case SOUTH_WEST:
                        return state.with(this.getShapeProperty(), RailShape.NORTH_EAST);
                    case NORTH_WEST:
                        return state.with(this.getShapeProperty(), RailShape.SOUTH_EAST);
                    case NORTH_EAST:
                        return state.with(this.getShapeProperty(), RailShape.SOUTH_WEST);
                    case NORTH_SOUTH: //Forge fix: MC-196102
                    case EAST_WEST:
                        return state;
                }
            case COUNTERCLOCKWISE_90:
                switch(state.get(this.getShapeProperty())) {
                    case NORTH_SOUTH:
                        return state.with(this.getShapeProperty(), RailShape.EAST_WEST);
                    case EAST_WEST:
                        return state.with(this.getShapeProperty(), RailShape.NORTH_SOUTH);
                    case ASCENDING_EAST:
                        return state.with(this.getShapeProperty(), RailShape.ASCENDING_NORTH);
                    case ASCENDING_WEST:
                        return state.with(this.getShapeProperty(), RailShape.ASCENDING_SOUTH);
                    case ASCENDING_NORTH:
                        return state.with(this.getShapeProperty(), RailShape.ASCENDING_WEST);
                    case ASCENDING_SOUTH:
                        return state.with(this.getShapeProperty(), RailShape.ASCENDING_EAST);
                    case SOUTH_EAST:
                        return state.with(this.getShapeProperty(), RailShape.NORTH_EAST);
                    case SOUTH_WEST:
                        return state.with(this.getShapeProperty(), RailShape.SOUTH_EAST);
                    case NORTH_WEST:
                        return state.with(this.getShapeProperty(), RailShape.SOUTH_WEST);
                    case NORTH_EAST:
                        return state.with(this.getShapeProperty(), RailShape.NORTH_WEST);
                }
            case CLOCKWISE_90:
                switch(state.get(this.getShapeProperty())) {
                    case NORTH_SOUTH:
                        return state.with(this.getShapeProperty(), RailShape.EAST_WEST);
                    case EAST_WEST:
                        return state.with(this.getShapeProperty(), RailShape.NORTH_SOUTH);
                    case ASCENDING_EAST:
                        return state.with(this.getShapeProperty(), RailShape.ASCENDING_SOUTH);
                    case ASCENDING_WEST:
                        return state.with(this.getShapeProperty(), RailShape.ASCENDING_NORTH);
                    case ASCENDING_NORTH:
                        return state.with(this.getShapeProperty(), RailShape.ASCENDING_EAST);
                    case ASCENDING_SOUTH:
                        return state.with(this.getShapeProperty(), RailShape.ASCENDING_WEST);
                    case SOUTH_EAST:
                        return state.with(this.getShapeProperty(), RailShape.SOUTH_WEST);
                    case SOUTH_WEST:
                        return state.with(this.getShapeProperty(), RailShape.NORTH_WEST);
                    case NORTH_WEST:
                        return state.with(this.getShapeProperty(), RailShape.NORTH_EAST);
                    case NORTH_EAST:
                        return state.with(this.getShapeProperty(), RailShape.SOUTH_EAST);
                }
            default:
                return state;
        }
    }
    
    @Nonnull
    @Override
    @SuppressWarnings("deprecation")
    public BlockState mirror(BlockState state, Mirror mirrorIn) {
        RailShape railshape = state.get(this.getShapeProperty());
        switch(mirrorIn) {
            case LEFT_RIGHT:
                switch(railshape) {
                    case ASCENDING_NORTH:
                        return state.with(this.getShapeProperty(), RailShape.ASCENDING_SOUTH);
                    case ASCENDING_SOUTH:
                        return state.with(this.getShapeProperty(), RailShape.ASCENDING_NORTH);
                    case SOUTH_EAST:
                        return state.with(this.getShapeProperty(), RailShape.NORTH_EAST);
                    case SOUTH_WEST:
                        return state.with(this.getShapeProperty(), RailShape.NORTH_WEST);
                    case NORTH_WEST:
                        return state.with(this.getShapeProperty(), RailShape.SOUTH_WEST);
                    case NORTH_EAST:
                        return state.with(this.getShapeProperty(), RailShape.SOUTH_EAST);
                    default:
                        return super.mirror(state, mirrorIn);
                }
            case FRONT_BACK:
                switch(railshape) {
                    case ASCENDING_EAST:
                        return state.with(this.getShapeProperty(), RailShape.ASCENDING_WEST);
                    case ASCENDING_WEST:
                        return state.with(this.getShapeProperty(), RailShape.ASCENDING_EAST);
                    case ASCENDING_NORTH:
                    case ASCENDING_SOUTH:
                    default:
                        break;
                    case SOUTH_EAST:
                        return state.with(this.getShapeProperty(), RailShape.SOUTH_WEST);
                    case SOUTH_WEST:
                        return state.with(this.getShapeProperty(), RailShape.SOUTH_EAST);
                    case NORTH_WEST:
                        return state.with(this.getShapeProperty(), RailShape.NORTH_EAST);
                    case NORTH_EAST:
                        return state.with(this.getShapeProperty(), RailShape.NORTH_WEST);
                }
        }

        return super.mirror(state, mirrorIn);
    }

    @Override
    public boolean areCornersDisabled() {
        return !this.hasCorners;
    }

    @Override
    public boolean canMakeSlopes(BlockState state, IBlockReader world, BlockPos pos) {
        return this.hasSlopes;
    }
}
