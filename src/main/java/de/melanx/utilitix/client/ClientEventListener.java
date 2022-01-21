package de.melanx.utilitix.client;

import de.melanx.utilitix.client.commands.MapsCommand;
import net.minecraft.commands.Commands;
import net.minecraftforge.client.event.RegisterClientCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class ClientEventListener {

    @SubscribeEvent
    public void registerClientCommands(RegisterClientCommandsEvent event) {
        event.getDispatcher().register(Commands.literal("utilitix")
                .then(MapsCommand.register()));
    }
}
