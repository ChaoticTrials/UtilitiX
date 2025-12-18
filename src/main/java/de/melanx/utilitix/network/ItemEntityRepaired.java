package de.melanx.utilitix.network;

import de.melanx.utilitix.UtilitiX;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.network.registration.HandlerThread;
import org.moddingx.libx.network.PacketHandler;

import javax.annotation.Nonnull;

public class ItemEntityRepaired extends PacketHandler<ItemEntityRepaired.Message> {

    public static final CustomPacketPayload.Type<ItemEntityRepaired.Message> TYPE = new CustomPacketPayload.Type<>(UtilitiX.getInstance().resource("item_entity_repaired"));

    protected ItemEntityRepaired() {
        super(TYPE, PacketFlow.CLIENTBOUND, Message.CODEC, HandlerThread.MAIN);
    }

    @Override
    public void handle(Message msg, IPayloadContext ctx) {
        ClientLevel level = Minecraft.getInstance().level;
        if (level == null) {
            return;
        }

        Entity item = level.getEntity(msg.id());
        if (item instanceof ItemEntity) {
            ((ItemEntity) item).getItem().setDamageValue(0);
        }
    }

    public record Message(int id) implements CustomPacketPayload {

        public static final StreamCodec<RegistryFriendlyByteBuf, Message> CODEC = StreamCodec.of(
                (buffer, msg) -> {
                    buffer.writeInt(msg.id);
                }, buffer -> new Message(buffer.readInt())
        );

        @Nonnull
        @Override
        public Type<? extends CustomPacketPayload> type() {
            return ItemEntityRepaired.TYPE;
        }
    }
}
