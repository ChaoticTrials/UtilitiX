package de.melanx.utilitix.network;

import io.github.noeppi_noeppi.libx.network.PacketSerializer;
import net.minecraft.network.FriendlyByteBuf;

public class PistonCartModeCycleSerializer implements PacketSerializer<PistonCartModeCycleSerializer.PistonCartModeCycleMessage> {

    @Override
    public Class<PistonCartModeCycleMessage> messageClass() {
        return PistonCartModeCycleMessage.class;
    }

    @Override
    public void encode(PistonCartModeCycleMessage msg, FriendlyByteBuf buffer) {
        buffer.writeInt(msg.id);
    }

    @Override
    public PistonCartModeCycleMessage decode(FriendlyByteBuf buffer) {
        return new PistonCartModeCycleMessage(buffer.readInt());
    }

    public record PistonCartModeCycleMessage(int id) {
        // record
    }
}
