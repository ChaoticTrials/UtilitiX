package de.melanx.utilitix.mixin;

import de.melanx.utilitix.util.MixinUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.piston.PistonStructureResolver;
import net.minecraft.world.level.block.state.BlockState;
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
        cir.setReturnValue(MixinUtil.addBranchingBlocks((PistonStructureResolver) (Object) this, fromPos));
    }
}
