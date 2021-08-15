package de.melanx.utilitix.content.track.rails;

import io.github.noeppi_noeppi.libx.mod.ModX;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.Tag;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.RailShape;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public abstract class BlockPowerableRail extends BlockRail {

    @Nullable
    private final Tag<Block> powerables;

    public BlockPowerableRail(ModX mod, @Nullable Tag<Block> powerables, Properties properties) {
        this(mod, powerables, properties, new Item.Properties());
    }

    public BlockPowerableRail(ModX mod, @Nullable Tag<Block> powerables, Properties properties, Item.Properties itemProperties) {
        super(mod, false, properties, itemProperties);
        this.powerables = powerables;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(BlockStateProperties.POWERED);
    }

    protected boolean findPower(Level level, BlockPos pos, BlockState state, boolean searchForward, int recursionCount) {
        if (recursionCount >= 8 || !(state.getBlock() instanceof BlockPowerableRail)) {
            return false;
        }
        BlockPos searchPos = pos.immutable();
        boolean lookDown = true;
        RailShape rail = state.getValue(((BlockPowerableRail) state.getBlock()).getShapeProperty());
        switch (rail) {
            case NORTH_SOUTH:
                searchPos = searchPos.relative(searchForward ? Direction.SOUTH : Direction.NORTH);
                break;
            case EAST_WEST:
                searchPos = searchPos.relative(searchForward ? Direction.EAST : Direction.WEST);
                break;
            case ASCENDING_EAST:
                if (searchForward) {
                    searchPos = searchPos.west();
                } else {
                    searchPos = searchPos.east().above();
                    lookDown = false;
                }
                rail = RailShape.EAST_WEST;
                break;
            case ASCENDING_WEST:
                if (searchForward) {
                    searchPos = searchPos.west().above();
                    lookDown = false;
                } else {
                    searchPos = searchPos.east();
                }
                rail = RailShape.EAST_WEST;
                break;
            case ASCENDING_NORTH:
                if (searchForward) {
                    searchPos = searchPos.south();
                } else {
                    searchPos = searchPos.north().above();
                    lookDown = false;
                }
                rail = RailShape.NORTH_SOUTH;
                break;
            case ASCENDING_SOUTH:
                if (searchForward) {
                    searchPos = searchPos.south().above();
                    lookDown = false;
                } else {
                    searchPos = searchPos.north();
                }
                rail = RailShape.NORTH_SOUTH;
                break;
        }

        return this.canPower(level, searchPos, searchForward, recursionCount, rail) || (lookDown && this.canMakeSlopes(level.getBlockState(searchPos), level, searchPos) && this.canPower(level, searchPos.below(), searchForward, recursionCount, rail));
    }

    private boolean canPower(Level level, BlockPos pos, boolean searchForward, int recursionCount, RailShape shape) {
        BlockState target = level.getBlockState(pos);
        if (!(target.getBlock() instanceof BlockPowerableRail)) {
            return false;
        } else {
            RailShape rail = target.getBlock() == this ? this.getRailDirection(target, level, pos, null) : RailShape.NORTH_SOUTH;
            if (shape == RailShape.EAST_WEST && (rail == RailShape.NORTH_SOUTH || rail == RailShape.ASCENDING_NORTH || rail == RailShape.ASCENDING_SOUTH)) {
                return false;
            }
            if (shape == RailShape.NORTH_SOUTH && (rail == RailShape.EAST_WEST || rail == RailShape.ASCENDING_EAST || rail == RailShape.ASCENDING_WEST)) {
                return false;
            }
            if (this.powerables == null ? target.getBlock() == this : this.powerables.contains(target.getBlock())) {
                return level.hasNeighborSignal(pos) || this.findPower(level, pos, target, searchForward, recursionCount + 1);
            } else {
                return false;
            }
        }
    }

    @Override
    protected void updateState(BlockState state, @Nonnull Level level, @Nonnull BlockPos pos, @Nonnull Block block) {
        if (state.getBlock() == this) {
            boolean powered = state.getValue(BlockStateProperties.POWERED);
            boolean shouldBePowered = level.hasNeighborSignal(pos) || this.findPower(level, pos, state, true, 0) || this.findPower(level, pos, state, false, 0);
            if (powered != shouldBePowered) {
                level.setBlock(pos, state.setValue(BlockStateProperties.POWERED, shouldBePowered), 3);
                level.updateNeighborsAt(pos.below(), this);
                if (state.getValue(this.getShapeProperty()).isAscending()) {
                    level.updateNeighborsAt(pos.above(), this);
                }
            }
        }
    }
}
