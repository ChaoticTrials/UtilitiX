package de.melanx.utilitix.registration;

import de.melanx.utilitix.UtilitiX;
import de.melanx.utilitix.block.ComparatorRedirector;
import de.melanx.utilitix.block.ModProperties;
import de.melanx.utilitix.block.WeakRedstoneTorch;
import de.melanx.utilitix.content.brewery.BlockAdvancedBrewery;
import de.melanx.utilitix.content.brewery.ContainerMenuAdvancedBrewery;
import de.melanx.utilitix.content.brewery.TileAdvancedBrewery;
import de.melanx.utilitix.content.crudefurnace.BlockCrudeFurnace;
import de.melanx.utilitix.content.crudefurnace.ContainerMenuCrudeFurnace;
import de.melanx.utilitix.content.crudefurnace.TileCrudeFurnace;
import de.melanx.utilitix.content.experiencecrystal.BlockExperienceCrystal;
import de.melanx.utilitix.content.experiencecrystal.ContainerMenuExperienceCrystal;
import de.melanx.utilitix.content.experiencecrystal.TileExperienceCrystal;
import de.melanx.utilitix.content.track.rails.*;
import de.melanx.utilitix.content.wireless.BlockLinkedRepeater;
import io.github.noeppi_noeppi.libx.annotation.registration.RegisterClass;
import io.github.noeppi_noeppi.libx.base.BlockBase;
import io.github.noeppi_noeppi.libx.base.tile.BlockMenu;
import io.github.noeppi_noeppi.libx.menu.BlockEntityMenu;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.state.properties.RailShape;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;

import javax.annotation.Nonnull;

@RegisterClass(priority = 1)
public class ModBlocks {

    public static final BlockMenu<TileAdvancedBrewery, ContainerMenuAdvancedBrewery> advancedBrewery = new BlockAdvancedBrewery(UtilitiX.getInstance(), BlockBehaviour.Properties.copy(Blocks.BREWING_STAND));
    public static final BlockMenu<TileCrudeFurnace, ContainerMenuCrudeFurnace> crudeFurnace = new BlockCrudeFurnace(UtilitiX.getInstance(), BlockEntityMenu.createMenuType(ContainerMenuCrudeFurnace::new), BlockBehaviour.Properties.copy(Blocks.FURNACE));
    public static final BlockBase comparatorRedirectorUp = new ComparatorRedirector(UtilitiX.getInstance(), Direction.UP, BlockBehaviour.Properties.copy(Blocks.OBSERVER));
    public static final BlockBase comparatorRedirectorDown = new ComparatorRedirector(UtilitiX.getInstance(), Direction.DOWN, BlockBehaviour.Properties.copy(Blocks.OBSERVER));
    public static final WeakRedstoneTorch weakRedstoneTorch = new WeakRedstoneTorch(UtilitiX.getInstance(), BlockBehaviour.Properties.copy(Blocks.REDSTONE_TORCH));
    public static final BlockBase linkedRepeater = new BlockLinkedRepeater(UtilitiX.getInstance(), BlockBehaviour.Properties.copy(Blocks.REPEATER));
    public static final Block highspeedRail = new BlockPoweredRail(UtilitiX.getInstance(), 0.7, BlockBehaviour.Properties.copy(Blocks.POWERED_RAIL)) {
        @Nonnull
        @Override
        public Property<RailShape> getShapeProperty() {
            return ModProperties.RAIL_SHAPE_FLAT_STRAIGHT;
        }
    };
    public static final Block directionalRail = new BlockDirectionalRail(UtilitiX.getInstance(), 0.4, BlockBehaviour.Properties.copy(Blocks.POWERED_RAIL)) {
        @Nonnull
        @Override
        public Property<RailShape> getShapeProperty() {
            return BlockStateProperties.RAIL_SHAPE_STRAIGHT;
        }
    };
    public static final Block directionalHighspeedRail = new BlockDirectionalRail(UtilitiX.getInstance(), 0.7, BlockBehaviour.Properties.copy(Blocks.POWERED_RAIL)) {
        @Nonnull
        @Override
        public Property<RailShape> getShapeProperty() {
            return ModProperties.RAIL_SHAPE_FLAT_STRAIGHT;
        }
    };
    public static final Block crossingRail = new BlockCrossingRail(UtilitiX.getInstance(), false, BlockBehaviour.Properties.copy(Blocks.RAIL));
    public static final Block filterRail = new BlockFilterRail(UtilitiX.getInstance(), false, BlockBehaviour.Properties.copy(Blocks.RAIL));
    public static final Block reinforcedRail = new BlockReinforcedRail(UtilitiX.getInstance(), BlockBehaviour.Properties.copy(Blocks.RAIL));
    public static final Block reinforcedCrossingRail = new BlockCrossingRail(UtilitiX.getInstance(), true, BlockBehaviour.Properties.copy(Blocks.RAIL));
    public static final Block reinforcedFilterRail = new BlockFilterRail(UtilitiX.getInstance(), true, BlockBehaviour.Properties.copy(Blocks.RAIL));
    public static final Block pistonControllerRail = new BlockPistonControllerRail(UtilitiX.getInstance(), false, BlockBehaviour.Properties.copy(Blocks.ACTIVATOR_RAIL)) {
        @Nonnull
        @Override
        public Property<RailShape> getShapeProperty() {
            return BlockStateProperties.RAIL_SHAPE_STRAIGHT;
        }
    };
    public static final Block reinforcedPistonControllerRail = new BlockPistonControllerRail(UtilitiX.getInstance(), true, BlockBehaviour.Properties.copy(Blocks.ACTIVATOR_RAIL)) {
        @Nonnull
        @Override
        public Property<RailShape> getShapeProperty() {
            return ModProperties.RAIL_SHAPE_FLAT_STRAIGHT;
        }
    };
    public static final BlockMenu<TileExperienceCrystal, ContainerMenuExperienceCrystal> experienceCrystal = new BlockExperienceCrystal(UtilitiX.getInstance(), BlockEntityMenu.createMenuType(ContainerMenuExperienceCrystal::new), BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_BLACK).strength(3, 7));
}
