package de.melanx.utilitix.mixin;

import de.melanx.utilitix.content.redstone.ComparatorRedirector;
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
            Level level = (Level) (Object) this;

            BlockState up = level.getBlockState(pos.above());
            if (up.getBlock() instanceof ComparatorRedirector) {
                level.updateNeighbourForOutputSignal(pos.above(), up.getBlock());
            }

            BlockState down = level.getBlockState(pos.below());
            if (down.getBlock() instanceof ComparatorRedirector) {
                level.updateNeighbourForOutputSignal(pos.below(), down.getBlock());
            }
        }
    }
}
