package de.melanx.utilitix.network;

import io.github.noeppi_noeppi.libx.network.PacketSerializer;
import net.minecraft.network.PacketBuffer;

public class PistonCartModeCycleSerializer implements PacketSerializer<PistonCartModeCycleSerializer.PistonCartModeCycleMessage> {

    @Override
    public Class<PistonCartModeCycleMessage> messageClass() {
        return PistonCartModeCycleMessage.class;
    }

    @Override
    public void encode(PistonCartModeCycleMessage msg, PacketBuffer buffer) {
        buffer.writeInt(msg.id);
    }

    @Override
    public PistonCartModeCycleMessage decode(PacketBuffer buffer) {
        return new PistonCartModeCycleMessage(buffer.readInt());
    }

    public static class PistonCartModeCycleMessage {

        public final int id;

        public PistonCartModeCycleMessage(int id) {
            this.id = id;
        }
    }
}
