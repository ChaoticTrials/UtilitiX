package de.melanx.utilitix.client;

import de.melanx.utilitix.content.glue.StickyRenderHelper;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.NeoForge;

public class ClientUtilitiX {

    public ClientUtilitiX(IEventBus modBus) {
        NeoForge.EVENT_BUS.addListener(StickyRenderHelper::renderWorld);
    }
}
