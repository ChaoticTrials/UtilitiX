package de.melanx.utilitix.network;

import io.github.noeppi_noeppi.libx.network.PacketSerializer;
import net.minecraft.network.FriendlyByteBuf;

public class ItemEntityRepairedSerializer implements PacketSerializer<ItemEntityRepairedSerializer.ItemEntityRepairedMessage> {

    @Override
    public Class<ItemEntityRepairedMessage> messageClass() {
        return ItemEntityRepairedMessage.class;
    }

    @Override
    public void encode(ItemEntityRepairedMessage msg, FriendlyByteBuf buffer) {
        buffer.writeInt(msg.id);
    }

    @Override
    public ItemEntityRepairedMessage decode(FriendlyByteBuf buffer) {
        return new ItemEntityRepairedMessage(buffer.readInt());
    }

    public record ItemEntityRepairedMessage(int id) {
        // record
    }
}
