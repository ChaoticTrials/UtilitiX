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

public class MinecartRendererX<T extends Cart> extends MinecartRenderer<T> {

    public MinecartRendererX(EntityRendererProvider.Context context, ModelLayerLocation layerLocation) {
        super(context, layerLocation);
    }

    @Override
    protected void renderMinecartContents(@Nonnull T entity, float partialTick, @Nonnull BlockState state, @Nonnull PoseStack matrixStack, @Nonnull MultiBufferSource buffer, int packedLight) {
        if (entity instanceof StonecutterCart) {
            matrixStack.pushPose();
            Direction dirCorrected = Direction.fromYRot(entity.yRot);
            if (entity.flipped || dirCorrected.getAxisDirection() == Direction.AxisDirection.POSITIVE) {
                matrixStack.translate(0.5, 0.5, 0.5);
                matrixStack.mulPose(Axis.YP.rotationDegrees((entity.flipped ? 180 : 0) + (dirCorrected.getAxisDirection() == Direction.AxisDirection.POSITIVE ? 180 : 0)));
                matrixStack.translate(-0.5, -0.5, -0.5);
            }
            switch (((StonecutterCart) entity).getMode()) {
                case LEFT -> {
                    matrixStack.translate(0.5, 0.65, 0.5);
                    matrixStack.mulPose(Axis.YP.rotationDegrees(90));
                    matrixStack.mulPose(Axis.XP.rotationDegrees(90));
                    matrixStack.translate(-0.5, -0.1, -0.5);
                }
                case RIGHT -> {
                    matrixStack.translate(0.5, 0.65, 0.5);
                    matrixStack.mulPose(Axis.YP.rotationDegrees(90));
                    matrixStack.mulPose(Axis.XP.rotationDegrees(-90));
                    matrixStack.translate(-0.5, -0.1, -0.5);
                }
                case TOP_LEFT -> {
                    matrixStack.translate(0.5, 0.55, 0.5);
                    matrixStack.mulPose(Axis.YP.rotationDegrees(90));
                    matrixStack.mulPose(Axis.XP.rotationDegrees(55));
                    matrixStack.translate(-0.5, -0.15, -0.5);
                }
                case TOP_RIGHT -> {
                    matrixStack.translate(0.5, 0.55, 0.5);
                    matrixStack.mulPose(Axis.YP.rotationDegrees(90));
                    matrixStack.mulPose(Axis.XP.rotationDegrees(-55));
                    matrixStack.translate(-0.5, -0.15, -0.5);
                }
                case FRONT -> {
                    matrixStack.translate(0.5, 0.65, 0.5);
                    matrixStack.mulPose(Axis.XP.rotationDegrees(90));
                    matrixStack.translate(-0.5, 0.2, -0.5);
                }
            }
            super.renderMinecartContents(entity, partialTick, state, matrixStack, buffer, packedLight);
            matrixStack.popPose();
        } else {
            super.renderMinecartContents(entity, partialTick, state, matrixStack, buffer, packedLight);
        }
    }
}
