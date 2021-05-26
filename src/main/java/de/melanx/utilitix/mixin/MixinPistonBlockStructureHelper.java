package de.melanx.utilitix.mixin;

import de.melanx.utilitix.module.slime.SlimyCapability;
import de.melanx.utilitix.module.slime.StickyChunk;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.PistonBlockStructureHelper;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PistonBlockStructureHelper.class)
public class MixinPistonBlockStructureHelper {
    
    @Redirect(
            method = "Lnet/minecraft/block/PistonBlockStructureHelper;canMove()Z",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/World;getBlockState(Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/block/BlockState;",
                    ordinal = 1
            )
    )
    private BlockState checkSticky1(World world, BlockPos pos) {
        return Blocks.SLIME_BLOCK.getDefaultState();
    }
    
    @Redirect(
            method = "Lnet/minecraft/block/PistonBlockStructureHelper;addBlockLine(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/util/Direction;)Z",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/World;getBlockState(Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/block/BlockState;",
                    ordinal = 2
            )
    )
    private BlockState checkSticky2(World world, BlockPos pos) {
        return Blocks.SLIME_BLOCK.getDefaultState();
    }
    
    @Inject(
            method = "Lnet/minecraft/block/PistonBlockStructureHelper;addBranchingBlocks(Lnet/minecraft/util/math/BlockPos;)Z",
            at = @At("HEAD"),
            cancellable = true
    )
    private void addBranchingBlocks(BlockPos fromPos, CallbackInfoReturnable<Boolean> cir) {
        // We call this in any case. If it's a regular sticky block, do vanilla logic
        // if not, add direction-specific branching
        World world = ((PistonBlockStructureHelper) (Object) this).world;
        if (!world.getBlockState(fromPos).isStickyBlock()) {
            // We need our own logic here
            Chunk chunk = world.getChunkAt(fromPos);
            //noinspection ConstantConditions
            StickyChunk glue = chunk.getCapability(SlimyCapability.STICKY_CHUNK).orElse(null);
            //noinspection ConstantConditions
            if (glue != null) {
                int x = fromPos.getX() & 0xF;
                int y = fromPos.getY();
                int z = fromPos.getZ() & 0xF;
                for (Direction dir : Direction.values()) {
                    if (glue.get(x, y, z, dir)) {
                        if (!this.addDirectionBranchingBlocks(fromPos, dir)) {
                            cir.setReturnValue(false);
                            return;
                        }
                    }
                }
            }
            cir.setReturnValue(true);
        }
    }
    
    
    @Unique
    private boolean addDirectionBranchingBlocks(BlockPos fromPos, Direction dir) {
        if (dir != ((PistonBlockStructureHelper) (Object) this).moveDirection) {
            BlockPos targetPos = fromPos.offset(dir);
            //noinspection RedundantIfStatement
            if (!((PistonBlockStructureHelper) (Object) this).addBlockLine(targetPos, dir)) {
                return false;
            }
        }
        return true;
    }
}
