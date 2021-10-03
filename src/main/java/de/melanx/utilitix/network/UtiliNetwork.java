package de.melanx.utilitix.network;

import io.github.noeppi_noeppi.libx.mod.ModX;
import io.github.noeppi_noeppi.libx.network.NetworkX;
import net.minecraftforge.fmllegacy.network.NetworkDirection;

public class UtiliNetwork extends NetworkX {

    public UtiliNetwork(ModX mod) {
        super(mod);
    }

    @Override
    protected Protocol getProtocol() {
        return Protocol.of("5");
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
