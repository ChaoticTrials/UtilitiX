package de.melanx.utilitix.config;

import org.moddingx.libx.annotation.config.RegisterConfig;
import org.moddingx.libx.config.Config;

@RegisterConfig(client = true, value = "client")
public class ClientConfig {

    @Config({"On some slower PCs, or in big modpacks, it seems like rendering glue drains a lot of performance.",
            "You can disable it here to see if it improves your performance.",
            "This will deactivate the glue rendering in the world, so you won't see any glue on any block."})
    public static boolean renderGlueOnBlocks = true;
}
