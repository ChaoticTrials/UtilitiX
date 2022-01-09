package de.melanx.utilitix.content.brewery;

import io.github.noeppi_noeppi.libx.base.tile.MenuBlockBE;
import io.github.noeppi_noeppi.libx.block.RotationShape;
import io.github.noeppi_noeppi.libx.menu.BlockEntityMenu;
import io.github.noeppi_noeppi.libx.mod.ModX;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.Consumer;

public class BlockAdvancedBrewery extends MenuBlockBE<TileAdvancedBrewery, ContainerMenuAdvancedBrewery> {

    public static final RotationShape SHAPE = new RotationShape(box(1, 0, 1, 15, 12, 13));

    public BlockAdvancedBrewery(ModX mod, Properties properties) {
        this(mod, properties, new Item.Properties());
    }

    public BlockAdvancedBrewery(ModX mod, Properties properties, Item.Properties itemProperties) {
        super(mod, TileAdvancedBrewery.class, BlockEntityMenu.createMenuType(ContainerMenuAdvancedBrewery::new), properties, itemProperties);
        this.registerDefaultState(this.defaultBlockState().setValue(BlockStateProperties.HORIZONTAL_FACING, Direction.NORTH));
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void registerClient(ResourceLocation id, Consumer<Runnable> defer) {
        BlockEntityRenderers.register(this.getBlockEntityType(), context -> new BesrAdvancedBrewery());
        MenuScreens.register(this.menu, ScreenAdvancedBrewery::new);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(BlockStateProperties.HORIZONTAL_FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(BlockStateProperties.HORIZONTAL_FACING);
    }

    @Override
    @SuppressWarnings("deprecation")
    public int getLightBlock(@Nonnull BlockState state, @Nonnull BlockGetter level, @Nonnull BlockPos pos) {
        return 0;
    }

    @Override
    public boolean propagatesSkylightDown(@Nonnull BlockState state, @Nonnull BlockGetter reader, @Nonnull BlockPos pos) {
        return true;
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean useShapeForLightOcclusion(@Nonnull BlockState state) {
        return true;
    }

    @Nonnull
    @Override
    @SuppressWarnings("deprecation")
    public VoxelShape getShape(@Nonnull BlockState state, @Nonnull BlockGetter level, @Nonnull BlockPos pos, @Nonnull CollisionContext context) {
        return SHAPE.getShape(state.getValue(BlockStateProperties.HORIZONTAL_FACING));
    }
}
