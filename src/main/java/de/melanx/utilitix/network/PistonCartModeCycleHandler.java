package de.melanx.utilitix.network;

import de.melanx.utilitix.content.track.carts.EntityPistonCart;
import de.melanx.utilitix.content.track.carts.piston.PistonCartMode;
import net.minecraft.client.Minecraft;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class PistonCartModeCycleHandler {

    public static void handle(PistonCartModeCycleSerializer.PistonCartModeCycleMessage msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayerEntity sender = ctx.get().getSender();
            if (sender != null) {
                Entity entity = sender.getServerWorld().getEntityByID(msg.id);
                if (entity instanceof EntityPistonCart) {
                    int modeIdx = ((EntityPistonCart) entity).getMode().ordinal();
                    PistonCartMode[] modes = PistonCartMode.values();
                    ((EntityPistonCart) entity).setMode(modes[(modeIdx + 1) % modes.length]);
                }
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
