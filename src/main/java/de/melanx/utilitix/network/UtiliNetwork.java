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
        return Protocol.of("8");
    }

    @Override
    protected void registerPackets() {
        this.registerGame(NetworkDirection.PLAY_TO_SERVER, new StickyChunkRequest.Serializer(), () -> StickyChunkRequest.Handler::new);
        this.registerGame(NetworkDirection.PLAY_TO_SERVER, new PistonCartModeCycle.Serializer(), () -> PistonCartModeCycle.Handler::new);
        this.registerGame(NetworkDirection.PLAY_TO_SERVER, new ClickScreenButton.Serializer(), () -> ClickScreenButton.Handler::new);
        this.registerGame(NetworkDirection.PLAY_TO_SERVER, new OpenCurioBackpack.Serializer(), () -> OpenCurioBackpack.Handler::new);

        this.registerGame(NetworkDirection.PLAY_TO_CLIENT, new StickyChunkUpdate.Serializer(), () -> StickyChunkUpdate.Handler::new);
        this.registerGame(NetworkDirection.PLAY_TO_CLIENT, new ItemEntityRepaired.Serializer(), () -> ItemEntityRepaired.Handler::new);
    }
}
