package de.melanx.utilitix.block;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import org.moddingx.libx.base.BlockBase;
import org.moddingx.libx.mod.ModX;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.ToIntFunction;

public class DimmableRedstoneLamp extends BlockBase {

    public static final IntegerProperty SIGNAL = BlockStateProperties.POWER;
    public static final ToIntFunction<BlockState> LIGHT_EMISSION = state -> state.getValue(SIGNAL);

    public DimmableRedstoneLamp(ModX mod, Properties properties) {
        super(mod, properties);
        this.registerDefaultState(this.defaultBlockState().setValue(SIGNAL, 0));
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(@Nonnull BlockPlaceContext context) {
        return this.defaultBlockState().setValue(SIGNAL, context.getLevel().getSignal(context.getClickedPos(), context.getClickedFace()));
    }

    @SuppressWarnings("deprecation")
    @Override
    public void neighborChanged(@Nonnull BlockState state, @Nonnull Level level, @Nonnull BlockPos pos, @Nonnull Block block, @Nonnull BlockPos fromPos, boolean isMoving) {
        if (!level.isClientSide) {
            this.updatePowerStrength(state, level, pos);
        }
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onPlace(@Nonnull BlockState state, @Nonnull Level level, @Nonnull BlockPos pos, @Nonnull BlockState oldState, boolean isMoving) {
        if (!oldState.is(state.getBlock()) && !level.isClientSide) {
            this.updatePowerStrength(state, level, pos);
        }
    }

    private void updatePowerStrength(BlockState state, Level level, BlockPos pos) {
        int signal = level.getBestNeighborSignal(pos);
        boolean hasSignal = state.getValue(SIGNAL) > 0;
        if (hasSignal != level.hasNeighborSignal(pos) && hasSignal) {
            level.scheduleTick(pos, this, 4);
            return;
        }

        if (state.getValue(SIGNAL) != signal) {
            level.setBlock(pos, state.setValue(SIGNAL, signal), Block.UPDATE_CLIENTS);
        }
    }

    @SuppressWarnings("deprecation")
    @Override
    public void tick(@Nonnull BlockState state, @Nonnull ServerLevel level, @Nonnull BlockPos pos, @Nonnull RandomSource random) {
        if (state.getValue(SIGNAL) > 0 && !level.hasNeighborSignal(pos)) {
            level.setBlock(pos, state.setValue(SIGNAL, 0), Block.UPDATE_CLIENTS);
        }
    }

    @Override
    protected void createBlockStateDefinition(@Nonnull StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(SIGNAL);
    }
}
