package de.melanx.utilitix.client;

import de.melanx.utilitix.content.bell.ItemMobBell;
import de.melanx.utilitix.content.slime.SlimeRender;
import de.melanx.utilitix.registration.ModItems;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;
import net.neoforged.neoforge.common.NeoForge;

public class ClientUtilitiX {

    public ClientUtilitiX(IEventBus modBus) {
        modBus.addListener(this::registerItemColors);

        NeoForge.EVENT_BUS.addListener(SlimeRender::renderWorld);
    }

    private void registerItemColors(RegisterColorHandlersEvent.Item event) {
        event.register((stack, tintIndex) -> tintIndex == 1 ? 0xFF000000 | ItemMobBell.getColor(stack) : 0xFFFFFFFF, ModItems.mobBell);
    }
}
