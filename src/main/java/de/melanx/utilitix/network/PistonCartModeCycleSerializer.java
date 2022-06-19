package de.melanx.utilitix.network;

import net.minecraft.network.FriendlyByteBuf;
import org.moddingx.libx.network.PacketSerializer;

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
