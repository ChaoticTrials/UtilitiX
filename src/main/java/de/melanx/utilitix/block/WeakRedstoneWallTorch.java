package de.melanx.utilitix.block;

import de.melanx.utilitix.registration.ModBlocks;
import net.minecraft.block.BlockState;
import net.minecraft.block.RedstoneWallTorchBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import java.util.Random;

public class WeakRedstoneWallTorch extends RedstoneWallTorchBlock {

    public WeakRedstoneWallTorch(Properties properties) {
        super(properties);
    }

    @Override
    public ItemStack getPickBlock(BlockState state, RayTraceResult target, IBlockReader world, BlockPos pos, PlayerEntity player) {
        return new ItemStack(ModBlocks.weakRedstoneTorch);
    }

    @Override
    public void animateTick(@Nonnull BlockState state, @Nonnull World world, @Nonnull BlockPos pos, @Nonnull Random rand) {
        // stop redstone particles
    }
}
