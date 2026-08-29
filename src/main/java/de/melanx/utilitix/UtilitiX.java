package de.melanx.utilitix;

import de.melanx.utilitix.client.ClientUtilitiX;
import de.melanx.utilitix.content.shulkerboat.ShulkerBoatRenderer;
import de.melanx.utilitix.content.shulkerboat.ShulkerRaftRenderer;
import de.melanx.utilitix.data.*;
import de.melanx.utilitix.data.enchantments.EnchantmentProvider;
import de.melanx.utilitix.data.enchantments.EnchantmentTagsProvider;
import de.melanx.utilitix.network.UtiliNetwork;
import de.melanx.utilitix.registration.ModCreativeTab;
import de.melanx.utilitix.registration.ModEntities;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import org.moddingx.libx.datagen.DatagenSystem;
import org.moddingx.libx.mod.ModXRegistration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;

@Mod("utilitix")
public final class UtilitiX extends ModXRegistration {

    private static UtilitiX instance;
    private static UtiliNetwork network;
    public final Logger logger = LoggerFactory.getLogger(UtilitiX.class);

    public UtilitiX(IEventBus modBus, Dist dist) {
        instance = this;
        network = new UtiliNetwork(this);
        new ModCreativeTab(this);

        if (dist == Dist.CLIENT) {
            new ClientUtilitiX(modBus);
        }

        DatagenSystem.create(this, system -> {
            system.addRegistryProvider(LootTableProvider::new);
            system.addRegistryProvider(EnchantmentProvider::new);

            system.addDataProvider(BlockStateProvider::new);
            system.addDataProvider(ItemModelProvider::new);
            system.addDataProvider(ModTagProvider::new);
            system.addDataProvider(EnchantmentTagsProvider::new);
            system.addDataProvider(RecipeProvider::new);
        });
    }

    @Override
    protected void setup(FMLCommonSetupEvent event) {
        // NO-OP
    }

    @Override
    protected void clientSetup(FMLClientSetupEvent event) {
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
    }

    @Nonnull
    public static UtilitiX getInstance() {
        return instance;
    }

    @Nonnull
    public static UtiliNetwork getNetwork() {
        return network;
    }
}
