package de.melanx.utilitix.content.track.rails;

import net.minecraft.entity.item.minecart.AbstractMinecartEntity;
import net.minecraft.state.properties.RailShape;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import org.apache.commons.lang3.tuple.Pair;

public class RailUtil {

    public static void accelerateStraight(World world, BlockPos pos, RailShape shape, AbstractMinecartEntity cart, double accelerationValue) {
        Vector3d motion = cart.getMotion();
        double horizontalMotion = Math.sqrt(AbstractMinecartEntity.horizontalMag(motion));
        if (horizontalMotion > (accelerationValue / 40)) {
            cart.setMotion(motion.add((motion.x / horizontalMotion) * (accelerationValue * 0.15), 0.0, (motion.z / horizontalMotion) * (accelerationValue * 0.15)));
        } else {
            double motionX = motion.x;
            double motionZ = motion.z;
            if (shape == RailShape.EAST_WEST) {
                if (world.getBlockState(pos.west()).isNormalCube(world, pos.west())) {
                    motionX = (accelerationValue / 20);
                } else if (world.getBlockState(pos.east()).isNormalCube(world, pos.east())) {
                    motionX = -(accelerationValue / 20);
                }
            } else if (shape == RailShape.NORTH_SOUTH) {
                if (world.getBlockState(pos.north()).isNormalCube(world, pos.north())) {
                    motionZ = (accelerationValue / 20);
                } else if (world.getBlockState(pos.south()).isNormalCube(world, pos.south())) {
                    motionZ = -(accelerationValue / 20);
                }
            }
            cart.setMotion(motionX, motion.y, motionZ);
        }
    }
    
    public static void slowDownCart(World world, AbstractMinecartEntity cart, double accelerationValue) {
        double horizontalMotion = Math.sqrt(AbstractMinecartEntity.horizontalMag(cart.getMotion()));
        if (horizontalMotion < (accelerationValue * 0.75)) {
            cart.setMotion(Vector3d.ZERO);
        } else {
            cart.setMotion(cart.getMotion().mul(1 / (5 * accelerationValue), 0, 1 / (5 * accelerationValue)));
        }
    }
    
    public static Direction getFace(RailShape shape, boolean reverse) {
        switch (shape) {
            case NORTH_SOUTH: 
            case ASCENDING_NORTH:
            case ASCENDING_SOUTH:
                return reverse ? Direction.SOUTH : Direction.NORTH;
            case ASCENDING_EAST:
            case ASCENDING_WEST:
            case EAST_WEST: return reverse ? Direction.WEST : Direction.EAST;
            default: return Direction.NORTH;
        }
    }
    
    public static Pair<RailShape, Boolean> getForPlacement(Direction dir) {
        switch (dir) {
            case NORTH: return Pair.of(RailShape.NORTH_SOUTH, false);
            case SOUTH: return Pair.of(RailShape.NORTH_SOUTH, true);
            case EAST: return Pair.of(RailShape.EAST_WEST, false);
            case WEST: return Pair.of(RailShape.EAST_WEST, true);
        }
        return Pair.of(RailShape.NORTH_SOUTH, false);
    }
}
