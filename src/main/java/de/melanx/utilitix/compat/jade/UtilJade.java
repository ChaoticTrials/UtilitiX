package de.melanx.utilitix.compat.jade;

import de.melanx.utilitix.UtilitiX;
import de.melanx.utilitix.content.brewery.BlockAdvancedBrewery;
import de.melanx.utilitix.content.brewery.TileAdvancedBrewery;
import de.melanx.utilitix.content.crudefurnace.BlockCrudeFurnace;
import de.melanx.utilitix.content.crudefurnace.TileCrudeFurnace;
import de.melanx.utilitix.content.experiencecrystal.BlockExperienceCrystal;
import de.melanx.utilitix.content.experiencecrystal.TileExperienceCrystal;
import de.melanx.utilitix.content.wireless.BlockLinkedRepeater;
import de.melanx.utilitix.content.wireless.TileLinkedRepeater;
import de.melanx.utilitix.registration.ModEntities;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import snownee.jade.api.IWailaClientRegistration;
import snownee.jade.api.IWailaCommonRegistration;
import snownee.jade.api.IWailaPlugin;
import snownee.jade.api.WailaPlugin;

@WailaPlugin
public class UtilJade implements IWailaPlugin {

    public static final ResourceLocation ADVANCED_BREWERY = UtilitiX.getInstance().resource("advanced_brewery");
    public static final ResourceLocation CRUDE_FURNACE = UtilitiX.getInstance().resource("crude_furnace");
    public static final ResourceLocation EXPERIENCE_CRYSTAL = UtilitiX.getInstance().resource("experience_crystal");
    public static final ResourceLocation LINKED_REPEATER = UtilitiX.getInstance().resource("linked_repeater");
    public static final ResourceLocation GLUE_INFORMATION = UtilitiX.getInstance().resource("glue_information");

    @Override
    public void register(IWailaCommonRegistration registration) {
        registration.registerBlockDataProvider(AdvancedBreweryProvider.INSTANCE, TileAdvancedBrewery.class);
        registration.registerBlockDataProvider(CrudeFurnaceProvider.INSTANCE, TileCrudeFurnace.class);
        registration.registerBlockDataProvider(ExperienceCrystalProvider.INSTANCE, TileExperienceCrystal.class);
        registration.registerBlockDataProvider(LinkedRepeaterProvider.INSTANCE, TileLinkedRepeater.class);
    }

    @Override
    public void registerClient(IWailaClientRegistration registration) {
        registration.registerBlockComponent(AdvancedBreweryProvider.INSTANCE, BlockAdvancedBrewery.class);
        registration.registerBlockComponent(CrudeFurnaceProvider.INSTANCE, BlockCrudeFurnace.class);
        registration.registerBlockComponent(ExperienceCrystalProvider.INSTANCE, BlockExperienceCrystal.class);
        registration.registerBlockComponent(LinkedRepeaterProvider.INSTANCE, BlockLinkedRepeater.class);
        registration.registerBlockComponent(GlueProvider.INSTANCE, Block.class);

        registration.usePickedResult(ModEntities.shulkerBoat);
        registration.markAsClientFeature(GLUE_INFORMATION);
    }
}
