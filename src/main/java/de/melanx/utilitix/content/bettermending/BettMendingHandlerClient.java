package de.melanx.utilitix.content.bettermending;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;

@EventBusSubscriber(value = Dist.CLIENT)
public class BettMendingHandlerClient {

    @SubscribeEvent
    public static void pullXPClient(ClientTickEvent.Post event) {
        if (Minecraft.getInstance().level != null) {
            ClientLevel level = Minecraft.getInstance().level;
            BetterMendingHandler.moveExps(level, level.entitiesForRendering());
        }
    }
}
