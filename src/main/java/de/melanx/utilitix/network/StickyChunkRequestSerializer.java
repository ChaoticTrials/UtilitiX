package de.melanx.utilitix.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.ChunkPos;
import org.moddingx.libx.network.PacketSerializer;

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
