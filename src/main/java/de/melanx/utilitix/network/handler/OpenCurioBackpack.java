package de.melanx.utilitix.network.handler;

import de.melanx.utilitix.UtilitiX;
import de.melanx.utilitix.compat.curios.UtilCurios;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.network.registration.HandlerThread;
import org.moddingx.libx.network.PacketHandler;

import javax.annotation.Nonnull;

public class OpenCurioBackpack extends PacketHandler<OpenCurioBackpack.Message> {

    public static final CustomPacketPayload.Type<OpenCurioBackpack.Message> TYPE = new CustomPacketPayload.Type<>(UtilitiX.getInstance().resource("open_curio_backpack"));

    public OpenCurioBackpack() {
        super(TYPE, PacketFlow.SERVERBOUND, OpenCurioBackpack.Message.CODEC, HandlerThread.MAIN);
    }

    @Override
    public void handle(Message msg, IPayloadContext ctx) {
        if (ctx.player() instanceof ServerPlayer player) {
            UtilCurios.openBackpack(player);
        }
    }

    public record Message() implements CustomPacketPayload {

        public static final StreamCodec<RegistryFriendlyByteBuf, Message> CODEC = StreamCodec.unit(new Message());

        @Nonnull
        @Override
        public Type<? extends CustomPacketPayload> type() {
            return OpenCurioBackpack.TYPE;
        }
    }
}
