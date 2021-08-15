package de.melanx.utilitix.content.track.rails;

import de.melanx.utilitix.block.ModProperties;
import de.melanx.utilitix.content.track.TrackUtil;
import de.melanx.utilitix.content.track.carts.Cart;
import io.github.noeppi_noeppi.libx.mod.ModX;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.RailShape;
import net.minecraft.world.phys.Vec3;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nonnull;

public abstract class BlockDirectionalRail extends BlockPoweredRail {

    public BlockDirectionalRail(ModX mod, double maxRailSpeed, Properties properties) {
        super(mod, maxRailSpeed, properties);
    }

    public BlockDirectionalRail(ModX mod, double maxRailSpeed, Properties properties, Item.Properties itemProperties) {
        super(mod, maxRailSpeed, properties, itemProperties);
        this.registerDefaultState(this.getStateDefinition().any().setValue(BlockStateProperties.POWERED, false).setValue(ModProperties.REVERSE, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(ModProperties.REVERSE);
    }

    @Override
    public BlockState getStateForPlacement(@Nonnull BlockPlaceContext context) {
        BlockState state = super.getStateForPlacement(context);
        if (state == null) return null;
        Direction direction = context.getHorizontalDirection();
        Pair<RailShape, Boolean> properties = TrackUtil.getForPlacement(direction);
        return state.setValue(this.getShapeProperty(), properties.getLeft())
                .setValue(ModProperties.REVERSE, properties.getRight());
    }

    @Override
    public void onMinecartPass(BlockState state, Level level, BlockPos pos, AbstractMinecart cart) {
        if (state.getValue(BlockStateProperties.POWERED)) {
            Direction dir = TrackUtil.getFace(state.getValue(this.getShapeProperty()), state.getValue(ModProperties.REVERSE));
            Vec3 motion = cart.getDeltaMovement();
            boolean movingWrongly = false;
            if (dir.getAxis() == Direction.Axis.X) {
                movingWrongly = motion.x != 0 && ((motion.x > 0) != (dir.getStepX() > 0));
            } else if (dir.getAxis() == Direction.Axis.Z) {
                movingWrongly = motion.z != 0 && ((motion.z > 0) != (dir.getStepZ() > 0));
            }
            if (movingWrongly) {
                cart.setDeltaMovement(dir.getStepX() * (this.maxRailSpeed / 5), 0, dir.getStepZ() * (this.maxRailSpeed / 10));
            } else {
                if (Cart.getHorizontalDistanceSqr(motion) < ((this.maxRailSpeed / 10) * (this.maxRailSpeed / 10))) {
                    cart.setDeltaMovement(dir.getStepX() * (this.maxRailSpeed / 5), 0, dir.getStepZ() * (this.maxRailSpeed / 5));
                }
                TrackUtil.accelerateStraight(level, pos, state.getValue(this.getShapeProperty()), cart, Cart.getHorizontalDistanceSqr(motion) < (this.maxRailSpeed / 5) ? 1.5 * this.maxRailSpeed : this.maxRailSpeed);
            }
        } else {
            TrackUtil.slowDownCart(level, cart, this.maxRailSpeed);
        }
    }
}
