package de.melanx.utilitix.client;

import de.melanx.utilitix.UtilitiX;
import de.melanx.utilitix.client.commands.MapsCommand;
import de.melanx.utilitix.content.track.carts.piston.PistonCartContainerMenu;
import de.melanx.utilitix.content.track.carts.piston.PistonCartScreen;
import net.minecraft.client.model.BoatModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.commands.Commands;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterClientCommandsEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;

@EventBusSubscriber(modid = "utilitix", value = Dist.CLIENT)
public class ClientEventListener {

    public static ModelLayerLocation SHULKER_BOAT = new ModelLayerLocation(UtilitiX.getInstance().resource("shulker_boat"), "main");

    @SubscribeEvent
    public static void registerClientCommands(RegisterClientCommandsEvent event) {
        event.getDispatcher().register(Commands.literal("utilitix")
                .then(MapsCommand.register()));
    }

    @SubscribeEvent
    public static void registerLayerDefinition(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(SHULKER_BOAT, BoatModel::createBodyModel);
    }

    @SubscribeEvent
    public static void registerScreens(RegisterMenuScreensEvent event) {
        event.register(PistonCartContainerMenu.TYPE, PistonCartScreen::new);
//        event.register(BlockEntityMenu.createMenuType(ContainerMenuAdvancedBrewery::new), ScreenAdvancedBrewery::new); todo
//        event.register(BlockEntityMenu.createMenuType(ContainerMenuExperienceCrystal::new), ScreenExperienceCrystal::new);
    }
}
