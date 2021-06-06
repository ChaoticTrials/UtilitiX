package de.melanx.utilitix.network;

import io.github.noeppi_noeppi.libx.mod.ModX;
import io.github.noeppi_noeppi.libx.network.NetworkX;
import net.minecraftforge.fml.network.NetworkDirection;

public class UtiliNetwork extends NetworkX {

    public UtiliNetwork(ModX mod) {
        super(mod);
    }

    @Override
    protected String getProtocolVersion() {
        return "4";
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
