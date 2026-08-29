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
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.Block;
import snownee.jade.api.IWailaClientRegistration;
import snownee.jade.api.IWailaCommonRegistration;
import snownee.jade.api.IWailaPlugin;
import snownee.jade.api.WailaPlugin;

@WailaPlugin
public class UtilJade implements IWailaPlugin {

    public static final Identifier ADVANCED_BREWERY = UtilitiX.getInstance().id("advanced_brewery");
    public static final Identifier CRUDE_FURNACE = UtilitiX.getInstance().id("crude_furnace");
    public static final Identifier EXPERIENCE_CRYSTAL = UtilitiX.getInstance().id("experience_crystal");
    public static final Identifier LINKED_REPEATER = UtilitiX.getInstance().id("linked_repeater");
    public static final Identifier GLUE_INFORMATION = UtilitiX.getInstance().id("glue_information");

    @Override
    public void register(IWailaCommonRegistration registration) {
        registration.registerBlockDataProvider(AdvancedBreweryServerDataProvider.INSTANCE, AdvancedBreweryBlockEntity.class);
        registration.registerBlockDataProvider(CrudeFurnaceServerDataProvider.INSTANCE, CrudeFurnaceBlockEntity.class);
        registration.registerBlockDataProvider(ExperienceCrystalServerDataProvider.INSTANCE, ExperienceCrystalBlockEntity.class);
        registration.registerBlockDataProvider(LinkedRepeaterServerDataProvider.INSTANCE, LinkedRepeaterBlockEntity.class);
    }

    @Override
    public void registerClient(IWailaClientRegistration registration) {
        registration.registerBlockComponent(AdvancedBreweryProvider.INSTANCE, AdvancedBreweryBlock.class);
        registration.registerBlockComponent(CrudeFurnaceProvider.INSTANCE, CrudeFurnaceBlock.class);
        registration.registerBlockComponent(ExperienceCrystalProvider.INSTANCE, ExperienceCrystalBlock.class);
        registration.registerBlockComponent(LinkedRepeaterProvider.INSTANCE, LinkedRepeaterBlock.class);
        registration.registerBlockComponent(GlueProvider.INSTANCE, Block.class);

        registration.markAsClientFeature(GLUE_INFORMATION);
    }
}
