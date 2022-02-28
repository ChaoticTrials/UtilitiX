package de.melanx.utilitix.util;

import de.melanx.utilitix.content.slime.SlimyCapability;
import de.melanx.utilitix.content.slime.StickyChunk;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.piston.PistonStructureResolver;
import net.minecraft.world.level.chunk.LevelChunk;

public class MixinUtil {

    public static boolean addBranchingBlocks(PistonStructureResolver piston, BlockPos fromPos) {
        // We call this in any case. If it's a regular sticky block, do vanilla logic
        // if not, add direction-specific branching
        Level level = piston.level;
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
                        if (!MixinUtil.addDirectionBranchingBlocks(piston, fromPos, dir)) {
                            return false;
                        }
                    }
                }
            }

            return true;
        }

        return false;
    }

    private static boolean addDirectionBranchingBlocks(PistonStructureResolver piston, BlockPos fromPos, Direction dir) {
        if (dir != piston.pushDirection) {
            BlockPos targetPos = fromPos.relative(dir);
            //noinspection RedundantIfStatement
            if (!piston.addBlockLine(targetPos, dir)) {
                return false;
            }
        }

        return true;
    }

    public static void afterSetBlockState(Level level, BlockPos pos, Byte glueData) {
        if (level != null && pos != null && glueData != null) {
            LevelChunk chunk = level.getChunkAt(pos);
            //noinspection ConstantConditions
            StickyChunk glue = chunk.getCapability(SlimyCapability.STICKY_CHUNK).orElse(null);
            //noinspection ConstantConditions
            if (glue != null) {
                int x = pos.getX() & 0xF;
                int y = pos.getY();
                int z = pos.getZ() & 0xF;
                glue.setData(x, y, z, glueData);
                chunk.setUnsaved(true);
            }
        }
    }
}
