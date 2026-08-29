package de.melanx.utilitix.content.shulkerboat;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.monster.shulker.ShulkerModel;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.BoatRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.BoatRenderState;
import net.minecraft.client.renderer.entity.state.ShulkerRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;

import javax.annotation.Nonnull;

public class ShulkerBoatRenderer extends BoatRenderer {

    private static final Identifier SHULKER_TEXTURE = Identifier.fromNamespaceAndPath("minecraft", "textures/" + Sheets.DEFAULT_SHULKER_TEXTURE_LOCATION.texture().getPath() + ".png");

    private final ShulkerModel shulkerModel;

    public ShulkerBoatRenderer(EntityRendererProvider.Context context, ModelLayerLocation modelId) {
        super(context, modelId);
        this.shulkerModel = new ShulkerModel(context.bakeLayer(ModelLayers.SHULKER));
    }

    @Override
    protected void submitTypeAdditions(@Nonnull BoatRenderState state, @Nonnull PoseStack poseStack, @Nonnull SubmitNodeCollector submitNodeCollector, int lightCoords) {
        super.submitTypeAdditions(state, poseStack, submitNodeCollector, lightCoords);

        poseStack.pushPose();
        poseStack.translate(-0.475, -1.01, 0);
        poseStack.scale(0.8F, 0.8F, 0.8F);
        poseStack.mulPose(Axis.YP.rotationDegrees(90.0F));
        submitNodeCollector.submitModel(this.shulkerModel, new ShulkerRenderState(), poseStack, SHULKER_TEXTURE, lightCoords, OverlayTexture.NO_OVERLAY, state.outlineColor, null);
        poseStack.popPose();
    }
}
