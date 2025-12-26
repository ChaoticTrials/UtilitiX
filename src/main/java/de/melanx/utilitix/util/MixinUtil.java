package de.melanx.utilitix.util;

import de.melanx.utilitix.config.FeatureConfig;
import de.melanx.utilitix.content.glue.StickyChunk;
import de.melanx.utilitix.registration.ModAttachmentTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.piston.PistonStructureResolver;
import net.minecraft.world.level.chunk.LevelChunk;

public class MixinUtil {

    public static boolean addDirectionBranchingBlocks(PistonStructureResolver piston, BlockPos fromPos, Direction dir) {
        if (dir == piston.pushDirection) {
            return true;
        }

        BlockPos targetPos = fromPos.relative(dir);
        return piston.addBlockLine(targetPos, dir);
    }

    public static void afterSetBlockState(Level level, BlockPos pos, Byte glueData) {
        if (level == null || pos == null || glueData == null || !FeatureConfig.Misc.InWorldChanges.glue) {
            return;
        }

        LevelChunk chunk = level.getChunkAt(pos);
        // Sticky data is stored as an attachment on the chunk, not on the level.
        // Create it if needed (glueData originates from a previously glued block).
        StickyChunk glue = chunk.getData(ModAttachmentTypes.stickyChunk);

        glue.attach(chunk);
        int x = pos.getX() & 0xF;
        int y = pos.getY();
        int z = pos.getZ() & 0xF;
        glue.setData(x, y, z, glueData);
        chunk.setUnsaved(true);
    }
}
