package de.melanx.utilitix.content.crudefurnace;

import de.melanx.utilitix.config.FeatureConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.AbstractFurnaceBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import org.moddingx.libx.base.tile.MenuBlockBE;
import org.moddingx.libx.inventory.BaseItemStackHandler;
import org.moddingx.libx.mod.ModX;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class CrudeFurnaceBlock extends MenuBlockBE<CrudeFurnaceBlockEntity, CrudeFurnaceMenu> {

    public CrudeFurnaceBlock(ModX mod, Properties properties) {
        super(mod, CrudeFurnaceBlockEntity.class, CrudeFurnaceMenu.TYPE, properties);
        this.registerDefaultState(this.defaultBlockState()
                .setValue(HorizontalDirectionalBlock.FACING, Direction.NORTH)
                .setValue(AbstractFurnaceBlock.LIT, false));
    }

    @Override
    public boolean hasAnalogOutputSignal(@Nonnull BlockState state) {
        return true;
    }

    @Override
    public int getAnalogOutputSignal(@Nonnull BlockState blockState, @Nonnull Level level, @Nonnull BlockPos pos) {
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (blockEntity instanceof CrudeFurnaceBlockEntity crudeFurnace) {
            return AbstractContainerMenu.getRedstoneSignalFromContainer(((BaseItemStackHandler) crudeFurnace.getUnrestricted()).toVanilla());
        }

        return super.getAnalogOutputSignal(blockState, level, pos);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(@Nonnull BlockPlaceContext context) {
        return this.defaultBlockState().setValue(HorizontalDirectionalBlock.FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override
    protected void createBlockStateDefinition(@Nonnull StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(HorizontalDirectionalBlock.FACING, AbstractFurnaceBlock.LIT);
    }

    @Override
    public boolean isEnabled(@Nonnull FeatureFlagSet enabledFeatures) {
        return FeatureConfig.Machines.crudeFurnace;
    }
}
