package de.melanx.utilitix.util;

import com.google.errorprone.annotations.DoNotCall;
import de.melanx.utilitix.UtilitiXConfig;
import de.melanx.utilitix.registration.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.SignalGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class CoreUtil {

    @DoNotCall
    public static boolean shouldPreventWaterlogging(Player player) {
        return UtilitiXConfig.crouchNoWaterlog && player != null && player.isShiftKeyDown();
    }

    @DoNotCall
    public static int getBestNeighborSignalEdit(SignalGetter signalGetter, BlockPos pos) {
        int i = 0;

        for (Direction direction : SignalGetter.DIRECTIONS) {
            int j = CoreUtil.getRedstonePowerAsCallFromNeighbours(signalGetter, pos.relative(direction), direction);
            if (j >= 15) {
                return 15;
            }

            if (j > i) {
                i = j;
            }
        }

        return i;
    }

    private static int getRedstonePowerAsCallFromNeighbours(SignalGetter signalGetter, BlockPos pos, Direction facing) {
        int power = 0;
        BlockState state = signalGetter.getBlockState(pos);
        if (state.getSignal(signalGetter, pos, facing) > 0) {
            power = signalGetter.getSignal(pos, facing);
        }

        if (state.shouldCheckWeakPower(signalGetter, pos, facing)) {
            power = Math.max(power, CoreUtil.getNearStrongPower(signalGetter, pos));
        }

        return power;
    }

    private static int getNearStrongPower(SignalGetter signalGetter, BlockPos pos) {
        BlockPos posDown = pos.below();
        Block block = signalGetter.getBlockState(posDown).getBlock();
        if (block == ModBlocks.weakRedstoneTorch || block == ModBlocks.weakRedstoneTorch.wallTorch) {
            int power = signalGetter.getDirectSignal(pos.above(), Direction.UP);

            if (power >= 15) {
                return power;
            } else {
                power = Math.max(power, signalGetter.getDirectSignal(pos.north(), Direction.NORTH));
                if (power >= 15) {
                    return power;
                } else {
                    power = Math.max(power, signalGetter.getDirectSignal(pos.south(), Direction.SOUTH));
                    if (power >= 15) {
                        return power;
                    } else {
                        power = Math.max(power, signalGetter.getDirectSignal(pos.west(), Direction.WEST));
                        if (power >= 15) {
                            return power;
                        } else {
                            power = Math.max(power, signalGetter.getDirectSignal(pos.east(), Direction.EAST));
                            return power;
                        }
                    }
                }
            }
        } else {
            return signalGetter.getDirectSignalTo(pos);
        }
    }
}
