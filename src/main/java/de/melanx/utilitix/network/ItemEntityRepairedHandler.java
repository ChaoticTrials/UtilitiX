package de.melanx.utilitix.network;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class ItemEntityRepairedHandler {

    public static void handle(ItemEntityRepairedSerializer.ItemEntityRepairedMessage msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ClientLevel level = Minecraft.getInstance().level;
            if (level == null) return;
            Entity item = level.getEntity(msg.id());
            if (item instanceof ItemEntity) {
                ((ItemEntity) item).getItem().setDamageValue(0);
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
