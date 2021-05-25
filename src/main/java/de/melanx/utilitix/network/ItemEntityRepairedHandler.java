package de.melanx.utilitix.network;

import net.minecraft.client.Minecraft;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class ItemEntityRepairedHandler {

    public static void handle(ItemEntityRepairedSerializer.ItemEntityRepairedMessage msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ClientWorld world = Minecraft.getInstance().world;
            if (world == null) return;
            Entity item = world.getEntityByID(msg.id);
            if (item instanceof ItemEntity) {
                ((ItemEntity) item).getItem().setDamage(0);
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
