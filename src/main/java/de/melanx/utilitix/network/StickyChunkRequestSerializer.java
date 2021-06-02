package de.melanx.utilitix.network;

import io.github.noeppi_noeppi.libx.network.PacketSerializer;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.ChunkPos;

public class StickyChunkRequestSerializer implements PacketSerializer<StickyChunkRequestSerializer.StickyChunkRequestMessage> {

    @Override
    public Class<StickyChunkRequestMessage> messageClass() {
        return StickyChunkRequestMessage.class;
    }

    @Override
    public void encode(StickyChunkRequestMessage msg, PacketBuffer buffer) {
        buffer.writeInt(msg.pos.x);
        buffer.writeInt(msg.pos.z);
    }

    @Override
    public StickyChunkRequestMessage decode(PacketBuffer buffer) {
        return new StickyChunkRequestMessage(new ChunkPos(buffer.readInt(), buffer.readInt()));
    }

    public static class StickyChunkRequestMessage {

        public final ChunkPos pos;

        public StickyChunkRequestMessage(ChunkPos pos) {
            this.pos = pos;
        }
    }
}
