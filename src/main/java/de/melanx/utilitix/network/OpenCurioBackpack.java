package de.melanx.utilitix.network;

import de.melanx.utilitix.compat.curios.UtilCurios;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import org.moddingx.libx.network.PacketHandler;
import org.moddingx.libx.network.PacketSerializer;

import java.util.function.Supplier;

public record OpenCurioBackpack() {

    public static class Handler implements PacketHandler<OpenCurioBackpack> {

        @Override
        public Target target() {
            return Target.MAIN_THREAD;
        }

        @Override
        public boolean handle(OpenCurioBackpack msg, Supplier<NetworkEvent.Context> ctx) {
            ctx.get().enqueueWork(() -> {
                ServerPlayer sender = ctx.get().getSender();
                UtilCurios.openBackpack(sender);
            });
            return true;
        }
    }

    public static class Serializer implements PacketSerializer<OpenCurioBackpack> {

        @Override
        public Class<OpenCurioBackpack> messageClass() {
            return OpenCurioBackpack.class;
        }

        @Override
        public void encode(OpenCurioBackpack msg, FriendlyByteBuf buffer) {

        }

        @Override
        public OpenCurioBackpack decode(FriendlyByteBuf buffer) {
            return new OpenCurioBackpack();
        }
    }
}
