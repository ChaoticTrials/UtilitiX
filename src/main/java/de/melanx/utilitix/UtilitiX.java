package de.melanx.utilitix;

import de.melanx.utilitix.client.ClientUtilitiX;
import de.melanx.utilitix.content.BetterMending;
import de.melanx.utilitix.content.shulkerboat.ShulkerBoatRenderer;
import de.melanx.utilitix.data.*;
import de.melanx.utilitix.data.enchantments.EnchantmentProvider;
import de.melanx.utilitix.data.enchantments.EnchantmentTagsProvider;
import de.melanx.utilitix.network.UtiliNetwork;
import de.melanx.utilitix.registration.ModCreativeTab;
import de.melanx.utilitix.registration.ModEntities;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.raid.Raid;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.common.asm.enumextension.EnumProxy;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import org.moddingx.libx.datagen.DatagenSystem;
import org.moddingx.libx.mod.ModXRegistration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.util.function.Supplier;

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

//        modBus.addListener(SlimyCapability::registerCapability); todo

        NeoForge.EVENT_BUS.register(new BetterMending());
//        NeoForge.EVENT_BUS.addListener(LevelChunk.class, SlimyCapability::attach); todo

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
        if (UtilitiXConfig.illusionerInRaid) {
            // todo check
            new EnumProxy<>(
                    Raid.RaiderType.class,
                    (Supplier<EntityType<?>>) () -> EntityType.ILLUSIONER, new int[]{0, 5, 0, 2, 0, 2, 0, 3}
            );
        }
    }

    @Override
    protected void clientSetup(FMLClientSetupEvent event) {
        EntityRenderers.register(ModEntities.shulkerBoat, ShulkerBoatRenderer::new);
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
