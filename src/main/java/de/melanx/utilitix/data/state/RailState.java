package de.melanx.utilitix.data.state;

import de.melanx.utilitix.data.BlockStateProvider;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.MultiVariant;
import net.minecraft.client.data.models.model.ModelTemplate;
import net.minecraft.client.data.models.model.ModelTemplates;
import net.minecraft.client.data.models.model.TextureMapping;
import net.minecraft.client.data.models.model.TextureSlot;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.state.properties.RailShape;

import javax.annotation.Nullable;

public class RailState {

    public final Property<RailShape> shapeProperty;
    @Nullable
    public final Property<Boolean> reverseProperty;

    private final MultiVariant modelStraight;
    private final MultiVariant modelCorner;
    private final MultiVariant modelRaisedNE;
    private final MultiVariant modelRaisedSW;

    public RailState(BlockStateProvider provider, Identifier id, Property<RailShape> shapeProperty, @Nullable Property<Boolean> reverseProperty) {
        this(provider, id, shapeProperty, reverseProperty, "");
    }

    public RailState(BlockStateProvider provider, Identifier id, Property<RailShape> shapeProperty, @Nullable Property<Boolean> reverseProperty, String modelId) {
        this.shapeProperty = shapeProperty;
        this.reverseProperty = reverseProperty;

        String suffix = modelId.isEmpty() ? "" : "_" + modelId;
        this.modelStraight = this.createModel(provider, id, ModelTemplates.RAIL_FLAT, suffix, suffix, RailShape.NORTH_SOUTH, RailShape.EAST_WEST);
        this.modelCorner = this.createModel(provider, id, ModelTemplates.RAIL_CURVED, "_corner" + suffix, "_corner" + suffix, RailShape.NORTH_EAST, RailShape.NORTH_WEST, RailShape.SOUTH_EAST, RailShape.SOUTH_WEST);

        if (this.modelCorner != null && reverseProperty != null) {
            throw new IllegalStateException("Can't use corner rail models together with reverse properties.");
        }

        if (reverseProperty != null) {
            this.modelRaisedNE = this.createModel(provider, id, ModelTemplates.RAIL_RAISED_NE, "_ascending_ne" + suffix, suffix, RailShape.ASCENDING_NORTH, RailShape.ASCENDING_SOUTH, RailShape.ASCENDING_EAST, RailShape.ASCENDING_WEST);
            this.modelRaisedSW = this.createModel(provider, id, ModelTemplates.RAIL_RAISED_SW, "_ascending_sw" + suffix, suffix, RailShape.ASCENDING_NORTH, RailShape.ASCENDING_SOUTH, RailShape.ASCENDING_EAST, RailShape.ASCENDING_WEST);
        } else {
            this.modelRaisedNE = this.createModel(provider, id, ModelTemplates.RAIL_RAISED_NE, "_ascending_ne" + suffix, suffix, RailShape.ASCENDING_NORTH, RailShape.ASCENDING_EAST);
            this.modelRaisedSW = this.createModel(provider, id, ModelTemplates.RAIL_RAISED_SW, "_ascending_sw" + suffix, suffix, RailShape.ASCENDING_SOUTH, RailShape.ASCENDING_WEST);
        }
    }

    public MultiVariant get(RailShape shape, boolean reverse) {
        return switch(shape) {
            case NORTH_SOUTH -> reverse ? this.modelStraight.with(BlockModelGenerators.Y_ROT_180) : this.modelStraight;
            case EAST_WEST ->
                    reverse ? this.modelStraight.with(BlockModelGenerators.Y_ROT_270) : this.modelStraight.with(BlockModelGenerators.Y_ROT_90);
            case ASCENDING_EAST ->
                    reverse ? this.modelRaisedSW.with(BlockModelGenerators.Y_ROT_270) : this.modelRaisedNE.with(BlockModelGenerators.Y_ROT_90);
            case ASCENDING_WEST ->
                    reverse ? this.modelRaisedNE.with(BlockModelGenerators.Y_ROT_270) : this.modelRaisedSW.with(BlockModelGenerators.Y_ROT_90);
            case ASCENDING_NORTH -> reverse ? this.modelRaisedSW.with(BlockModelGenerators.Y_ROT_180) : this.modelRaisedNE;
            case ASCENDING_SOUTH -> reverse ? this.modelRaisedNE.with(BlockModelGenerators.Y_ROT_180) : this.modelRaisedSW;
            case SOUTH_EAST -> this.modelCorner;
            case SOUTH_WEST -> this.modelCorner.with(BlockModelGenerators.Y_ROT_90);
            case NORTH_WEST -> this.modelCorner.with(BlockModelGenerators.Y_ROT_180);
            case NORTH_EAST -> this.modelCorner.with(BlockModelGenerators.Y_ROT_270);
        };
    }

    @Nullable
    private MultiVariant createModel(BlockStateProvider provider, Identifier id, ModelTemplate template, String modelSuffix, String textureSuffix, RailShape... shapes) {
        boolean needsModel = false;
        for (RailShape shape : shapes) {
            if (this.shapeProperty.getPossibleValues().contains(shape)) {
                needsModel = true;
                break;
            }
        }

        if (!needsModel) {
            return null;
        }

        Identifier modelId = BlockStateProvider.blockModelId(id, modelSuffix);
        Identifier textureId = Identifier.fromNamespaceAndPath(id.getNamespace(), "block/" + id.getPath() + textureSuffix);
        TextureMapping mapping = new TextureMapping().put(TextureSlot.RAIL, provider.material(textureId));
        return BlockModelGenerators.plainVariant(provider.createBlockModel(modelId, template, mapping));
    }
}
