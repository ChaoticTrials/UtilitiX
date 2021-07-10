package de.melanx.utilitix.content.crudefurnace;

import de.melanx.utilitix.registration.ModBlocks;
import io.github.noeppi_noeppi.libx.inventory.BaseItemStackHandler;
import io.github.noeppi_noeppi.libx.mod.ModX;
import io.github.noeppi_noeppi.libx.mod.registration.BlockGUI;
import net.minecraft.block.AbstractFurnaceBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalBlock;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.Consumer;

public class BlockCrudeFurnace extends BlockGUI<TileCrudeFurnace, ContainerCrudeFurnace> {

    public BlockCrudeFurnace(ModX mod, ContainerType<ContainerCrudeFurnace> container, Properties properties) {
        super(mod, TileCrudeFurnace.class, container, properties);
        this.setDefaultState(this.getStateContainer().getBaseState()
                .with(HorizontalBlock.HORIZONTAL_FACING, Direction.NORTH)
                .with(AbstractFurnaceBlock.LIT, false));
    }

    @Override
    public void registerClient(ResourceLocation id, Consumer<Runnable> defer) {
        ScreenManager.registerFactory(ModBlocks.crudeFurnace.container, ScreenCrudeFurnace::new);
    }

    @Override
    public void onReplaced(@Nonnull BlockState state, @Nonnull World world, @Nonnull BlockPos pos, @Nonnull BlockState newState, boolean isMoving) {
        if (!world.isRemote && !state.matchesBlock(newState.getBlock())) {
            TileEntity tile = world.getTileEntity(pos);
            if (tile instanceof TileCrudeFurnace) {
                InventoryHelper.dropInventoryItems(world, pos, ((BaseItemStackHandler) ((TileCrudeFurnace) tile).getInventory()).toIInventory());
                ((TileCrudeFurnace) tile).grantStoredRecipeExperience(world, Vector3d.copyCentered(pos));
                world.updateComparatorOutputLevel(pos, this);
            }

            super.onReplaced(state, world, pos, newState, isMoving);
        }
    }

    @Override
    protected boolean shouldDropInventory(World world, BlockPos pos, BlockState state) {
        return false;
    }

    @SuppressWarnings("deprecation")
    @Override
    public boolean hasComparatorInputOverride(@Nonnull BlockState state) {
        return true;
    }

    @SuppressWarnings("deprecation")
    @Override
    public int getComparatorInputOverride(@Nonnull BlockState state, @Nonnull World world, @Nonnull BlockPos pos) {
        TileEntity tile = world.getTileEntity(pos);
        if (tile instanceof TileCrudeFurnace) {
            return Container.calcRedstoneFromInventory(((BaseItemStackHandler) ((TileCrudeFurnace) tile).getUnrestricted()).toIInventory());
        }

        return super.getComparatorInputOverride(state, world, pos);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(@Nonnull BlockItemUseContext context) {
        return this.getDefaultState().with(HorizontalBlock.HORIZONTAL_FACING, context.getPlacementHorizontalFacing().getOpposite());
    }

    @Override
    protected void fillStateContainer(@Nonnull StateContainer.Builder<Block, BlockState> builder) {
        builder.add(HorizontalBlock.HORIZONTAL_FACING, AbstractFurnaceBlock.LIT);
    }
}
