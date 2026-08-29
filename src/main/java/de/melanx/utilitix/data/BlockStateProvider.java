package de.melanx.utilitix.data;

import com.mojang.math.Quadrant;
import de.melanx.utilitix.UtilitiX;
import de.melanx.utilitix.block.ModProperties;
import de.melanx.utilitix.content.redstone.ComparatorRedirectorBlock;
import de.melanx.utilitix.content.redstone.DimmableRedstoneLampBlock;
import de.melanx.utilitix.data.state.RailState;
import de.melanx.utilitix.registration.ModBlocks;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.blockstates.MultiVariantGenerator;
import net.minecraft.client.data.models.blockstates.PropertyDispatch;
import net.minecraft.client.data.models.model.ModelTemplate;
import net.minecraft.client.data.models.model.ModelTemplates;
import net.minecraft.client.data.models.model.TextureMapping;
import net.minecraft.client.data.models.model.TextureSlot;
import net.minecraft.client.renderer.block.dispatch.VariantMutator;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.AbstractFurnaceBlock;
import net.minecraft.world.level.block.BaseRailBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.state.properties.RailShape;
import org.moddingx.libx.datagen.DatagenContext;
import org.moddingx.libx.datagen.provider.model.BlockStateProviderBase;

import javax.annotation.Nullable;
import java.util.function.Supplier;

public class BlockStateProvider extends BlockStateProviderBase {

    public static final Identifier LINKED_REPEATER_PARENT = Identifier.fromNamespaceAndPath(UtilitiX.getInstance().modid, "block/linked_repeater_base");

    public static final Identifier TEXTURE_REPEATER_OFF = Identifier.fromNamespaceAndPath("minecraft", "block/repeater");
    public static final Identifier TEXTURE_REPEATER_ON = Identifier.fromNamespaceAndPath("minecraft", "block/repeater_on");

    public static final Identifier TEXTURE_TORCH_OFF = Identifier.fromNamespaceAndPath("minecraft", "block/redstone_torch_off");
    public static final Identifier TEXTURE_TORCH_ON = Identifier.fromNamespaceAndPath("minecraft", "block/redstone_torch");

    private static final TextureSlot REPEATER_SLOT = TextureSlot.create("repeater");
    private static final TextureSlot TORCH_SLOT = TextureSlot.create("torch");
    private static final ModelTemplate LINKED_REPEATER_TEMPLATE = new ModelTemplate(java.util.Optional.of(LINKED_REPEATER_PARENT), java.util.Optional.empty(), REPEATER_SLOT, TORCH_SLOT);

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
    protected void defaultState(Identifier id, Block block, Supplier<Identifier> model) {
        if (block == ModBlocks.linkedRepeater) {
            Identifier modelOn = this.createBlockModel(BlockStateProvider.blockModelId(id, "_on"), LINKED_REPEATER_TEMPLATE, new TextureMapping()
                    .put(REPEATER_SLOT, this.material(TEXTURE_REPEATER_ON))
                    .put(TORCH_SLOT, this.material(TEXTURE_TORCH_ON)));
            Identifier modelOff = this.createBlockModel(BlockStateProvider.blockModelId(id, "_off"), LINKED_REPEATER_TEMPLATE, new TextureMapping()
                    .put(REPEATER_SLOT, this.material(TEXTURE_REPEATER_OFF))
                    .put(TORCH_SLOT, this.material(TEXTURE_TORCH_OFF)));

            this.models().blockStateOutput.accept(MultiVariantGenerator.dispatch(block).with(
                    PropertyDispatch.initial(BlockStateProperties.HORIZONTAL_FACING, BlockStateProperties.POWER).generate((dir, power) ->
                            BlockModelGenerators.plainVariant(power > 0 ? modelOn : modelOff).with(BlockStateProvider.yRot(dir)))
            ));

            return;
        }

        if (block instanceof BaseRailBlock) {
            @SuppressWarnings("unchecked")
            Property<RailShape> shapeProperty = (Property<RailShape>) block.getStateDefinition().getProperties().stream()
                    .filter(p -> RailShape.class.equals(p.getValueClass())).findFirst().orElse(null);
            Property<Boolean> reverseProperty = block.getStateDefinition().getProperties().contains(ModProperties.REVERSE) ? ModProperties.REVERSE : null;
            if (shapeProperty == null) {
                throw new IllegalStateException("Rail block without shape property.");
            }

            MultiVariantGenerator generator;
            if (block.getStateDefinition().getProperties().contains(ModProperties.RAIL_SIDE)) {
                RailState stateLeft = new RailState(this, id, shapeProperty, reverseProperty, "left");
                RailState stateRight = new RailState(this, id, shapeProperty, reverseProperty, "right");
                generator = this.railDispatch(block, shapeProperty, reverseProperty, ModProperties.RAIL_SIDE, stateLeft, stateRight);
            } else if (block.getStateDefinition().getProperties().contains(BlockStateProperties.POWERED)) {
                RailState stateOff = new RailState(this, id, shapeProperty, reverseProperty);
                RailState stateOn = new RailState(this, id, shapeProperty, reverseProperty, "on");
                generator = this.railDispatch(block, shapeProperty, reverseProperty, BlockStateProperties.POWERED, stateOff, stateOn);
            } else {
                RailState state = new RailState(this, id, shapeProperty, reverseProperty);
                generator = this.railDispatch(block, shapeProperty, reverseProperty, state);
            }

            this.models().blockStateOutput.accept(generator);

            return;
        }

        if (block == ModBlocks.crudeFurnace) {
            Identifier modelOn = this.createBlockModel(BlockStateProvider.blockModelId(id, "_on"), ModelTemplates.CUBE_ORIENTABLE, new TextureMapping()
                    .put(TextureSlot.SIDE, this.material(Identifier.fromNamespaceAndPath(id.getNamespace(), "block/" + id.getPath() + "_side")))
                    .put(TextureSlot.FRONT, this.material(Identifier.fromNamespaceAndPath(id.getNamespace(), "block/" + id.getPath() + "_front_on")))
                    .put(TextureSlot.TOP, this.material(Identifier.fromNamespaceAndPath(id.getNamespace(), "block/" + id.getPath() + "_top"))));
            Identifier modelOff = this.createBlockModel(BlockStateProvider.blockModelId(id), ModelTemplates.CUBE_ORIENTABLE, new TextureMapping()
                    .put(TextureSlot.SIDE, this.material(Identifier.fromNamespaceAndPath(id.getNamespace(), "block/" + id.getPath() + "_side")))
                    .put(TextureSlot.FRONT, this.material(Identifier.fromNamespaceAndPath(id.getNamespace(), "block/" + id.getPath() + "_front")))
                    .put(TextureSlot.TOP, this.material(Identifier.fromNamespaceAndPath(id.getNamespace(), "block/" + id.getPath() + "_top"))));

            this.models().blockStateOutput.accept(MultiVariantGenerator.dispatch(block).with(
                    PropertyDispatch.initial(BlockStateProperties.HORIZONTAL_FACING, AbstractFurnaceBlock.LIT).generate((dir, lit) ->
                            BlockModelGenerators.plainVariant(lit ? modelOn : modelOff).with(BlockStateProvider.yRot(dir.getOpposite())))
            ));

            return;
        }

        if (block == ModBlocks.stoneWall) {
            this.wallBlock(block, this.mcId("block/stone"));

            return;
        }

        if (block == ModBlocks.dimmableRedstoneLamp) {
            this.models().blockStateOutput.accept(MultiVariantGenerator.dispatch(block).with(
                    PropertyDispatch.initial(DimmableRedstoneLampBlock.SIGNAL).generate(signal -> {
                        boolean isDefault = signal == 0 || signal == 15;
                        Identifier modelId;
                        if (isDefault) {
                            boolean on = signal == 15;
                            modelId = this.createBlockModel(BlockStateProvider.blockModelId(id, on ? "_" + signal : ""), ModelTemplates.CUBE_ALL,
                                    TextureMapping.cube(this.material(this.mcId("block/redstone_lamp" + (on ? "_on" : "")))));
                        } else {
                            modelId = this.createBlockModel(BlockStateProvider.blockModelId(id, "_" + signal), ModelTemplates.CUBE_ALL,
                                    TextureMapping.cube(this.material(Identifier.fromNamespaceAndPath(id.getNamespace(), "block/dimmable_redstone_lamp_" + signal))));
                        }

                        return BlockModelGenerators.plainVariant(modelId);
                    })
            ));

            return;
        }

        super.defaultState(id, block, model);
    }

    private MultiVariantGenerator railDispatch(Block block, Property<RailShape> shapeProperty, @Nullable Property<Boolean> reverseProperty, RailState state) {
        if (reverseProperty == null) {
            return MultiVariantGenerator.dispatch(block).with(
                    PropertyDispatch.initial(shapeProperty).generate(shape -> state.get(shape, false))
            );
        }

        return MultiVariantGenerator.dispatch(block).with(
                PropertyDispatch.initial(shapeProperty, reverseProperty).generate(state::get)
        );
    }

    private MultiVariantGenerator railDispatch(Block block, Property<RailShape> shapeProperty, @Nullable Property<Boolean> reverseProperty, Property<Boolean> extra, RailState stateFalse, RailState stateTrue) {
        if (reverseProperty == null) {
            return MultiVariantGenerator.dispatch(block).with(
                    PropertyDispatch.initial(shapeProperty, extra).generate((shape, value) -> (value ? stateTrue : stateFalse).get(shape, false))
            );
        }

        return MultiVariantGenerator.dispatch(block).with(
                PropertyDispatch.initial(shapeProperty, reverseProperty, extra).generate((shape, reverse, value) -> (value ? stateTrue : stateFalse).get(shape, reverse))
        );
    }

    private static VariantMutator yRot(Direction direction) {
        int degrees = ((int) direction.toYRot()) % 360;
        if (degrees < 0) {
            degrees += 360;
        }

        return switch(degrees) {
            case 90 -> BlockModelGenerators.Y_ROT_90;
            case 180 -> BlockModelGenerators.Y_ROT_180;
            case 270 -> BlockModelGenerators.Y_ROT_270;
            default -> VariantMutator.Y_ROT.withValue(Quadrant.R0);
        };
    }

    @Override
    protected Identifier defaultModel(Identifier id, Block block) {
        if (block instanceof ComparatorRedirectorBlock) {
            Identifier top = Identifier.fromNamespaceAndPath(UtilitiX.getInstance().modid, "block/comparator_redirector_top");
            Identifier bottom = Identifier.fromNamespaceAndPath(UtilitiX.getInstance().modid, "block/comparator_redirector_bottom");
            if (((ComparatorRedirectorBlock) block).direction == Direction.DOWN) {
                Identifier tmp = top;
                top = bottom;
                bottom = tmp;
            }

            TextureMapping mapping = new TextureMapping()
                    .put(TextureSlot.SIDE, this.material(Identifier.fromNamespaceAndPath(UtilitiX.getInstance().modid, "block/comparator_redirector_side")))
                    .put(TextureSlot.TOP, this.material(bottom))
                    .put(TextureSlot.BOTTOM, this.material(top));
            return this.createBlockModel(blockModelId(id), ModelTemplates.CUBE_BOTTOM_TOP, mapping);
        }

        return super.defaultModel(id, block);
    }

    private Identifier mcId(String path) {
        return Identifier.fromNamespaceAndPath("minecraft", path);
    }

    @Override
    public Material material(Identifier id) {
        return super.material(id);
    }

    @Override
    public Identifier createBlockModel(Identifier id, ModelTemplate template, TextureMapping mapping) {
        return super.createBlockModel(id, template, mapping);
    }

    public static Identifier blockModelId(Identifier id, String suffix) {
        return BlockStateProviderBase.blockModelId(id, suffix);
    }

    public static Identifier blockModelId(Identifier id) {
        return BlockStateProviderBase.blockModelId(id);
    }
}
