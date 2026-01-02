package de.melanx.utilitix.mixin;

import de.melanx.utilitix.config.CommonConfig;
import de.melanx.utilitix.content.glue.StickyChunk;
import de.melanx.utilitix.registration.ModAttachmentTypes;
import de.melanx.utilitix.util.MixinUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.piston.PistonStructureResolver;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(PistonStructureResolver.class)
public class MixinPistonStructureResolver {

    @Unique private boolean utilitix$hasGlueInThisMove = false;
    @Shadow @Final public Level level;
    @Shadow @Final private List<BlockPos> toDestroy;
    @Shadow @Final private List<BlockPos> toPush;

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

    @ModifyConstant(
            method = "addBlockLine",
            constant = @Constant(intValue = 12),
            require = 3
    )
    private int increaseBlockLimit(int original, BlockPos originPos) {
        return this.utilitix$shouldIncreaseLimit(originPos) ? CommonConfig.glueBlockLimit : original;
    }

    @Inject(
            method = "addBranchingBlocks",
            at = @At("HEAD"),
            cancellable = true
    )
    private void utilitix$addBranchingBlocks(BlockPos fromPos, CallbackInfoReturnable<Boolean> cir) {
        // We call this in any case. If it's a regular sticky block, do vanilla logic
        // if not, add direction-specific branching
        PistonStructureResolver pistonStructureResolver = (PistonStructureResolver) (Object) this;
        Level level = pistonStructureResolver.level;

        if (!level.getBlockState(fromPos).isStickyBlock()) {
            // We need our own logic here
            LevelChunk chunk = level.getChunkAt(fromPos);
            //noinspection ConstantConditions
            StickyChunk glue = chunk.getExistingDataOrNull(ModAttachmentTypes.stickyChunk);

            //noinspection ConstantConditions
            if (glue != null) {
                int x = fromPos.getX() & 0xF;
                int y = fromPos.getY();
                int z = fromPos.getZ() & 0xF;
                for (Direction dir : Direction.values()) {
                    if (glue.get(x, y, z, dir)) {
                        if (!MixinUtil.addDirectionBranchingBlocks(pistonStructureResolver, fromPos, dir)) {
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
    private boolean utilitix$shouldIncreaseLimit(BlockPos originPos) {
        if (this.utilitix$hasGlueInThisMove) {
            return true;
        }

        if (utilitix$hasAnyGlue(originPos)) {
            this.utilitix$hasGlueInThisMove = true;
            return true;
        }

        for (BlockPos pos : this.toPush) {
            if (utilitix$hasAnyGlue(pos)) {
                this.utilitix$hasGlueInThisMove = true;
                return true;
            }
        }

        for (BlockPos pos : this.toDestroy) {
            if (utilitix$hasAnyGlue(pos)) {
                this.utilitix$hasGlueInThisMove = true;
                return true;
            }
        }

        return false;
    }

    @Unique
    private boolean utilitix$hasAnyGlue(BlockPos pos) {
        LevelChunk chunk = this.level.getChunkAt(pos);
        StickyChunk glue = chunk.getExistingDataOrNull(ModAttachmentTypes.stickyChunk);
        if (glue == null) {
            return false;
        }

        int lx = pos.getX() & 0xF;
        int lz = pos.getZ() & 0xF;
        int y = pos.getY();

        return glue.getAny(lx, y, lz);
    }
}
