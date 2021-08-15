//package de.melanx.utilitix.mixin;
//
//import de.melanx.utilitix.content.slime.SlimyCapability;
//import de.melanx.utilitix.content.slime.StickyChunk;
//import net.minecraft.world.level.block.state.BlockState;
//import net.minecraft.world.level.block.Blocks;
//import net.minecraft.world.level.block.piston.PistonStructureResolver;
//import net.minecraft.core.Direction;
//import net.minecraft.core.BlockPos;
//import net.minecraft.world.level.Level;
//import net.minecraft.world.level.chunk.LevelChunk;
//import org.spongepowered.asm.mixin.Mixin;
//import org.spongepowered.asm.mixin.Unique;
//import org.spongepowered.asm.mixin.injection.At;
//import org.spongepowered.asm.mixin.injection.Inject;
//import org.spongepowered.asm.mixin.injection.Redirect;
//import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
//
//@Mixin(PistonStructureResolver.class)
//public class MixinPistonBlockStructureHelper {
//
//    @Redirect(
//            method = "Lnet/minecraft/block/PistonBlockStructureHelper;canMove()Z",
//            at = @At(
//                    value = "INVOKE",
//                    target = "Lnet/minecraft/world/World;getBlockState(Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/block/BlockState;",
//                    ordinal = 1
//            )
//    )
//    private BlockState checkSticky1(Level level, BlockPos pos) {
//        return Blocks.SLIME_BLOCK.defaultBlockState();
//    }
//
//    @Redirect(
//            method = "Lnet/minecraft/block/PistonBlockStructureHelper;addBlockLine(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/util/Direction;)Z",
//            at = @At(
//                    value = "INVOKE",
//                    target = "Lnet/minecraft/world/World;getBlockState(Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/block/BlockState;",
//                    ordinal = 2
//            )
//    )
//    private BlockState checkSticky2(Level level, BlockPos pos) {
//        return Blocks.SLIME_BLOCK.defaultBlockState();
//    }
//
//    @Inject(
//            method = "Lnet/minecraft/block/PistonBlockStructureHelper;addBranchingBlocks(Lnet/minecraft/util/math/BlockPos;)Z",
//            at = @At("HEAD"),
//            cancellable = true
//    )
//    private void addBranchingBlocks(BlockPos fromPos, CallbackInfoReturnable<Boolean> cir) {
//        // We call this in any case. If it's a regular sticky block, do vanilla logic
//        // if not, add direction-specific branching
//        Level level = ((PistonStructureResolver) (Object) this).level;
//        if (!level.getBlockState(fromPos).isStickyBlock()) {
//            // We need our own logic here
//            LevelChunk chunk = world.getChunkAt(fromPos);
//            //noinspection ConstantConditions
//            StickyChunk glue = chunk.getCapability(SlimyCapability.STICKY_CHUNK).orElse(null);
//            //noinspection ConstantConditions
//            if (glue != null) {
//                int x = fromPos.getX() & 0xF;
//                int y = fromPos.getY();
//                int z = fromPos.getZ() & 0xF;
//                for (Direction dir : Direction.values()) {
//                    if (glue.get(x, y, z, dir)) {
//                        if (!this.addDirectionBranchingBlocks(fromPos, dir)) {
//                            cir.setReturnValue(false);
//                            return;
//                        }
//                    }
//                }
//            }
//            cir.setReturnValue(true);
//        }
//    }
//
//
//    @Unique
//    private boolean addDirectionBranchingBlocks(BlockPos fromPos, Direction dir) {
//        if (dir != ((PistonStructureResolver) (Object) this).pushDirection) {
//            BlockPos targetPos = fromPos.relative(dir);
//            //noinspection RedundantIfStatement
//            if (!((PistonStructureResolver) (Object) this).addBlockLine(targetPos, dir)) {
//                return false;
//            }
//        }
//        return true;
//    }
//}
