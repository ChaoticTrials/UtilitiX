package de.melanx.utilitix.mixin;

import de.melanx.utilitix.block.ComparatorRedirector;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
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
}
