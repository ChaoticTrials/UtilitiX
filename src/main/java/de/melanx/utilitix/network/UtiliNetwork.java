package de.melanx.utilitix.network;

import de.melanx.utilitix.network.handler.*;
import org.moddingx.libx.mod.ModX;
import org.moddingx.libx.network.NetworkX;

public class UtiliNetwork extends NetworkX {

    public UtiliNetwork(ModX mod) {
        super(mod);

        // send to server
        this.register(new StickyChunkRequest());
        this.register(new PistonCartModeCycle());
        this.register(new ClickScreenButton());
        this.register(new OpenCurioBackpack());

        // send to client
        this.register(new StickyChunkUpdate());
        this.register(new ItemEntityRepaired());
    }

    @Override
    protected String getVersion() {
        return "10";
    }
}
