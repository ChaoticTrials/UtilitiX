package de.melanx.utilitix.registration;

import de.melanx.utilitix.UtilitiX;
import de.melanx.utilitix.block.ModProperties;
import de.melanx.utilitix.content.brewery.AdvancedBreweryBlock;
import de.melanx.utilitix.content.brewery.AdvancedBreweryBlockEntity;
import de.melanx.utilitix.content.brewery.AdvancedBreweryMenu;
import de.melanx.utilitix.content.crudefurnace.CrudeFurnaceBlock;
import de.melanx.utilitix.content.crudefurnace.CrudeFurnaceBlockEntity;
import de.melanx.utilitix.content.crudefurnace.CrudeFurnaceMenu;
import de.melanx.utilitix.content.decoration.StoneWallBlock;
import de.melanx.utilitix.content.experiencecrystal.ExperienceCrystalBlock;
import de.melanx.utilitix.content.experiencecrystal.ExperienceCrystalBlockEntity;
import de.melanx.utilitix.content.experiencecrystal.ExperienceCrystalMenu;
import de.melanx.utilitix.content.redstone.ComparatorRedirectorBlock;
import de.melanx.utilitix.content.redstone.DimmableRedstoneLampBlock;
import de.melanx.utilitix.content.redstone.WeakRedstoneTorchBlock;
import de.melanx.utilitix.content.redstone.wireless.LinkedRepeaterBlock;
import de.melanx.utilitix.content.track.rails.*;
import net.minecraft.core.Direction;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.state.properties.RailShape;
import net.minecraft.world.level.material.MapColor;
import org.moddingx.libx.annotation.registration.RegisterClass;
import org.moddingx.libx.base.BlockBase;
import org.moddingx.libx.base.tile.MenuBlockBE;

import javax.annotation.Nonnull;

@RegisterClass(registry = "BLOCK", priority = 1)
public class ModBlocks {

    public static final MenuBlockBE<AdvancedBreweryBlockEntity, AdvancedBreweryMenu> advancedBrewery = new AdvancedBreweryBlock(UtilitiX.getInstance(), BlockBehaviour.Properties.ofLegacyCopy(Blocks.BREWING_STAND));
    public static final MenuBlockBE<CrudeFurnaceBlockEntity, CrudeFurnaceMenu> crudeFurnace = new CrudeFurnaceBlock(UtilitiX.getInstance(), BlockBehaviour.Properties.ofLegacyCopy(Blocks.FURNACE));
    public static final BlockBase comparatorRedirectorUp = new ComparatorRedirectorBlock(UtilitiX.getInstance(), Direction.UP, BlockBehaviour.Properties.ofLegacyCopy(Blocks.OBSERVER));
    public static final BlockBase comparatorRedirectorDown = new ComparatorRedirectorBlock(UtilitiX.getInstance(), Direction.DOWN, BlockBehaviour.Properties.ofLegacyCopy(Blocks.OBSERVER));
    public static final WeakRedstoneTorchBlock weakRedstoneTorch = new WeakRedstoneTorchBlock(UtilitiX.getInstance(), BlockBehaviour.Properties.ofLegacyCopy(Blocks.REDSTONE_TORCH));
    public static final BlockBase linkedRepeater = new LinkedRepeaterBlock(UtilitiX.getInstance(), BlockBehaviour.Properties.ofLegacyCopy(Blocks.REPEATER));
    public static final DimmableRedstoneLampBlock dimmableRedstoneLamp = new DimmableRedstoneLampBlock(UtilitiX.getInstance(), BlockBehaviour.Properties.ofLegacyCopy(Blocks.REDSTONE_LAMP).strength(0.3F).sound(SoundType.GLASS).lightLevel(DimmableRedstoneLampBlock.LIGHT_EMISSION).isValidSpawn((state, level, pos, entityType) -> true));
    public static final Block highspeedRail = new PoweredRailBlock(UtilitiX.getInstance(), 0.7, BlockBehaviour.Properties.ofLegacyCopy(Blocks.POWERED_RAIL)) {
        @Nonnull
        @Override
        public Property<RailShape> getShapeProperty() {
            return ModProperties.RAIL_SHAPE_FLAT_STRAIGHT;
        }
    };
    public static final Block directionalRail = new DirectionalRailBlock(UtilitiX.getInstance(), 0.4, BlockBehaviour.Properties.ofLegacyCopy(Blocks.POWERED_RAIL));
    public static final Block directionalHighspeedRail = new DirectionalRailBlock(UtilitiX.getInstance(), 0.7, BlockBehaviour.Properties.ofLegacyCopy(Blocks.POWERED_RAIL)) {
        @Nonnull
        @Override
        public Property<RailShape> getShapeProperty() {
            return ModProperties.RAIL_SHAPE_FLAT_STRAIGHT;
        }
    };
    public static final Block crossingRail = new CrossingRailBlock(UtilitiX.getInstance(), false, BlockBehaviour.Properties.ofLegacyCopy(Blocks.RAIL));
    public static final Block filterRail = new FilterRailBlock(UtilitiX.getInstance(), false, BlockBehaviour.Properties.ofLegacyCopy(Blocks.RAIL));
    public static final Block reinforcedRail = new ReinforcedRailBlock(UtilitiX.getInstance(), BlockBehaviour.Properties.ofLegacyCopy(Blocks.RAIL));
    public static final Block reinforcedCrossingRail = new CrossingRailBlock(UtilitiX.getInstance(), true, BlockBehaviour.Properties.ofLegacyCopy(Blocks.RAIL));
    public static final Block reinforcedFilterRail = new FilterRailBlock(UtilitiX.getInstance(), true, BlockBehaviour.Properties.ofLegacyCopy(Blocks.RAIL));
    public static final Block pistonControllerRail = new PistonControllerRailBlock(UtilitiX.getInstance(), false, BlockBehaviour.Properties.ofLegacyCopy(Blocks.ACTIVATOR_RAIL));
    public static final Block reinforcedPistonControllerRail = new PistonControllerRailBlock(UtilitiX.getInstance(), true, BlockBehaviour.Properties.ofLegacyCopy(Blocks.ACTIVATOR_RAIL)) {
        @Nonnull
        @Override
        public Property<RailShape> getShapeProperty() {
            return ModProperties.RAIL_SHAPE_FLAT_STRAIGHT;
        }
    };
    public static final MenuBlockBE<ExperienceCrystalBlockEntity, ExperienceCrystalMenu> experienceCrystal = new ExperienceCrystalBlock(UtilitiX.getInstance(), BlockBehaviour.Properties.ofLegacyCopy(Blocks.STONE).mapColor(MapColor.COLOR_LIGHT_GREEN).strength(3, 7));
    public static final Block stoneWall = new StoneWallBlock(BlockBehaviour.Properties.ofLegacyCopy(Blocks.STONE), new Item.Properties());
}
