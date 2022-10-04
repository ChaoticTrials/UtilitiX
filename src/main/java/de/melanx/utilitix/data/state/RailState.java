package de.melanx.utilitix.data.state;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.state.properties.RailShape;
import net.minecraftforge.client.model.generators.ConfiguredModel;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.client.model.generators.VariantBlockStateBuilder;
import org.moddingx.libx.datagen.provider.BlockStateProviderBase;

import javax.annotation.Nullable;
import java.util.function.UnaryOperator;

public class RailState {

    public static final ResourceLocation STRAIGHT_RAIL_PARENT = new ResourceLocation("minecraft", "block/rail_flat");
    public static final ResourceLocation RAISED_RAIL_NE_PARENT = new ResourceLocation("minecraft", "block/template_rail_raised_ne");
    public static final ResourceLocation RAISED_RAIL_SW_PARENT = new ResourceLocation("minecraft", "block/template_rail_raised_sw");
    public static final ResourceLocation CURVED_RAIL_PARENT = new ResourceLocation("minecraft", "block/rail_curved");

    public final Property<RailShape> shapeProperty;
    @Nullable
    public final Property<Boolean> reverseProperty;
    public final UnaryOperator<VariantBlockStateBuilder.PartialBlockstate> variants;

    public RailState(Property<RailShape> shapeProperty, @Nullable Property<Boolean> reverseProperty) {
        this(shapeProperty, reverseProperty, UnaryOperator.identity());
    }
    
    public RailState(Property<RailShape> shapeProperty, @Nullable Property<Boolean> reverseProperty, UnaryOperator<VariantBlockStateBuilder.PartialBlockstate> variants) {
        this.shapeProperty = shapeProperty;
        this.reverseProperty = reverseProperty;
        this.variants = variants;
    }
    
    public void generate(BlockStateProviderBase provider, VariantBlockStateBuilder builder, ResourceLocation id) {
        this.doGenerate(provider, builder, id, "");
    }

    public void generate(BlockStateProviderBase provider, VariantBlockStateBuilder builder, ResourceLocation id, String modelId) {
        this.doGenerate(provider, builder, id, "_" + modelId);
    }
    
    @SuppressWarnings("ConstantConditions")
    private void doGenerate(BlockStateProviderBase provider, VariantBlockStateBuilder builder, ResourceLocation id, String modelId) {
        ModelFile modelStraight = this.createModel(provider, id, STRAIGHT_RAIL_PARENT, modelId, modelId, RailShape.NORTH_SOUTH, RailShape.EAST_WEST);
        ModelFile modelCorner = this.createModel(provider, id, CURVED_RAIL_PARENT, "_corner" + modelId, "_corner" + modelId, RailShape.NORTH_EAST, RailShape.NORTH_WEST, RailShape.SOUTH_EAST, RailShape.SOUTH_WEST);
        ModelFile modelRaisedNE;
        ModelFile modelRaisedSW;
        if (this.reverseProperty != null) {
            modelRaisedNE = this.createModel(provider, id, RAISED_RAIL_NE_PARENT, "_ascending_ne" + modelId, modelId, RailShape.ASCENDING_NORTH, RailShape.ASCENDING_SOUTH, RailShape.ASCENDING_EAST, RailShape.ASCENDING_WEST);
            modelRaisedSW = this.createModel(provider, id, RAISED_RAIL_SW_PARENT, "_ascending_sw" + modelId, modelId, RailShape.ASCENDING_NORTH, RailShape.ASCENDING_SOUTH, RailShape.ASCENDING_EAST, RailShape.ASCENDING_WEST);
        } else {
            modelRaisedNE = this.createModel(provider, id, RAISED_RAIL_NE_PARENT, "_ascending_ne" + modelId, modelId, RailShape.ASCENDING_NORTH, RailShape.ASCENDING_EAST);
            modelRaisedSW = this.createModel(provider, id, RAISED_RAIL_SW_PARENT, "_ascending_sw" + modelId, modelId, RailShape.ASCENDING_SOUTH, RailShape.ASCENDING_WEST);
        }
        if (modelCorner != null && this.reverseProperty != null) {
            throw new IllegalStateException("Can't use corner rail models together with reverse properties.");
        }
        for (RailShape shape : this.shapeProperty.getPossibleValues()) {
            switch (shape) {
                case NORTH_SOUTH:
                    if (this.reverseProperty == null) {
                        this.partial(builder, shape, false).addModels(new ConfiguredModel(modelStraight, 0, 0, false));
                    } else {
                        this.partial(builder, shape, false).addModels(new ConfiguredModel(modelStraight, 0, 0, false));
                        this.partial(builder, shape, true).addModels(new ConfiguredModel(modelStraight, 0, 180, false));
                    }
                    break;
                case EAST_WEST:
                    if (this.reverseProperty == null) {
                        this.partial(builder, shape, false).addModels(new ConfiguredModel(modelStraight, 0, 90, false));
                    } else {
                        this.partial(builder, shape, false).addModels(new ConfiguredModel(modelStraight, 0, 90, false));
                        this.partial(builder, shape, true).addModels(new ConfiguredModel(modelStraight, 0, 270, false));
                    }
                    break;
                case ASCENDING_EAST:
                    if (this.reverseProperty == null) {
                        this.partial(builder, shape, false).addModels(new ConfiguredModel(modelRaisedNE, 0, 90, false));
                    } else {
                        this.partial(builder, shape, false).addModels(new ConfiguredModel(modelRaisedNE, 0, 90, false));
                        this.partial(builder, shape, true).addModels(new ConfiguredModel(modelRaisedSW, 0, 270, false));
                    }
                    break;
                case ASCENDING_WEST:
                    if (this.reverseProperty == null) {
                        this.partial(builder, shape, false).addModels(new ConfiguredModel(modelRaisedSW, 0, 90, false));
                    } else {
                        this.partial(builder, shape, false).addModels(new ConfiguredModel(modelRaisedSW, 0, 90, false));
                        this.partial(builder, shape, true).addModels(new ConfiguredModel(modelRaisedNE, 0, 270, false));
                    }
                    break;
                case ASCENDING_NORTH:
                    if (this.reverseProperty == null) {
                        this.partial(builder, shape, false).addModels(new ConfiguredModel(modelRaisedNE, 0, 0, false));
                    } else {
                        this.partial(builder, shape, false).addModels(new ConfiguredModel(modelRaisedNE, 0, 0, false));
                        this.partial(builder, shape, true).addModels(new ConfiguredModel(modelRaisedSW, 0, 180, false));
                    }
                    break;
                case ASCENDING_SOUTH:
                    if (this.reverseProperty == null) {
                        this.partial(builder, shape, false).addModels(new ConfiguredModel(modelRaisedSW, 0, 0, false));
                    } else {
                        this.partial(builder, shape, false).addModels(new ConfiguredModel(modelRaisedSW, 0, 0, false));
                        this.partial(builder, shape, true).addModels(new ConfiguredModel(modelRaisedNE, 0, 180, false));
                    }
                    break;
                case SOUTH_EAST:
                    this.partial(builder, shape, false).addModels(new ConfiguredModel(modelCorner, 0, 0, false));
                    break;
                case SOUTH_WEST:
                    this.partial(builder, shape, false).addModels(new ConfiguredModel(modelCorner, 0, 90, false));
                    break;
                case NORTH_WEST:
                    this.partial(builder, shape, false).addModels(new ConfiguredModel(modelCorner, 0, 180, false));
                    break;
                case NORTH_EAST:
                    this.partial(builder, shape, false).addModels(new ConfiguredModel(modelCorner, 0, 270, false));
                    break;
            }
        }
    }
    
    private VariantBlockStateBuilder.PartialBlockstate partial(VariantBlockStateBuilder builder, RailShape shape, boolean reverse) {
        VariantBlockStateBuilder.PartialBlockstate partial = builder.partialState().with(this.shapeProperty, shape);
        if (this.reverseProperty != null) {
            partial = partial.with(this.reverseProperty, reverse);
        }
        return this.variants.apply(partial);
    }
    
    private ModelFile createModel(BlockStateProviderBase provider, ResourceLocation id, ResourceLocation parent, String modelId, String textureId, RailShape... shapes) {
        boolean needsModel = false;
        for (RailShape shape : shapes) {
            if (this.shapeProperty.getPossibleValues().contains(shape)) {
                needsModel = true;
                break;
            }
        }
        if (needsModel) {
            return provider.models().withExistingParent(id.getPath() + modelId, parent)
                    .texture("rail", new ResourceLocation(id.getNamespace(), "block/" + id.getPath() + textureId))
                    .renderType("cutout");
        } else {
            return null;
        }
    }
}
