package de.melanx.utilitix.content.track.carts;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MinecartRenderer;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nonnull;

public class MinecartRendererX<T extends BaseCart> extends MinecartRenderer<T> {

    public MinecartRendererX(EntityRendererProvider.Context context, ModelLayerLocation layerLocation) {
        super(context, layerLocation);
    }

    @Override
    protected void renderMinecartContents(@Nonnull T entity, float partialTick, @Nonnull BlockState state, @Nonnull PoseStack poseStack, @Nonnull MultiBufferSource buffer, int packedLight) {
        if (!(entity instanceof StonecutterCart stonecutterCart)) {
            super.renderMinecartContents(entity, partialTick, state, poseStack, buffer, packedLight);
            return;
        }

        poseStack.pushPose();
        Direction dirCorrected = Direction.fromYRot(entity.yRot);
        if (entity.flipped || dirCorrected.getAxisDirection() == Direction.AxisDirection.POSITIVE) {
            poseStack.translate(0.5, 0.5, 0.5);
            poseStack.mulPose(Axis.YP.rotationDegrees((entity.flipped ? 180 : 0) + (dirCorrected.getAxisDirection() == Direction.AxisDirection.POSITIVE ? 180 : 0)));
            poseStack.translate(-0.5, -0.5, -0.5);
        }

        switch(stonecutterCart.getMode()) {
            case LEFT -> {
                poseStack.translate(0.5, 0.65, 0.5);
                poseStack.mulPose(Axis.YP.rotationDegrees(90));
                poseStack.mulPose(Axis.XP.rotationDegrees(90));
                poseStack.translate(-0.5, -0.1, -0.5);
            }
            case RIGHT -> {
                poseStack.translate(0.5, 0.65, 0.5);
                poseStack.mulPose(Axis.YP.rotationDegrees(90));
                poseStack.mulPose(Axis.XP.rotationDegrees(-90));
                poseStack.translate(-0.5, -0.1, -0.5);
            }
            case TOP_LEFT -> {
                poseStack.translate(0.5, 0.55, 0.5);
                poseStack.mulPose(Axis.YP.rotationDegrees(90));
                poseStack.mulPose(Axis.XP.rotationDegrees(55));
                poseStack.translate(-0.5, -0.15, -0.5);
            }
            case TOP_RIGHT -> {
                poseStack.translate(0.5, 0.55, 0.5);
                poseStack.mulPose(Axis.YP.rotationDegrees(90));
                poseStack.mulPose(Axis.XP.rotationDegrees(-55));
                poseStack.translate(-0.5, -0.15, -0.5);
            }
            case FRONT -> {
                poseStack.translate(0.5, 0.65, 0.5);
                poseStack.mulPose(Axis.XP.rotationDegrees(90));
                poseStack.translate(-0.5, 0.2, -0.5);
            }
        }

        super.renderMinecartContents(entity, partialTick, state, poseStack, buffer, packedLight);
        poseStack.popPose();

    }
}
