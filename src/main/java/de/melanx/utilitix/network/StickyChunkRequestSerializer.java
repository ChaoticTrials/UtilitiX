package de.melanx.utilitix.network;

import io.github.noeppi_noeppi.libx.network.PacketSerializer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.ChunkPos;

public class StickyChunkRequestSerializer implements PacketSerializer<StickyChunkRequestSerializer.StickyChunkRequestMessage> {

    @Override
    public Class<StickyChunkRequestMessage> messageClass() {
        return StickyChunkRequestMessage.class;
    }

    @Override
    public void encode(StickyChunkRequestMessage msg, FriendlyByteBuf buffer) {
        buffer.writeInt(msg.pos.x);
        buffer.writeInt(msg.pos.z);
    }

    @Override
    public StickyChunkRequestMessage decode(FriendlyByteBuf buffer) {
        return new StickyChunkRequestMessage(new ChunkPos(buffer.readInt(), buffer.readInt()));
    }

    public record StickyChunkRequestMessage(ChunkPos pos) {
        // record
    }
}
