package de.melanx.utilitix.compat.jade;

import de.melanx.utilitix.UtilitiX;
import de.melanx.utilitix.content.brewery.AdvancedBreweryBlock;
import de.melanx.utilitix.content.brewery.AdvancedBreweryBlockEntity;
import de.melanx.utilitix.content.crudefurnace.CrudeFurnaceBlock;
import de.melanx.utilitix.content.crudefurnace.CrudeFurnaceBlockEntity;
import de.melanx.utilitix.content.experiencecrystal.ExperienceCrystalBlock;
import de.melanx.utilitix.content.experiencecrystal.ExperienceCrystalBlockEntity;
import de.melanx.utilitix.content.redstone.wireless.LinkedRepeaterBlock;
import de.melanx.utilitix.content.redstone.wireless.LinkedRepeaterBlockEntity;
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
        registration.registerBlockDataProvider(AdvancedBreweryProvider.INSTANCE, AdvancedBreweryBlockEntity.class);
        registration.registerBlockDataProvider(CrudeFurnaceProvider.INSTANCE, CrudeFurnaceBlockEntity.class);
        registration.registerBlockDataProvider(ExperienceCrystalProvider.INSTANCE, ExperienceCrystalBlockEntity.class);
        registration.registerBlockDataProvider(LinkedRepeaterProvider.INSTANCE, LinkedRepeaterBlockEntity.class);
    }

    @Override
    public void registerClient(IWailaClientRegistration registration) {
        registration.registerBlockComponent(AdvancedBreweryProvider.INSTANCE, AdvancedBreweryBlock.class);
        registration.registerBlockComponent(CrudeFurnaceProvider.INSTANCE, CrudeFurnaceBlock.class);
        registration.registerBlockComponent(ExperienceCrystalProvider.INSTANCE, ExperienceCrystalBlock.class);
        registration.registerBlockComponent(LinkedRepeaterProvider.INSTANCE, LinkedRepeaterBlock.class);
        registration.registerBlockComponent(GlueProvider.INSTANCE, Block.class);

        registration.usePickedResult(ModEntities.shulkerBoat);
        registration.markAsClientFeature(GLUE_INFORMATION);
    }
}
