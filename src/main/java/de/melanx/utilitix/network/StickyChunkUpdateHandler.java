package de.melanx.utilitix.network;

import de.melanx.utilitix.UtilitiX;
import de.melanx.utilitix.content.slime.SlimyCapability;
import de.melanx.utilitix.content.slime.StickyChunk;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class StickyChunkUpdateHandler {

    public static void handle(StickyChunkUpdateSerializer.StickyChunkUpdateMessage msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            Level level = Minecraft.getInstance().level;
            if (level != null) {
                //noinspection deprecation
                if (level.hasChunkAt(new BlockPos(msg.pos().getMinBlockX(), 0, msg.pos().getMinBlockZ()))) {
                    LevelChunk loaded = level.getChunk(msg.pos().x, msg.pos().z);
                    //noinspection ConstantConditions
                    if (loaded != null) {
                        //noinspection ConstantConditions
                        StickyChunk glue = loaded.getCapability(SlimyCapability.STICKY_CHUNK).orElse(null);
                        //noinspection ConstantConditions
                        if (glue != null) {
                            glue.loadFrom(msg.data());
                            return;
                        }
                    }
                }
            }
            UtilitiX.getInstance().logger.warn("Received invalid sticky chunk packet for unloaded chunk: (" + msg.pos().x + "," + msg.pos().z + ")");
        });
        ctx.get().setPacketHandled(true);
    }
}
