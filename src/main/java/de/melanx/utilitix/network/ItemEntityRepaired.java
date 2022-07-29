package de.melanx.utilitix.network;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraftforge.network.NetworkEvent;
import org.moddingx.libx.network.PacketHandler;
import org.moddingx.libx.network.PacketSerializer;

import java.util.function.Supplier;

public record ItemEntityRepaired(int id) {

    public static class Handler implements PacketHandler<ItemEntityRepaired> {

        @Override
        public Target target() {
            return Target.MAIN_THREAD;
        }

        @Override
        public boolean handle(ItemEntityRepaired msg, Supplier<NetworkEvent.Context> ctx) {
            ClientLevel level = Minecraft.getInstance().level;
            if (level == null) return true;
            Entity item = level.getEntity(msg.id());
            if (item instanceof ItemEntity) {
                ((ItemEntity) item).getItem().setDamageValue(0);
            }

            return true;
        }
    }

    public static class Serializer implements PacketSerializer<ItemEntityRepaired> {

        @Override
        public Class<ItemEntityRepaired> messageClass() {
            return ItemEntityRepaired.class;
        }

        @Override
        public void encode(ItemEntityRepaired msg, FriendlyByteBuf buffer) {
            buffer.writeInt(msg.id);
        }

        @Override
        public ItemEntityRepaired decode(FriendlyByteBuf buffer) {
            return new ItemEntityRepaired(buffer.readInt());
        }
    }
}
