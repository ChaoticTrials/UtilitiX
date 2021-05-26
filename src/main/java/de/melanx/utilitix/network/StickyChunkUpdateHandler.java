package de.melanx.utilitix.network;

import de.melanx.utilitix.UtilitiX;
import de.melanx.utilitix.content.slime.SlimyCapability;
import de.melanx.utilitix.content.slime.StickyChunk;
import net.minecraft.client.Minecraft;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class StickyChunkUpdateHandler {

    public static void handle(StickyChunkUpdateSerializer.StickyChunkUpdateMessage msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            World world = Minecraft.getInstance().world;
            if (world != null) {
                //noinspection deprecation
                if (world.isBlockLoaded(new BlockPos(msg.pos.getXStart(), 0, msg.pos.getZStart()))) {
                    Chunk loaded = world.getChunk(msg.pos.x, msg.pos.z);
                    //noinspection ConstantConditions
                    if (loaded != null) {
                        //noinspection ConstantConditions
                        StickyChunk glue = loaded.getCapability(SlimyCapability.STICKY_CHUNK).orElse(null);
                        //noinspection ConstantConditions
                        if (glue != null) {
                            glue.setStickies(msg.data.getStickies());
                            return;
                        }
                    }
                }
            }
            UtilitiX.getInstance().logger.warn("Received invalid sticky chunk packet for unloaded chunk: (" + msg.pos.x + "," + msg.pos.z + ")");
        });
        ctx.get().setPacketHandled(true);
    }
}
