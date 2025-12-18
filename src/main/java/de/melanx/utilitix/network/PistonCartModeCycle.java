package de.melanx.utilitix.network;

import de.melanx.utilitix.UtilitiX;
import de.melanx.utilitix.content.track.carts.PistonCart;
import de.melanx.utilitix.content.track.carts.piston.PistonCartMode;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.network.registration.HandlerThread;
import org.moddingx.libx.network.PacketHandler;

import javax.annotation.Nonnull;

public class PistonCartModeCycle extends PacketHandler<PistonCartModeCycle.Message> {

    public static final CustomPacketPayload.Type<PistonCartModeCycle.Message> TYPE = new CustomPacketPayload.Type<>(UtilitiX.getInstance().resource("piston_cart_mode_cycle"));

    public PistonCartModeCycle() {
        super(TYPE, PacketFlow.SERVERBOUND, PistonCartModeCycle.Message.CODEC, HandlerThread.MAIN);
    }

    @Override
    public void handle(Message msg, IPayloadContext ctx) {
        if (!(ctx.player() instanceof ServerPlayer sender)) {
            return;
        }

        Entity entity = sender.level().getEntity(msg.id());
        if (entity instanceof PistonCart) {
            int modeIdx = ((PistonCart) entity).getMode().ordinal();
            PistonCartMode[] modes = PistonCartMode.values();
            ((PistonCart) entity).setMode(modes[(modeIdx + 1) % modes.length]);
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
            return PistonCartModeCycle.TYPE;
        }
    }
}
