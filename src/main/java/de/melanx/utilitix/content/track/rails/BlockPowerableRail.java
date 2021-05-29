package de.melanx.utilitix.content.track.rails;

import io.github.noeppi_noeppi.libx.mod.ModX;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.Item;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.state.properties.RailShape;
import net.minecraft.tags.ITag;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public abstract class BlockPowerableRail extends BlockRail {

    @Nullable
    private final ITag<Block> powerables;

    public BlockPowerableRail(ModX mod, @Nullable ITag<Block> powerables, Properties properties) {
        this(mod, powerables, properties, new Item.Properties());
    }

    public BlockPowerableRail(ModX mod, @Nullable ITag<Block> powerables, Properties properties, Item.Properties itemProperties) {
        super(mod, false, properties, itemProperties);
        this.powerables = powerables;
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        super.fillStateContainer(builder);
        builder.add(BlockStateProperties.POWERED);
    }

    protected boolean findPower(World world, BlockPos pos, BlockState state, boolean searchForward, int recursionCount) {
        if (recursionCount >= 8 || !(state.getBlock() instanceof BlockPowerableRail)) {
            return false;
        }
        BlockPos searchPos = pos.toImmutable();
        boolean lookDown = true;
        RailShape rail = state.get(((BlockPowerableRail) state.getBlock()).getShapeProperty());
        switch (rail) {
            case NORTH_SOUTH:
                searchPos = searchPos.offset(searchForward ? Direction.SOUTH : Direction.NORTH);
                break;
            case EAST_WEST:
                searchPos = searchPos.offset(searchForward ? Direction.EAST : Direction.WEST);
                break;
            case ASCENDING_EAST:
                if (searchForward) {
                    searchPos = searchPos.west();
                } else {
                    searchPos = searchPos.east().up();
                    lookDown = false;
                }
                rail = RailShape.EAST_WEST;
                break;
            case ASCENDING_WEST:
                if (searchForward) {
                    searchPos = searchPos.west().up();
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
                    searchPos = searchPos.north().up();
                    lookDown = false;
                }
                rail = RailShape.NORTH_SOUTH;
                break;
            case ASCENDING_SOUTH:
                if (searchForward) {
                    searchPos = searchPos.south().up();
                    lookDown = false;
                } else {
                    searchPos = searchPos.north();
                }
                rail = RailShape.NORTH_SOUTH;
                break;
        }

        return this.canPower(world, searchPos, searchForward, recursionCount, rail) || (lookDown && this.canMakeSlopes(world.getBlockState(searchPos), world, searchPos) && this.canPower(world, searchPos.down(), searchForward, recursionCount, rail));
    }

    private boolean canPower(World world, BlockPos pos, boolean searchForward, int recursionCount, RailShape shape) {
        BlockState target = world.getBlockState(pos);
        if (!(target.getBlock() instanceof BlockPowerableRail)) {
            return false;
        } else {
            RailShape rail = this.getRailDirection(target, world, pos, null);
            if (shape == RailShape.EAST_WEST && (rail == RailShape.NORTH_SOUTH || rail == RailShape.ASCENDING_NORTH || rail == RailShape.ASCENDING_SOUTH)) {
                return false;
            }
            if (shape == RailShape.NORTH_SOUTH && (rail == RailShape.EAST_WEST || rail == RailShape.ASCENDING_EAST || rail == RailShape.ASCENDING_WEST)) {
                return false;
            }
            if (this.powerables == null ? target.getBlock() == this : this.powerables.contains(target.getBlock())) {
                return world.isBlockPowered(pos) || this.findPower(world, pos, target, searchForward, recursionCount + 1);
            } else {
                return false;
            }
        }
    }

    @Override
    protected void updateState(BlockState state, World world, @Nonnull BlockPos pos, @Nonnull Block block) {
        boolean powered = state.get(BlockStateProperties.POWERED);
        boolean shouldBePowered = world.isBlockPowered(pos) || this.findPower(world, pos, state, true, 0) || this.findPower(world, pos, state, false, 0);
        if (powered != shouldBePowered) {
            world.setBlockState(pos, state.with(BlockStateProperties.POWERED, shouldBePowered), 3);
            world.notifyNeighborsOfStateChange(pos.down(), this);
            if (state.get(this.getShapeProperty()).isAscending()) {
                world.notifyNeighborsOfStateChange(pos.up(), this);
            }
        }
    }
}
