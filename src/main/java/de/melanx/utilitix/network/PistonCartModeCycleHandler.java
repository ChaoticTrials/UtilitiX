package de.melanx.utilitix.network;

import de.melanx.utilitix.content.track.carts.PistonCart;
import de.melanx.utilitix.content.track.carts.piston.PistonCartMode;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class PistonCartModeCycleHandler {

    public static void handle(PistonCartModeCycleSerializer.PistonCartModeCycleMessage msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer sender = ctx.get().getSender();
            if (sender != null) {
                Entity entity = sender.getLevel().getEntity(msg.id());
                if (entity instanceof PistonCart) {
                    int modeIdx = ((PistonCart) entity).getMode().ordinal();
                    PistonCartMode[] modes = PistonCartMode.values();
                    ((PistonCart) entity).setMode(modes[(modeIdx + 1) % modes.length]);
                }
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
