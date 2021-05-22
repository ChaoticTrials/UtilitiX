package de.melanx.utilitix.mixin;

import de.melanx.utilitix.registration.ModBlocks;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(World.class)
public class MixinWorld {
    @Redirect(
            method = "getRedstonePowerFromNeighbors(Lnet/minecraft/util/math/BlockPos;)I",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/World;getRedstonePower(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/util/Direction;)I"
            )
    )
    public int getRedstonePowerAsCallFromNeighbours(World world, BlockPos pos, Direction facing) {
        int power = 0;
        BlockState state = world.getBlockState(pos);
        if (state.getWeakPower(world, pos, facing) > 0) {
            power = world.getRedstonePower(pos, facing);
        }

        if (state.shouldCheckWeakPower(world, pos, facing)) {
            power = Math.max(power, this.getNearStrongPower(world, pos));
        }

        return power;
    }

    public int getNearStrongPower(World world, BlockPos pos) {
        BlockPos posDown = pos.down();
        Block block = world.getBlockState(posDown).getBlock();
        if (block == ModBlocks.weakRedstoneTorch || block == ModBlocks.weakRedstoneTorch.wallTorch) {
            int power = world.getStrongPower(pos.up(), Direction.UP);

            if (power >= 15) {
                return power;
            } else {
                power = Math.max(power, world.getStrongPower(pos.north(), Direction.NORTH));
                if (power >= 15) {
                    return power;
                } else {
                    power = Math.max(power, world.getStrongPower(pos.south(), Direction.SOUTH));
                    if (power >= 15) {
                        return power;
                    } else {
                        power = Math.max(power, world.getStrongPower(pos.west(), Direction.WEST));
                        if (power >= 15) {
                            return power;
                        } else {
                            power = Math.max(power, world.getStrongPower(pos.east(), Direction.EAST));
                            return power >= 15 ? power : power;
                        }
                    }
                }
            }
        } else {
            return world.getStrongPower(pos);
        }
    }
}
