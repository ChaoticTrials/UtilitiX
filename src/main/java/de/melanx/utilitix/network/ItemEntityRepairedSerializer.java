package de.melanx.utilitix.network;

import net.minecraft.network.FriendlyByteBuf;
import org.moddingx.libx.network.PacketSerializer;

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
