package de.melanx.utilitix.block;

import net.minecraft.block.BlockState;
import net.minecraft.block.RedstoneTorchBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import java.util.Random;

public class WeakRedstoneTorch extends RedstoneTorchBlock {
    public WeakRedstoneTorch(Properties properties) {
        super(properties);
    }

    @Override
    public void animateTick(@Nonnull BlockState state, @Nonnull World world, @Nonnull BlockPos pos, @Nonnull Random rand) {
        // stop redstone particles
    }
}
