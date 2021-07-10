package de.melanx.utilitix.registration;

import de.melanx.utilitix.UtilitiX;
import de.melanx.utilitix.block.ComparatorRedirector;
import de.melanx.utilitix.block.ModProperties;
import de.melanx.utilitix.block.WeakRedstoneTorch;
import de.melanx.utilitix.content.brewery.BlockAdvancedBrewery;
import de.melanx.utilitix.content.brewery.ContainerAdvancedBrewery;
import de.melanx.utilitix.content.brewery.TileAdvancedBrewery;
import de.melanx.utilitix.content.crudefurnace.BlockCrudeFurnace;
import de.melanx.utilitix.content.crudefurnace.ContainerCrudeFurnace;
import de.melanx.utilitix.content.crudefurnace.TileCrudeFurnace;
import de.melanx.utilitix.content.experiencecrystal.BlockExperienceCrystal;
import de.melanx.utilitix.content.experiencecrystal.ContainerExperienceCrystal;
import de.melanx.utilitix.content.experiencecrystal.TileExperienceCrystal;
import de.melanx.utilitix.content.track.rails.*;
import de.melanx.utilitix.content.wireless.BlockLinkedRepeater;
import io.github.noeppi_noeppi.libx.annotation.RegisterClass;
import io.github.noeppi_noeppi.libx.inventory.container.ContainerBase;
import io.github.noeppi_noeppi.libx.mod.registration.BlockBase;
import io.github.noeppi_noeppi.libx.mod.registration.BlockGUI;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.state.Property;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.state.properties.RailShape;
import net.minecraft.util.Direction;
import net.minecraftforge.common.ToolType;

import javax.annotation.Nonnull;

@RegisterClass(priority = 1)
public class ModBlocks {

    public static final BlockGUI<TileAdvancedBrewery, ContainerAdvancedBrewery> advancedBrewery = new BlockAdvancedBrewery(UtilitiX.getInstance(), AbstractBlock.Properties.from(Blocks.BREWING_STAND));
    public static final BlockGUI<TileCrudeFurnace, ContainerCrudeFurnace> crudeFurnace = new BlockCrudeFurnace(UtilitiX.getInstance(), ContainerBase.createContainerType(ContainerCrudeFurnace::new), AbstractBlock.Properties.from(Blocks.FURNACE));
    public static final BlockBase comparatorRedirectorUp = new ComparatorRedirector(UtilitiX.getInstance(), Direction.UP, AbstractBlock.Properties.from(Blocks.OBSERVER));
    public static final BlockBase comparatorRedirectorDown = new ComparatorRedirector(UtilitiX.getInstance(), Direction.DOWN, AbstractBlock.Properties.from(Blocks.OBSERVER));
    public static final WeakRedstoneTorch weakRedstoneTorch = new WeakRedstoneTorch(UtilitiX.getInstance(), AbstractBlock.Properties.from(Blocks.REDSTONE_TORCH));
    public static final BlockBase linkedRepeater = new BlockLinkedRepeater(UtilitiX.getInstance(), AbstractBlock.Properties.from(Blocks.REPEATER));
    public static final Block highspeedRail = new BlockPoweredRail(UtilitiX.getInstance(), 0.7, AbstractBlock.Properties.from(Blocks.POWERED_RAIL)) { @Nonnull @Override public Property<RailShape> getShapeProperty() { return ModProperties.RAIL_SHAPE_FLAT_STRAIGHT; }};
    public static final Block directionalRail = new BlockDirectionalRail(UtilitiX.getInstance(), 0.4, AbstractBlock.Properties.from(Blocks.POWERED_RAIL)) { @Nonnull @Override public Property<RailShape> getShapeProperty() { return BlockStateProperties.RAIL_SHAPE_STRAIGHT; }};
    public static final Block directionalHighspeedRail = new BlockDirectionalRail(UtilitiX.getInstance(), 0.7, AbstractBlock.Properties.from(Blocks.POWERED_RAIL)) { @Nonnull @Override public Property<RailShape> getShapeProperty() { return ModProperties.RAIL_SHAPE_FLAT_STRAIGHT; }};
    public static final Block crossingRail = new BlockCrossingRail(UtilitiX.getInstance(), false, AbstractBlock.Properties.from(Blocks.RAIL));
    public static final Block filterRail = new BlockFilterRail(UtilitiX.getInstance(), false, AbstractBlock.Properties.from(Blocks.RAIL));
    public static final Block reinforcedRail = new BlockReinforcedRail(UtilitiX.getInstance(), AbstractBlock.Properties.from(Blocks.RAIL));
    public static final Block reinforcedCrossingRail = new BlockCrossingRail(UtilitiX.getInstance(), true, AbstractBlock.Properties.from(Blocks.RAIL));
    public static final Block reinforcedFilterRail = new BlockFilterRail(UtilitiX.getInstance(), true, AbstractBlock.Properties.from(Blocks.RAIL));
    public static final Block pistonControllerRail = new BlockPistonControllerRail(UtilitiX.getInstance(), false, AbstractBlock.Properties.from(Blocks.ACTIVATOR_RAIL)) { @Nonnull @Override public Property<RailShape> getShapeProperty() { return BlockStateProperties.RAIL_SHAPE_STRAIGHT; }};
    public static final Block reinforcedPistonControllerRail = new BlockPistonControllerRail(UtilitiX.getInstance(), true, AbstractBlock.Properties.from(Blocks.ACTIVATOR_RAIL)) { @Nonnull @Override public Property<RailShape> getShapeProperty() { return ModProperties.RAIL_SHAPE_FLAT_STRAIGHT; }};
    public static final BlockGUI<TileExperienceCrystal, ContainerExperienceCrystal> experienceCrystal = new BlockExperienceCrystal(UtilitiX.getInstance(), ContainerBase.createContainerType(ContainerExperienceCrystal::new), AbstractBlock.Properties.create(Material.ROCK, MaterialColor.BLACK).hardnessAndResistance(3, 7).harvestTool(ToolType.PICKAXE));
}
