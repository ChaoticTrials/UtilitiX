package de.melanx.utilitix.content.track;

import de.melanx.utilitix.content.track.carts.Cart;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.properties.RailShape;
import net.minecraft.world.phys.Vec3;
import org.apache.commons.lang3.tuple.Pair;

public class TrackUtil {

    public static void accelerateStraight(Level level, BlockPos pos, RailShape shape, AbstractMinecart cart, double accelerationValue) {
        Vec3 motion = cart.getDeltaMovement();
        double horizontalMotion = Math.sqrt(Cart.getHorizontalDistanceSqr(motion));
        if (horizontalMotion > (accelerationValue / 40)) {
            cart.setDeltaMovement(motion.add((motion.x / horizontalMotion) * (accelerationValue * 0.15), 0.0, (motion.z / horizontalMotion) * (accelerationValue * 0.15)));
        } else {
            double motionX = motion.x;
            double motionZ = motion.z;
            if (shape == RailShape.EAST_WEST) {
                if (level.getBlockState(pos.west()).isRedstoneConductor(level, pos.west())) {
                    motionX = (accelerationValue / 20);
                } else if (level.getBlockState(pos.east()).isRedstoneConductor(level, pos.east())) {
                    motionX = -(accelerationValue / 20);
                }
            } else if (shape == RailShape.NORTH_SOUTH) {
                if (level.getBlockState(pos.north()).isRedstoneConductor(level, pos.north())) {
                    motionZ = (accelerationValue / 20);
                } else if (level.getBlockState(pos.south()).isRedstoneConductor(level, pos.south())) {
                    motionZ = -(accelerationValue / 20);
                }
            }
            cart.setDeltaMovement(motionX, motion.y, motionZ);
        }
    }

    public static void slowDownCart(Level level, AbstractMinecart cart, double accelerationValue) {
        double horizontalMotion = Math.sqrt(Cart.getHorizontalDistanceSqr(cart.getDeltaMovement()));
        if (horizontalMotion < (accelerationValue * 0.75)) {
            cart.setDeltaMovement(Vec3.ZERO);
        } else {
            cart.setDeltaMovement(cart.getDeltaMovement().multiply(1 / (5 * accelerationValue), 0, 1 / (5 * accelerationValue)));
        }
    }

    public static Direction getFace(RailShape shape, boolean reverse) {
        return switch (shape) {
            case NORTH_SOUTH, ASCENDING_NORTH, ASCENDING_SOUTH -> reverse ? Direction.SOUTH : Direction.NORTH;
            case ASCENDING_EAST, ASCENDING_WEST, EAST_WEST -> reverse ? Direction.WEST : Direction.EAST;
            default -> Direction.NORTH;
        };
    }

    public static Pair<RailShape, Boolean> getForPlacement(Direction dir) {
        return switch (dir) {
            case NORTH -> Pair.of(RailShape.NORTH_SOUTH, false);
            case SOUTH -> Pair.of(RailShape.NORTH_SOUTH, true);
            case EAST -> Pair.of(RailShape.EAST_WEST, false);
            case WEST -> Pair.of(RailShape.EAST_WEST, true);
            default -> Pair.of(RailShape.NORTH_SOUTH, false);
        };
    }

    public static void defaultCollisions(AbstractMinecart cart, Entity other) {
        if (!cart.level().isClientSide && !cart.noPhysics && !other.noPhysics && !cart.hasPassenger(other)) {
            double xd = other.getX() - cart.getX();
            double zd = other.getZ() - cart.getZ();
            double horizontalSquared = xd * xd + zd * zd;
            if (horizontalSquared >= 0.01 * 0.1) {
                double horizontal = Mth.sqrt((float) horizontalSquared);
                xd = xd / horizontal;
                zd = zd / horizontal;

                if (other instanceof AbstractMinecart otherCart) {
                    Vec3 diffVecN = new Vec3(otherCart.getX() - cart.getX(), 0, otherCart.getZ() - cart.getZ()).normalize();
                    Vec3 cartRot = new Vec3(Math.cos(Math.toRadians(cart.yRot)), 0.0D, Math.sin(Math.toRadians(cart.yRot))).normalize();
                    double product = Math.abs(diffVecN.dot(cartRot));
                    if (product >= 0.8) {
                        Vec3 cartMotion = cart.getDeltaMovement();
                        Vec3 entityMotion = otherCart.getDeltaMovement();
                        if (!cart.isPoweredCart() && otherCart.isPoweredCart()) {
                            cart.setDeltaMovement(cartMotion.multiply(0.2, 1, 0.2));
                            cart.push(entityMotion.x - xd, 0, entityMotion.z - zd);
                            otherCart.setDeltaMovement(entityMotion.multiply(0.95, 1, 0.95));
                        } else if (cart.isPoweredCart() && !otherCart.isPoweredCart()) {
                            otherCart.setDeltaMovement(entityMotion.multiply(0.2, 1, 0.2));
                            otherCart.push(cartMotion.x + xd, 0, cartMotion.z + zd);
                            cart.setDeltaMovement(cartMotion.multiply(0.95, 1, 0.95));
                        } else {
                            double xm = (entityMotion.x + cartMotion.x) / 2;
                            double zm = (entityMotion.z + cartMotion.z) / 2;
                            cart.setDeltaMovement(cartMotion.multiply(0.2, 1, 0.2));
                            cart.push(xm - xd, 0, zm - zd);
                            otherCart.setDeltaMovement(entityMotion.multiply(0.2, 1, 0.2));
                            otherCart.push(xm + xd, 0, zm + zd);
                        }
                    }
                } else {
                    cart.push(-xd, 0, -zd);
                    other.push(xd / 4, 0, zd / 4);
                }
            }
        }
    }
}
