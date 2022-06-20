package de.melanx.utilitix.client;

import de.melanx.utilitix.UtilitiX;
import de.melanx.utilitix.client.commands.MapsCommand;
import net.minecraft.client.model.BoatModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.commands.Commands;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.RegisterClientCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = "utilitix", bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientEventListener {

    public static ModelLayerLocation SHULKER_BOAT = new ModelLayerLocation(UtilitiX.getInstance().resource("shulker_boat"), "main");

    @SubscribeEvent
    public void registerClientCommands(RegisterClientCommandsEvent event) {
        event.getDispatcher().register(Commands.literal("utilitix")
                .then(MapsCommand.register()));
    }

    @SubscribeEvent
    public static void registerLayerDefinition(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(SHULKER_BOAT, () -> BoatModel.createBodyModel(true));
    }
}
