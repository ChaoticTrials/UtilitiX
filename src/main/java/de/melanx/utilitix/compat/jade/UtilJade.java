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
import mcp.mobius.waila.api.IRegistrar;
import mcp.mobius.waila.api.IWailaPlugin;
import mcp.mobius.waila.api.TooltipPosition;
import mcp.mobius.waila.api.WailaPlugin;
import net.minecraft.resources.ResourceLocation;

@WailaPlugin
public class UtilJade implements IWailaPlugin {

    public static final ResourceLocation ADVANCED_BREWERY = UtilitiX.getInstance().resource("advanced_brewery");
    public static final ResourceLocation CRUDE_FURNACE = UtilitiX.getInstance().resource("crude_furnace");
    public static final ResourceLocation EXPERIENCE_CRYSTAL = UtilitiX.getInstance().resource("experience_crystal");
    public static final ResourceLocation LINKED_REPEATER = UtilitiX.getInstance().resource("linked_repeater");

    @Override
    public void register(IRegistrar registrar) {
        registrar.registerComponentProvider(AdvancedBreweryProvider.INSTANCE, TooltipPosition.BODY, BlockAdvancedBrewery.class);
        registrar.registerBlockDataProvider(AdvancedBreweryProvider.INSTANCE, TileAdvancedBrewery.class);
        registrar.addConfig(ADVANCED_BREWERY, true);

        registrar.registerComponentProvider(CrudeFurnaceProvider.INSTANCE, TooltipPosition.BODY, BlockCrudeFurnace.class);
        registrar.registerBlockDataProvider(CrudeFurnaceProvider.INSTANCE, TileCrudeFurnace.class);
        registrar.addConfig(CRUDE_FURNACE, true);

        registrar.registerComponentProvider(ExperienceCrystalProvider.INSTANCE, TooltipPosition.BODY, BlockExperienceCrystal.class);
        registrar.registerBlockDataProvider(ExperienceCrystalProvider.INSTANCE, TileExperienceCrystal.class);
        registrar.addConfig(EXPERIENCE_CRYSTAL, true);

        registrar.registerComponentProvider(LinkedRepeaterProvider.INSTANCE, TooltipPosition.BODY, BlockLinkedRepeater.class);
        registrar.registerBlockDataProvider(LinkedRepeaterProvider.INSTANCE, TileLinkedRepeater.class);
        registrar.addConfig(LINKED_REPEATER, true);
    }
}
