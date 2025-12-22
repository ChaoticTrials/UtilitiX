package de.melanx.utilitix.config;

import org.moddingx.libx.annotation.config.RegisterConfig;
import org.moddingx.libx.config.Config;
import org.moddingx.libx.config.validate.IntRange;

@RegisterConfig(client = true, value = "client")
public class ClientConfig {

    @Config({"On some slower PCs, or in big modpacks, it seems like rendering glue drains a lot of performance.",
            "You can disable it here to see if it improves your performance.",
            "This will deactivate the glue rendering in the world, so you won't see any glue on any block."})
    public static boolean renderGlueOnBlocks = true;

    @Config({"Size scale for exporting maps", "1 = 128x128px", "2 = 256x256px", "3 = 384x384px", "And so on, you got the pattern I hope"})
    @IntRange(min = 1)
    public static int mapScale = 3;
}
