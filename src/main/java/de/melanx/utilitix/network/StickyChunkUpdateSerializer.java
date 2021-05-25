package de.melanx.utilitix.network;

import de.melanx.utilitix.module.slime.StickyChunk;
import io.github.noeppi_noeppi.libx.network.PacketSerializer;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.ChunkPos;

public class StickyChunkUpdateSerializer implements PacketSerializer<StickyChunkUpdateSerializer.StickyChunkUpdateMessage> {
    
    @Override
    public Class<StickyChunkUpdateMessage> messageClass() {
        return StickyChunkUpdateMessage.class;
    }

    @Override
    public void encode(StickyChunkUpdateMessage msg, PacketBuffer buffer) {
        buffer.writeInt(msg.pos.x);
        buffer.writeInt(msg.pos.z);
        buffer.writeByteArray(msg.data.getStickies());
    }

    @Override
    public StickyChunkUpdateMessage decode(PacketBuffer buffer) {
        ChunkPos pos = new ChunkPos(buffer.readInt(), buffer.readInt());
        StickyChunk chunk = new StickyChunk();
        chunk.setStickies(buffer.readByteArray());
        return new StickyChunkUpdateMessage(pos, chunk);
    }

    public static class StickyChunkUpdateMessage {
        
        public final ChunkPos pos;
        public final StickyChunk data;

        public StickyChunkUpdateMessage(ChunkPos pos, StickyChunk data) {
            this.pos = pos;
            this.data = data;
        }
    }
}
