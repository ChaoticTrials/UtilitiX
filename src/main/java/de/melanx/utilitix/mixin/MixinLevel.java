package de.melanx.utilitix.mixin;

import de.melanx.utilitix.block.ComparatorRedirector;
import de.melanx.utilitix.registration.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Level.class)
public abstract class MixinLevel {

    @Inject(
            method = "updateNeighbourForOutputSignal",
            at = @At("RETURN")
    )
    public void updateComparatorOutputLevel(BlockPos pos, Block block, CallbackInfo ci) {
        if (!(block instanceof ComparatorRedirector)) {
            BlockState up = ((Level) (Object) this).getBlockState(pos.above());
            if (up.getBlock() instanceof ComparatorRedirector) {
                ((Level) (Object) this).updateNeighbourForOutputSignal(pos.above(), up.getBlock());
            }
            BlockState down = ((Level) (Object) this).getBlockState(pos.below());
            if (down.getBlock() instanceof ComparatorRedirector) {
                ((Level) (Object) this).updateNeighbourForOutputSignal(pos.below(), down.getBlock());
            }
        }
    }

    @Redirect(
            method = "getBestNeighborSignal",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/Level;getSignal(Lnet/minecraft/core/BlockPos;Lnet/minecraft/core/Direction;)I"
            )
    )
    public int getRedstonePowerAsCallFromNeighbours(Level level, BlockPos pos, Direction facing) {
        int power = 0;
        BlockState state = level.getBlockState(pos);
        if (state.getSignal(level, pos, facing) > 0) {
            power = level.getSignal(pos, facing);
        }

        if (state.shouldCheckWeakPower(level, pos, facing)) {
            power = Math.max(power, this.getNearStrongPower(level, pos));
        }

        return power;
    }

    @Unique
    private int getNearStrongPower(Level level, BlockPos pos) {
        BlockPos posDown = pos.below();
        Block block = level.getBlockState(posDown).getBlock();
        if (block == ModBlocks.weakRedstoneTorch || block == ModBlocks.weakRedstoneTorch.wallTorch) {
            int power = level.getDirectSignal(pos.above(), Direction.UP);

            if (power >= 15) {
                return power;
            } else {
                power = Math.max(power, level.getDirectSignal(pos.north(), Direction.NORTH));
                if (power >= 15) {
                    return power;
                } else {
                    power = Math.max(power, level.getDirectSignal(pos.south(), Direction.SOUTH));
                    if (power >= 15) {
                        return power;
                    } else {
                        power = Math.max(power, level.getDirectSignal(pos.west(), Direction.WEST));
                        if (power >= 15) {
                            return power;
                        } else {
                            power = Math.max(power, level.getDirectSignal(pos.east(), Direction.EAST));
                            return power;
                        }
                    }
                }
            }
        } else {
            return level.getDirectSignalTo(pos);
        }
    }
}
