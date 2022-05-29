package de.melanx.utilitix.mixin;

import de.melanx.utilitix.content.slime.SlimyCapability;
import de.melanx.utilitix.content.slime.StickyChunk;
import de.melanx.utilitix.util.MixinUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.piston.PistonStructureResolver;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PistonStructureResolver.class)
public class MixinPistonStructureResolver {

    @Redirect(
            method = "resolve",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/Level;getBlockState(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/block/state/BlockState;",
                    ordinal = 1
            )
    )
    private BlockState checkSticky1(Level level, BlockPos pos) {
        return Blocks.SLIME_BLOCK.defaultBlockState();
    }

    @Redirect(
            method = "addBlockLine",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/Level;getBlockState(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/block/state/BlockState;",
                    ordinal = 2
            )
    )
    private BlockState checkSticky2(Level level, BlockPos pos) {
        return Blocks.SLIME_BLOCK.defaultBlockState();
    }

    @Inject(
            method = "addBranchingBlocks",
            at = @At("HEAD"),
            cancellable = true
    )
    private void addBranchingBlocks(BlockPos fromPos, CallbackInfoReturnable<Boolean> cir) {
        // We call this in any case. If it's a regular sticky block, do vanilla logic
        // if not, add direction-specific branching
        Level level = ((PistonStructureResolver) (Object) this).level;
        if (!level.getBlockState(fromPos).isStickyBlock()) {
            // We need our own logic here
            LevelChunk chunk = level.getChunkAt(fromPos);
            //noinspection ConstantConditions
            StickyChunk glue = chunk.getCapability(SlimyCapability.STICKY_CHUNK).orElse(null);
            //noinspection ConstantConditions
            if (glue != null) {
                int x = fromPos.getX() & 0xF;
                int y = fromPos.getY();
                int z = fromPos.getZ() & 0xF;
                for (Direction dir : Direction.values()) {
                    if (glue.get(x, y, z, dir)) {
                        if (!MixinUtil.addDirectionBranchingBlocks((PistonStructureResolver) (Object) this, fromPos, dir)) {
                            cir.setReturnValue(false);
                            return;
                        }
                    }
                }
            }
            cir.setReturnValue(true);
        }
    }
}
