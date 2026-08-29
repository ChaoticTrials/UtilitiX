package de.melanx.utilitix.client;

import de.melanx.utilitix.content.glue.StickyRenderHelper;
import de.melanx.utilitix.content.shulkerboat.ShulkerBoatRenderer;
import de.melanx.utilitix.content.shulkerboat.ShulkerRaftRenderer;
import de.melanx.utilitix.content.track.carts.MinecartRendererX;
import de.melanx.utilitix.registration.ModEntities;
import de.melanx.utilitix.registration.ModRegisterables;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.common.NeoForge;

@Mod(value = "utilitix", dist = Dist.CLIENT)
public class ClientUtilitiX {

    public ClientUtilitiX(IEventBus modBus) {
        NeoForge.EVENT_BUS.addListener(StickyRenderHelper::renderWorld);
        modBus.addListener(ClientUtilitiX::clientSetup);
    }

    private static void clientSetup(FMLClientSetupEvent event) {
        EntityRenderers.register(ModEntities.oakShulkerBoat, context -> new ShulkerBoatRenderer(context, ModelLayers.OAK_BOAT));
        EntityRenderers.register(ModEntities.spruceShulkerBoat, context -> new ShulkerBoatRenderer(context, ModelLayers.SPRUCE_BOAT));
        EntityRenderers.register(ModEntities.birchShulkerBoat, context -> new ShulkerBoatRenderer(context, ModelLayers.BIRCH_BOAT));
        EntityRenderers.register(ModEntities.jungleShulkerBoat, context -> new ShulkerBoatRenderer(context, ModelLayers.JUNGLE_BOAT));
        EntityRenderers.register(ModEntities.acaciaShulkerBoat, context -> new ShulkerBoatRenderer(context, ModelLayers.ACACIA_BOAT));
        EntityRenderers.register(ModEntities.cherryShulkerBoat, context -> new ShulkerBoatRenderer(context, ModelLayers.CHERRY_BOAT));
        EntityRenderers.register(ModEntities.darkOakShulkerBoat, context -> new ShulkerBoatRenderer(context, ModelLayers.DARK_OAK_BOAT));
        EntityRenderers.register(ModEntities.mangroveShulkerBoat, context -> new ShulkerBoatRenderer(context, ModelLayers.MANGROVE_BOAT));
        EntityRenderers.register(ModEntities.paleOakShulkerBoat, context -> new ShulkerBoatRenderer(context, ModelLayers.PALE_OAK_BOAT));
        EntityRenderers.register(ModEntities.bambooShulkerRaft, context -> new ShulkerRaftRenderer(context, ModelLayers.BAMBOO_RAFT));
        EntityRenderers.register(ModRegisterables.enderCart.get(), context -> new MinecartRendererX<>(context, ModelLayers.MINECART));
        EntityRenderers.register(ModRegisterables.pistonCart.get(), context -> new MinecartRendererX<>(context, ModelLayers.MINECART));
        EntityRenderers.register(ModRegisterables.stonecutterCart.get(), context -> new MinecartRendererX<>(context, ModelLayers.MINECART));
        EntityRenderers.register(ModRegisterables.anvilCart.get(), context -> new MinecartRendererX<>(context, ModelLayers.MINECART));
    }
}
