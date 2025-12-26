package de.melanx.utilitix.data;

import de.melanx.utilitix.UtilitiX;
import de.melanx.utilitix.block.ModProperties;
import de.melanx.utilitix.content.redstone.ComparatorRedirectorBlock;
import de.melanx.utilitix.content.redstone.DimmableRedstoneLampBlock;
import de.melanx.utilitix.data.state.RailState;
import de.melanx.utilitix.registration.ModBlocks;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.AbstractFurnaceBlock;
import net.minecraft.world.level.block.BaseRailBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.WallBlock;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.state.properties.RailShape;
import net.neoforged.neoforge.client.model.generators.ConfiguredModel;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.client.model.generators.VariantBlockStateBuilder;
import org.moddingx.libx.datagen.DatagenContext;
import org.moddingx.libx.datagen.provider.model.BlockStateProviderBase;

import java.util.function.Supplier;

public class BlockStateProvider extends BlockStateProviderBase {

    public static final ResourceLocation LINKED_REPEATER_PARENT = ResourceLocation.fromNamespaceAndPath(UtilitiX.getInstance().modid, "block/linked_repeater_base");

    public static final ResourceLocation TEXTURE_REPEATER_OFF = ResourceLocation.fromNamespaceAndPath("minecraft", "block/repeater");
    public static final ResourceLocation TEXTURE_REPEATER_ON = ResourceLocation.fromNamespaceAndPath("minecraft", "block/repeater_on");

    public static final ResourceLocation TEXTURE_TORCH_OFF = ResourceLocation.fromNamespaceAndPath("minecraft", "block/redstone_torch_off");
    public static final ResourceLocation TEXTURE_TORCH_ON = ResourceLocation.fromNamespaceAndPath("minecraft", "block/redstone_torch");

    public BlockStateProvider(DatagenContext context) {
        super(context);
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

            return;
        }

        if (block instanceof BaseRailBlock) {
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

            return;
        }

        if (block == ModBlocks.crudeFurnace) {
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

            return;
        }

        if (block == ModBlocks.stoneWall) {
            this.wallBlock((WallBlock) block, this.mcLoc("block/stone"));

            return;
        }

        if (block == ModBlocks.dimmableRedstoneLamp) {
            VariantBlockStateBuilder builder = this.getVariantBuilder(block);
            for (int signal : DimmableRedstoneLampBlock.SIGNAL.getPossibleValues()) {
                boolean isDefault = signal == 0 || signal == 15;

                ConfiguredModel signalModel;
                if (isDefault) {
                    boolean on = signal == 15;
                    signalModel = new ConfiguredModel(this.models().cubeAll(id.getPath() + (on ? "_" + signal : ""), this.mcLoc("block/redstone_lamp" + (on ? "_on" : ""))));
                } else {
                    signalModel = new ConfiguredModel(this.models().cubeAll(id.getPath() + "_" + signal, this.modLoc("block/dimmable_redstone_lamp_" + signal)));
                }

                builder.partialState().with(DimmableRedstoneLampBlock.SIGNAL, signal)
                        .addModels(signalModel);
            }

            return;
        }

            super.defaultState(id, block, model);
    }

    @Override
    protected ModelFile defaultModel(ResourceLocation id, Block block) {
        if (block == ModBlocks.linkedRepeater || block instanceof BaseRailBlock || block == ModBlocks.crudeFurnace) {
            return null;
        }

        if (block instanceof ComparatorRedirectorBlock) {
            ResourceLocation top = ResourceLocation.fromNamespaceAndPath(UtilitiX.getInstance().modid, "block/comparator_redirector_top");
            ResourceLocation bottom = ResourceLocation.fromNamespaceAndPath(UtilitiX.getInstance().modid, "block/comparator_redirector_bottom");
            if (((ComparatorRedirectorBlock) block).direction == Direction.DOWN) {
                ResourceLocation tmp = top;
                top = bottom;
                bottom = tmp;
            }

            return this.models().cubeBottomTop(id.getPath(),
                    ResourceLocation.fromNamespaceAndPath(UtilitiX.getInstance().modid, "block/comparator_redirector_side"),
                    top, bottom
            );
        }

        return super.defaultModel(id, block);
    }
}
