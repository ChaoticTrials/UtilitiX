package de.melanx.utilitix.network;

import de.melanx.utilitix.content.track.carts.PistonCart;
import de.melanx.utilitix.content.track.carts.piston.PistonCartMode;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.network.NetworkEvent;
import org.moddingx.libx.network.PacketHandler;
import org.moddingx.libx.network.PacketSerializer;

import java.util.function.Supplier;

public record PistonCartModeCycle(int id) {

    public static class Handler implements PacketHandler<PistonCartModeCycle> {

        @Override
        public Target target() {
            return Target.MAIN_THREAD;
        }

        @Override
        public boolean handle(PistonCartModeCycle msg, Supplier<NetworkEvent.Context> ctx) {
            ServerPlayer sender = ctx.get().getSender();
            if (sender != null) {
                Entity entity = sender.getLevel().getEntity(msg.id());
                if (entity instanceof PistonCart) {
                    int modeIdx = ((PistonCart) entity).getMode().ordinal();
                    PistonCartMode[] modes = PistonCartMode.values();
                    ((PistonCart) entity).setMode(modes[(modeIdx + 1) % modes.length]);
                }
            }

            return true;
        }
    }

    public static class Serializer implements PacketSerializer<PistonCartModeCycle> {

        @Override
        public Class<PistonCartModeCycle> messageClass() {
            return PistonCartModeCycle.class;
        }

        @Override
        public void encode(PistonCartModeCycle msg, FriendlyByteBuf buffer) {
            buffer.writeInt(msg.id);
        }

        @Override
        public PistonCartModeCycle decode(FriendlyByteBuf buffer) {
            return new PistonCartModeCycle(buffer.readInt());
        }
    }
}
