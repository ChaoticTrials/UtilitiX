package de.melanx.utilitix.block;

import de.melanx.utilitix.block.container.ContainerAdvancedBrewery;
import de.melanx.utilitix.block.tile.TileAdvancedBrewery;
import io.github.noeppi_noeppi.libx.block.DirectionShape;
import io.github.noeppi_noeppi.libx.mod.ModX;
import io.github.noeppi_noeppi.libx.mod.registration.BlockGUI;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.Item;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class BlockAdvancedBrewery extends BlockGUI<TileAdvancedBrewery, ContainerAdvancedBrewery> {

    public static final DirectionShape SHAPE = new DirectionShape(makeCuboidShape(1, 0, 1, 15, 10, 13));
    
    public BlockAdvancedBrewery(ModX mod, ContainerType<ContainerAdvancedBrewery> container, Properties properties) {
        this(mod, container, properties, new Item.Properties());
    }

    public BlockAdvancedBrewery(ModX mod, ContainerType<ContainerAdvancedBrewery> container, Properties properties, Item.Properties itemProperties) {
        super(mod, TileAdvancedBrewery.class, container, properties, itemProperties);
        this.setDefaultState(this.getStateContainer().getBaseState().with(BlockStateProperties.HORIZONTAL_FACING, Direction.NORTH));
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context) {
        return this.getDefaultState().with(BlockStateProperties.HORIZONTAL_FACING, context.getPlacementHorizontalFacing().getOpposite());
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(BlockStateProperties.HORIZONTAL_FACING);
    }

    @Override
    @SuppressWarnings("deprecation")
    public int getOpacity(@Nonnull BlockState state, @Nonnull IBlockReader world, @Nonnull BlockPos pos) {
        return 0;
    }

    @Override
    public boolean propagatesSkylightDown(@Nonnull BlockState state, @Nonnull IBlockReader reader, @Nonnull BlockPos pos) {
        return true;
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean isTransparent(@Nonnull BlockState state) {
        return true;
    }

    @Nonnull
    @Override
    @SuppressWarnings("deprecation")
    public VoxelShape getShape(@Nonnull BlockState state, @Nonnull IBlockReader world, @Nonnull BlockPos pos, @Nonnull ISelectionContext context) {
        return SHAPE.getShape(state.get(BlockStateProperties.HORIZONTAL_FACING));
    }
}
