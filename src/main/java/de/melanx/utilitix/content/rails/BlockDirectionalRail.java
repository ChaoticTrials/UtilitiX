package de.melanx.utilitix.content.rails;

import de.melanx.utilitix.block.ModProperties;
import io.github.noeppi_noeppi.libx.mod.ModX;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.item.minecart.AbstractMinecartEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.Item;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.state.properties.RailShape;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nonnull;

public abstract class BlockDirectionalRail extends BlockPoweredRail {
    
    public BlockDirectionalRail(ModX mod, double maxRailSpeed, Properties properties) {
        super(mod, maxRailSpeed, properties);
    }

    public BlockDirectionalRail(ModX mod, double maxRailSpeed, Properties properties, Item.Properties itemProperties) {
        super(mod, maxRailSpeed, properties, itemProperties);
        this.setDefaultState(this.getStateContainer().getBaseState().with(BlockStateProperties.POWERED, false).with(ModProperties.REVERSE, false));
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        super.fillStateContainer(builder);
        builder.add(ModProperties.REVERSE);
    }

    @Override
    public BlockState getStateForPlacement(@Nonnull BlockItemUseContext context) {
        BlockState state = super.getStateForPlacement(context);
        if (state == null) return null;
        Direction direction = context.getPlacementHorizontalFacing();
        Pair<RailShape, Boolean> properties = RailUtil.getForPlacement(direction);
        return state.with(this.getShapeProperty(), properties.getLeft())
                .with(ModProperties.REVERSE, properties.getRight());
    }
    
    @Override
    public void onMinecartPass(BlockState state, World world, BlockPos pos, AbstractMinecartEntity cart) {
        if (state.get(BlockStateProperties.POWERED)) {
            Direction dir = RailUtil.getFace(state.get(this.getShapeProperty()), state.get(ModProperties.REVERSE));
            Vector3d motion = cart.getMotion();
            boolean movingWrongly = false;
            if (dir.getAxis() == Direction.Axis.X) {
                movingWrongly = motion.x != 0 && ((motion.x > 0) != (dir.getXOffset() > 0));
            } else if (dir.getAxis() == Direction.Axis.Z) {
                movingWrongly = motion.z != 0 && ((motion.z > 0) != (dir.getZOffset() > 0));
            }
            if (movingWrongly) {
                cart.setMotion(dir.getXOffset() * (this.maxRailSpeed / 5), 0, dir.getZOffset() * (this.maxRailSpeed / 10));
            } else {
                if (AbstractMinecartEntity.horizontalMag(motion) < ((this.maxRailSpeed / 10) * (this.maxRailSpeed / 10))) {
                    cart.setMotion(dir.getXOffset() * (this.maxRailSpeed / 5), 0, dir.getZOffset() * (this.maxRailSpeed / 5));
                }
                RailUtil.accelerateStraight(world, pos, state.get(this.getShapeProperty()), cart, AbstractMinecartEntity.horizontalMag(motion) < (this.maxRailSpeed / 5) ? 1.5 * this.maxRailSpeed : this.maxRailSpeed);
            }
        } else {
            RailUtil.slowDownCart(world, cart, this.maxRailSpeed);
        }
    }
}
