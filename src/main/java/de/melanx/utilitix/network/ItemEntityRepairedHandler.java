package de.melanx.utilitix.network;

import com.google.common.collect.Streams;
import io.github.noeppi_noeppi.libx.network.PacketSerializer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.Optional;
import java.util.UUID;
import java.util.function.Supplier;

public class ItemEntityRepairedHandler {

    public static void handle(Message msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ClientWorld world = Minecraft.getInstance().world;
            if (world == null) return;
            //noinspection UnstableApiUsage
            Optional<ItemEntity> item = Streams.stream(world.getAllEntities())
                    .filter(entity -> entity.getUniqueID().equals(msg.id))
                    .map(entity -> (ItemEntity) entity)
                    .findFirst();

            if (!item.isPresent()) return;
            ItemEntity entity = item.get();
            entity.getItem().setDamage(0);
        });
        ctx.get().setPacketHandled(true);
    }

    public static class Serializer implements PacketSerializer<Message> {

        @Override
        public Class<Message> messageClass() {
            return Message.class;
        }

        @Override
        public void encode(Message message, PacketBuffer buffer) {
            buffer.writeUniqueId(message.id);
        }

        @Override
        public Message decode(PacketBuffer buffer) {
            return new Message(buffer.readUniqueId());
        }
    }

    public static class Message {

        public final UUID id;

        public Message(UUID id) {
            this.id = id;
        }
    }
}
