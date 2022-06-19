package de.melanx.utilitix.data;

import de.melanx.utilitix.UtilitiX;
import de.melanx.utilitix.block.ComparatorRedirector;
import de.melanx.utilitix.block.DimmableRedstoneLamp;
import de.melanx.utilitix.block.ModProperties;
import de.melanx.utilitix.data.state.RailState;
import de.melanx.utilitix.registration.ModBlocks;
import net.minecraft.core.Direction;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.AbstractFurnaceBlock;
import net.minecraft.world.level.block.BaseRailBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.WallBlock;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.state.properties.RailShape;
import net.minecraftforge.client.model.generators.ConfiguredModel;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.client.model.generators.VariantBlockStateBuilder;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.moddingx.libx.annotation.data.Datagen;
import org.moddingx.libx.datagen.provider.BlockStateProviderBase;
import org.moddingx.libx.mod.ModX;

import java.util.function.Supplier;

@Datagen
public class BlockStateProvider extends BlockStateProviderBase {

    public static final ResourceLocation LINKED_REPEATER_PARENT = new ResourceLocation(UtilitiX.getInstance().modid, "block/linked_repeater_base");

    public static final ResourceLocation TEXTURE_REPEATER_OFF = new ResourceLocation("minecraft", "block/repeater");
    public static final ResourceLocation TEXTURE_REPEATER_ON = new ResourceLocation("minecraft", "block/repeater_on");

    public static final ResourceLocation TEXTURE_TORCH_OFF = new ResourceLocation("minecraft", "block/redstone_torch_off");
    public static final ResourceLocation TEXTURE_TORCH_ON = new ResourceLocation("minecraft", "block/redstone_torch");

    public BlockStateProvider(ModX mod, DataGenerator generator, ExistingFileHelper helper) {
        super(mod, generator, helper);
    }

    @Override
    protected void setup() {
        this.manualState(ModBlocks.weakRedstoneTorch);
        this.manualState(ModBlocks.weakRedstoneTorch.wallTorch);
        this.manualModel(ModBlocks.experienceCrystal);
        this.manualModel(ModBlocks.advancedBrewery);
    }

    @Override
    protected void defaultState(ResourceLocation id, Block block, Supplier<ModelFile> model) {
        if (block == ModBlocks.linkedRepeater) {
            VariantBlockStateBuilder builder = this.getVariantBuilder(block);
            ModelFile modelOn = this.models().withExistingParent(id.getPath() + "_on", LINKED_REPEATER_PARENT)
                    .texture("repeater", TEXTURE_REPEATER_ON)
                    .texture("torch", TEXTURE_TORCH_ON);
            ModelFile modelOff = this.models().withExistingParent(id.getPath() + "_off", LINKED_REPEATER_PARENT)
                    .texture("repeater", TEXTURE_REPEATER_OFF)
                    .texture("torch", TEXTURE_TORCH_OFF);
            for (Direction dir : BlockStateProperties.HORIZONTAL_FACING.getPossibleValues()) {
                for (int power : BlockStateProperties.POWER.getPossibleValues()) {
                    builder.partialState()
                            .with(BlockStateProperties.HORIZONTAL_FACING, dir)
                            .with(BlockStateProperties.POWER, power)
                            .addModels(new ConfiguredModel(power > 0 ? modelOn : modelOff, 0, (int) dir.toYRot(), false));
                }
            }
        } else if (block instanceof BaseRailBlock) {
            //noinspection unchecked
            Property<RailShape> shapeProperty = (Property<RailShape>) block.getStateDefinition().getProperties().stream()
                    .filter(p -> RailShape.class.equals(p.getValueClass())).findFirst().orElse(null);
            Property<Boolean> reverseProperty = block.getStateDefinition().getProperties().contains(ModProperties.REVERSE) ? ModProperties.REVERSE : null;
            if (shapeProperty == null) {
                throw new IllegalStateException("Rail block without shape property.");
            }
            VariantBlockStateBuilder builder = this.getVariantBuilder(block);
            if (block.getStateDefinition().getProperties().contains(ModProperties.RAIL_SIDE)) {
                RailState stateLeft = new RailState(shapeProperty, reverseProperty, p -> p.with(ModProperties.RAIL_SIDE, false));
                RailState stateRight = new RailState(shapeProperty, reverseProperty, p -> p.with(ModProperties.RAIL_SIDE, true));
                stateLeft.generate(this, builder, id, "left");
                stateRight.generate(this, builder, id, "right");
            } else if (block.getStateDefinition().getProperties().contains(BlockStateProperties.POWERED)) {
                RailState stateOff = new RailState(shapeProperty, reverseProperty, p -> p.with(BlockStateProperties.POWERED, false));
                RailState stateOn = new RailState(shapeProperty, reverseProperty, p -> p.with(BlockStateProperties.POWERED, true));
                stateOff.generate(this, builder, id);
                stateOn.generate(this, builder, id, "on");
            } else {
                RailState state = new RailState(shapeProperty, reverseProperty);
                state.generate(this, builder, id);
            }

//            if (block.getStateContainer().getProperties().contains(BlockStateProperties.RAIL_SHAPE)) {
//                VariantBlockStateBuilder builder = this.getVariantBuilder(block);
//                ModelFile modelStraight = this.models().withExistingParent(id.getPath(), STRAIGHT_RAIL_PARENT)
//                        .texture("rail", new ResourceLocation(id.getNamespace(), "block/" + id.getPath()));
//                ModelFile modelCorner = this.models().withExistingParent(id.getPath() + "_corner", CURVED_RAIL_PARENT)
//                        .texture("rail", new ResourceLocation(id.getNamespace(), "block/" + id.getPath() + "_corner"));
//                ModelFile modelRaisedNE = this.models().withExistingParent(id.getPath() + "_ascending_ne", RAISED_RAIL_NE_PARENT)
//                        .texture("rail", new ResourceLocation(id.getNamespace(), "block/" + id.getPath()));
//                ModelFile modelRaisedSW = this.models().withExistingParent(id.getPath() + "_ascending_sw", RAISED_RAIL_SW_PARENT)
//                        .texture("rail", new ResourceLocation(id.getNamespace(), "block/" + id.getPath()));
//                builder.partialState().with(BlockStateProperties.RAIL_SHAPE, RailShape.NORTH_SOUTH).addModels(new ConfiguredModel(modelStraight, 0, 0, false));
//                builder.partialState().with(BlockStateProperties.RAIL_SHAPE, RailShape.EAST_WEST).addModels(new ConfiguredModel(modelStraight, 0, 90, false));
//                builder.partialState().with(BlockStateProperties.RAIL_SHAPE, RailShape.ASCENDING_EAST).addModels(new ConfiguredModel(modelRaisedNE, 0, 90, false));
//                builder.partialState().with(BlockStateProperties.RAIL_SHAPE, RailShape.ASCENDING_WEST).addModels(new ConfiguredModel(modelRaisedSW, 0, 90, false));
//                builder.partialState().with(BlockStateProperties.RAIL_SHAPE, RailShape.ASCENDING_NORTH).addModels(new ConfiguredModel(modelRaisedNE, 0, 0, false));
//                builder.partialState().with(BlockStateProperties.RAIL_SHAPE, RailShape.ASCENDING_SOUTH).addModels(new ConfiguredModel(modelRaisedSW, 0, 0, false));
//                builder.partialState().with(BlockStateProperties.RAIL_SHAPE, RailShape.SOUTH_EAST).addModels(new ConfiguredModel(modelCorner, 0, 0, false));
//                builder.partialState().with(BlockStateProperties.RAIL_SHAPE, RailShape.SOUTH_WEST).addModels(new ConfiguredModel(modelCorner, 0, 90, false));
//                builder.partialState().with(BlockStateProperties.RAIL_SHAPE, RailShape.NORTH_WEST).addModels(new ConfiguredModel(modelCorner, 0, 180, false));
//                builder.partialState().with(BlockStateProperties.RAIL_SHAPE, RailShape.NORTH_EAST).addModels(new ConfiguredModel(modelCorner, 0, 270, false));
//            } else if (block.getStateContainer().getProperties().contains(BlockStateProperties.RAIL_SHAPE_STRAIGHT)) {
//                VariantBlockStateBuilder builder = this.getVariantBuilder(block);
//                ModelFile modelStraight = this.models().withExistingParent(id.getPath(), STRAIGHT_RAIL_PARENT)
//                        .texture("rail", new ResourceLocation(id.getNamespace(), "block/" + id.getPath()));
//                ModelFile modelRaisedNE = this.models().withExistingParent(id.getPath() + "_ascending_ne", RAISED_RAIL_NE_PARENT)
//                        .texture("rail", new ResourceLocation(id.getNamespace(), "block/" + id.getPath()));
//                ModelFile modelRaisedSW = this.models().withExistingParent(id.getPath() + "_ascending_sw", RAISED_RAIL_SW_PARENT)
//                        .texture("rail", new ResourceLocation(id.getNamespace(), "block/" + id.getPath()));
//                if (block.getStateContainer().getProperties().contains(BlockStateProperties.POWERED)) {
//                    ModelFile modelStraightPowered = this.models().withExistingParent(id.getPath() + "_on", STRAIGHT_RAIL_PARENT)
//                            .texture("rail", new ResourceLocation(id.getNamespace(), "block/" + id.getPath() + "_on"));
//                    ModelFile modelRaisedNEPowered = this.models().withExistingParent(id.getPath() + "_ascending_ne_on", RAISED_RAIL_NE_PARENT)
//                            .texture("rail", new ResourceLocation(id.getNamespace(), "block/" + id.getPath() + "_on"));
//                    ModelFile modelRaisedSWPowered = this.models().withExistingParent(id.getPath() + "_ascending_sw_on", RAISED_RAIL_SW_PARENT)
//                            .texture("rail", new ResourceLocation(id.getNamespace(), "block/" + id.getPath() + "_on"));
//                    builder.partialState().with(BlockStateProperties.RAIL_SHAPE_STRAIGHT, RailShape.NORTH_SOUTH).with(BlockStateProperties.POWERED, false).addModels(new ConfiguredModel(modelStraight, 0, 0, false));
//                    builder.partialState().with(BlockStateProperties.RAIL_SHAPE_STRAIGHT, RailShape.EAST_WEST).with(BlockStateProperties.POWERED, false).addModels(new ConfiguredModel(modelStraight, 0, 90, false));
//                    builder.partialState().with(BlockStateProperties.RAIL_SHAPE_STRAIGHT, RailShape.ASCENDING_EAST).with(BlockStateProperties.POWERED, false).addModels(new ConfiguredModel(modelRaisedNE, 0, 90, false));
//                    builder.partialState().with(BlockStateProperties.RAIL_SHAPE_STRAIGHT, RailShape.ASCENDING_WEST).with(BlockStateProperties.POWERED, false).addModels(new ConfiguredModel(modelRaisedSW, 0, 90, false));
//                    builder.partialState().with(BlockStateProperties.RAIL_SHAPE_STRAIGHT, RailShape.ASCENDING_NORTH).with(BlockStateProperties.POWERED, false).addModels(new ConfiguredModel(modelRaisedNE, 0, 0, false));
//                    builder.partialState().with(BlockStateProperties.RAIL_SHAPE_STRAIGHT, RailShape.ASCENDING_SOUTH).with(BlockStateProperties.POWERED, false).addModels(new ConfiguredModel(modelRaisedSW, 0, 0, false));
//                    builder.partialState().with(BlockStateProperties.RAIL_SHAPE_STRAIGHT, RailShape.NORTH_SOUTH).with(BlockStateProperties.POWERED, true).addModels(new ConfiguredModel(modelStraightPowered, 0, 0, false));
//                    builder.partialState().with(BlockStateProperties.RAIL_SHAPE_STRAIGHT, RailShape.EAST_WEST).with(BlockStateProperties.POWERED, true).addModels(new ConfiguredModel(modelStraightPowered, 0, 90, false));
//                    builder.partialState().with(BlockStateProperties.RAIL_SHAPE_STRAIGHT, RailShape.ASCENDING_EAST).with(BlockStateProperties.POWERED, true).addModels(new ConfiguredModel(modelRaisedNEPowered, 0, 90, false));
//                    builder.partialState().with(BlockStateProperties.RAIL_SHAPE_STRAIGHT, RailShape.ASCENDING_WEST).with(BlockStateProperties.POWERED, true).addModels(new ConfiguredModel(modelRaisedSWPowered, 0, 90, false));
//                    builder.partialState().with(BlockStateProperties.RAIL_SHAPE_STRAIGHT, RailShape.ASCENDING_NORTH).with(BlockStateProperties.POWERED, true).addModels(new ConfiguredModel(modelRaisedNEPowered, 0, 0, false));
//                    builder.partialState().with(BlockStateProperties.RAIL_SHAPE_STRAIGHT, RailShape.ASCENDING_SOUTH).with(BlockStateProperties.POWERED, true).addModels(new ConfiguredModel(modelRaisedSWPowered, 0, 0, false));
//                } else {
//                    builder.partialState().with(BlockStateProperties.RAIL_SHAPE_STRAIGHT, RailShape.NORTH_SOUTH).addModels(new ConfiguredModel(modelStraight, 0, 0, false));
//                    builder.partialState().with(BlockStateProperties.RAIL_SHAPE_STRAIGHT, RailShape.EAST_WEST).addModels(new ConfiguredModel(modelStraight, 0, 90, false));
//                    builder.partialState().with(BlockStateProperties.RAIL_SHAPE_STRAIGHT, RailShape.ASCENDING_EAST).addModels(new ConfiguredModel(modelRaisedNE, 0, 90, false));
//                    builder.partialState().with(BlockStateProperties.RAIL_SHAPE_STRAIGHT, RailShape.ASCENDING_WEST).addModels(new ConfiguredModel(modelRaisedSW, 0, 90, false));
//                    builder.partialState().with(BlockStateProperties.RAIL_SHAPE_STRAIGHT, RailShape.ASCENDING_NORTH).addModels(new ConfiguredModel(modelRaisedNE, 0, 0, false));
//                    builder.partialState().with(BlockStateProperties.RAIL_SHAPE_STRAIGHT, RailShape.ASCENDING_SOUTH).addModels(new ConfiguredModel(modelRaisedSW, 0, 0, false));
//                }
//            }
        } else if (block == ModBlocks.crudeFurnace) {
            VariantBlockStateBuilder builder = this.getVariantBuilder(block);
            ModelFile modelOn = this.models().orientable(id.getPath() + "_on",
                    this.modLoc("block/" + id.getPath() + "_side"),
                    this.modLoc("block/" + id.getPath() + "_front_on"),
                    this.modLoc("block/" + id.getPath() + "_top")
            );
            ModelFile modelOff = this.models().orientable(id.getPath(),
                    this.modLoc("block/" + id.getPath() + "_side"),
                    this.modLoc("block/" + id.getPath() + "_front"),
                    this.modLoc("block/" + id.getPath() + "_top")
            );
            for (Direction dir : BlockStateProperties.HORIZONTAL_FACING.getPossibleValues()) {
                for (boolean value : AbstractFurnaceBlock.LIT.getPossibleValues()) {
                    builder.partialState()
                            .with(BlockStateProperties.HORIZONTAL_FACING, dir)
                            .with(AbstractFurnaceBlock.LIT, value)
                            .addModels(new ConfiguredModel(value ? modelOn : modelOff, 0, (int) dir.getOpposite().toYRot(), false));
                }
            }
        } else if (block == ModBlocks.stoneWall) {
            this.wallBlock((WallBlock) block, this.mcLoc("block/stone"));
        } else if (block == ModBlocks.dimmableRedstoneLamp) {
            VariantBlockStateBuilder builder = this.getVariantBuilder(block);
            for (int signal : DimmableRedstoneLamp.SIGNAL.getPossibleValues()) {
                boolean isDefault = signal == 0 || signal == 15;

                ConfiguredModel signalModel;
                if (isDefault) {
                    boolean on = signal == 15;
                    signalModel = new ConfiguredModel(this.models().cubeAll(id.getPath() + (on ? "_" + signal : ""), this.mcLoc("block/redstone_lamp" + (on ? "_on" : ""))));
                } else {
                    signalModel = new ConfiguredModel(this.models().cubeAll(id.getPath() + "_" + signal, this.modLoc("block/dimmable_redstone_lamp_" + signal)));
                }

                builder.partialState().with(DimmableRedstoneLamp.SIGNAL, signal)
                        .addModels(signalModel);
            }
        } else {
            super.defaultState(id, block, model);
        }
    }

    @Override
    protected ModelFile defaultModel(ResourceLocation id, Block block) {
        if (block == ModBlocks.linkedRepeater || block instanceof BaseRailBlock || block == ModBlocks.crudeFurnace) {
            return null;
        } else if (block instanceof ComparatorRedirector) {
            ResourceLocation top = new ResourceLocation(UtilitiX.getInstance().modid, "block/comparator_redirector_top");
            ResourceLocation bottom = new ResourceLocation(UtilitiX.getInstance().modid, "block/comparator_redirector_bottom");
            if (((ComparatorRedirector) block).direction == Direction.DOWN) {
                ResourceLocation tmp = top;
                top = bottom;
                bottom = tmp;
            }
            return this.models().cubeBottomTop(id.getPath(),
                    new ResourceLocation(UtilitiX.getInstance().modid, "block/comparator_redirector_side"),
                    top, bottom
            );
        } else {
            return super.defaultModel(id, block);
        }
    }
}
