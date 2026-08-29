package de.melanx.utilitix;

import de.melanx.utilitix.data.*;
import de.melanx.utilitix.data.enchantments.EnchantmentProvider;
import de.melanx.utilitix.data.enchantments.EnchantmentTagsProvider;
import de.melanx.utilitix.network.UtiliNetwork;
import de.melanx.utilitix.registration.ModCreativeTab;
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

    public UtilitiX() {
        instance = this;
        network = new UtiliNetwork(this);
        new ModCreativeTab(this);

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
        // NO-OP
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
