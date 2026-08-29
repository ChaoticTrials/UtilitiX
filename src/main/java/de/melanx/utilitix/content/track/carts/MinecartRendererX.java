package de.melanx.utilitix.content.track.carts;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import de.melanx.utilitix.content.track.carts.stonecutter.StonecutterCartMode;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.block.BlockModelRenderState;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MinecartRenderer;
import net.minecraft.client.renderer.entity.state.MinecartRenderState;
import net.minecraft.core.Direction;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class MinecartRendererX<T extends BaseCart> extends MinecartRenderer {

    public MinecartRendererX(EntityRendererProvider.Context context, ModelLayerLocation layerLocation) {
        super(context, layerLocation);
    }

    @Nonnull
    @Override
    public MinecartRendererXState createRenderState() {
        return new MinecartRendererXState();
    }

    @Override
    public void extractRenderState(@Nonnull net.minecraft.world.entity.vehicle.minecart.AbstractMinecart entity, @Nonnull MinecartRenderState state, float partialTick) {
        super.extractRenderState(entity, state, partialTick);
        if (state instanceof MinecartRendererXState xState && entity instanceof StonecutterCart stonecutterCart) {
            xState.stonecutterMode = stonecutterCart.getMode();
            xState.flipped = stonecutterCart.isFlipped();
        } else if (state instanceof MinecartRendererXState xState) {
            xState.stonecutterMode = null;
        }
    }

    @Override
    protected void submitMinecartContents(@Nonnull MinecartRenderState state, @Nonnull BlockModelRenderState blockModel, @Nonnull PoseStack poseStack, @Nonnull SubmitNodeCollector submitNodeCollector, int lightCoords) {
        if (!(state instanceof MinecartRendererXState xState) || xState.stonecutterMode == null) {
            super.submitMinecartContents(state, blockModel, poseStack, submitNodeCollector, lightCoords);
            return;
        }

        poseStack.pushPose();
        Direction dirCorrected = Direction.fromYRot(state.yRot);
        if (xState.flipped || dirCorrected.getAxisDirection() == Direction.AxisDirection.POSITIVE) {
            poseStack.translate(0.5, 0.5, 0.5);
            poseStack.mulPose(Axis.YP.rotationDegrees((xState.flipped ? 180 : 0) + (dirCorrected.getAxisDirection() == Direction.AxisDirection.POSITIVE ? 180 : 0)));
            poseStack.translate(-0.5, -0.5, -0.5);
        }

        switch(xState.stonecutterMode) {
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
            case TOP -> {
                // no additional transform
            }
        }

        super.submitMinecartContents(state, blockModel, poseStack, submitNodeCollector, lightCoords);
        poseStack.popPose();
    }

    public static class MinecartRendererXState extends MinecartRenderState {

        @Nullable
        public StonecutterCartMode stonecutterMode;
        public boolean flipped;
    }
}
