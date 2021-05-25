package de.melanx.utilitix.network;

import io.github.noeppi_noeppi.libx.network.PacketSerializer;
import net.minecraft.network.PacketBuffer;

public class ItemEntityRepairedSerializer implements PacketSerializer<ItemEntityRepairedSerializer.ItemEntityRepairedMessage> {

    @Override
    public Class<ItemEntityRepairedMessage> messageClass() {
        return ItemEntityRepairedMessage.class;
    }

    @Override
    public void encode(ItemEntityRepairedMessage msg, PacketBuffer buffer) {
        buffer.writeInt(msg.id);
    }

    @Override
    public ItemEntityRepairedMessage decode(PacketBuffer buffer) {
        return new ItemEntityRepairedMessage(buffer.readInt());
    }

    public static class ItemEntityRepairedMessage {

        public final int id;

        public ItemEntityRepairedMessage(int id) {
            this.id = id;
        }
    }
}
