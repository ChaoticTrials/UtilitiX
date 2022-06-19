package de.melanx.utilitix.network;

import net.minecraftforge.network.NetworkDirection;
import org.moddingx.libx.mod.ModX;
import org.moddingx.libx.network.NetworkX;

public class UtiliNetwork extends NetworkX {

    public UtiliNetwork(ModX mod) {
        super(mod);
    }

    @Override
    protected Protocol getProtocol() {
        return Protocol.of("6");
    }

    @Override
    protected void registerPackets() {
        this.register(new StickyChunkRequestSerializer(), () -> StickyChunkRequestHandler::handle, NetworkDirection.PLAY_TO_SERVER);
        this.register(new PistonCartModeCycleSerializer(), () -> PistonCartModeCycleHandler::handle, NetworkDirection.PLAY_TO_SERVER);
        this.register(new ClickScreenButtonHandler.ClickScreenButtonSerializer(), () -> ClickScreenButtonHandler::handle, NetworkDirection.PLAY_TO_SERVER);
        
        this.register(new StickyChunkUpdateSerializer(), () -> StickyChunkUpdateHandler::handle, NetworkDirection.PLAY_TO_CLIENT);
        this.register(new ItemEntityRepairedSerializer(), () -> ItemEntityRepairedHandler::handle, NetworkDirection.PLAY_TO_CLIENT);
    }
}
