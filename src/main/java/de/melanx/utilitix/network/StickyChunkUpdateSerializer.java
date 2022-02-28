package de.melanx.utilitix.network;

import de.melanx.utilitix.content.slime.StickyChunk;
import io.github.noeppi_noeppi.libx.network.PacketSerializer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.ChunkPos;

public class StickyChunkUpdateSerializer implements PacketSerializer<StickyChunkUpdateSerializer.StickyChunkUpdateMessage> {

    @Override
    public Class<StickyChunkUpdateMessage> messageClass() {
        return StickyChunkUpdateMessage.class;
    }

    @Override
    public void encode(StickyChunkUpdateMessage msg, FriendlyByteBuf buffer) {
        buffer.writeInt(msg.pos().x);
        buffer.writeInt(msg.pos().z);
        msg.data().write(buffer);
    }

    @Override
    public StickyChunkUpdateMessage decode(FriendlyByteBuf buffer) {
        ChunkPos pos = new ChunkPos(buffer.readInt(), buffer.readInt());
        StickyChunk chunk = new StickyChunk();
        chunk.read(buffer);
        return new StickyChunkUpdateMessage(pos, chunk);
    }

    public record StickyChunkUpdateMessage(ChunkPos pos, StickyChunk data) {
        // record
    }
}
