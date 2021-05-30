package de.melanx.utilitix.content.track.carts;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.MinecartRenderer;
import net.minecraft.util.Direction;
import net.minecraft.util.math.vector.Vector3f;

import javax.annotation.Nonnull;

public class MinecartRendererX<T extends EntityCart> extends MinecartRenderer<T> {

    public MinecartRendererX(EntityRendererManager renderManagerIn) {
        super(renderManagerIn);
    }

    @Override
    protected void renderBlockState(@Nonnull T entity, float partialTicks, @Nonnull BlockState state, @Nonnull MatrixStack matrixStack, @Nonnull IRenderTypeBuffer buffer, int light) {
        if (entity instanceof EntityStonecutterCart) {
            matrixStack.push();
            Direction dirCorrected = Direction.fromAngle(entity.rotationYaw);
            if (entity.isInReverse || dirCorrected.getAxisDirection() == Direction.AxisDirection.POSITIVE) {
                matrixStack.translate(0.5, 0.5, 0.5);
                matrixStack.rotate(Vector3f.YP.rotationDegrees((entity.isInReverse ? 180 : 0) + (dirCorrected.getAxisDirection() == Direction.AxisDirection.POSITIVE ? 180 : 0)));
                matrixStack.translate(-0.5, -0.5, -0.5);
            }
            switch (((EntityStonecutterCart) entity).getMode()) {
                case LEFT:
                    matrixStack.translate(0.5, 0.65, 0.5);
                    matrixStack.rotate(Vector3f.YP.rotationDegrees(90));
                    matrixStack.rotate(Vector3f.XP.rotationDegrees(90));
                    matrixStack.translate(-0.5, -0.1, -0.5);
                    break;
                case RIGHT:
                    matrixStack.translate(0.5, 0.65, 0.5);
                    matrixStack.rotate(Vector3f.YP.rotationDegrees(90));
                    matrixStack.rotate(Vector3f.XP.rotationDegrees(-90));
                    matrixStack.translate(-0.5, -0.1, -0.5);
                    break;
                case TOP_LEFT:
                    matrixStack.translate(0.5, 0.55, 0.5);
                    matrixStack.rotate(Vector3f.YP.rotationDegrees(90));
                    matrixStack.rotate(Vector3f.XP.rotationDegrees(55));
                    matrixStack.translate(-0.5, -0.15, -0.5);
                    break;
                case TOP_RIGHT:
                    matrixStack.translate(0.5, 0.55, 0.5);
                    matrixStack.rotate(Vector3f.YP.rotationDegrees(90));
                    matrixStack.rotate(Vector3f.XP.rotationDegrees(-55));
                    matrixStack.translate(-0.5, -0.15, -0.5);
                    break;
                case FRONT:
                    matrixStack.translate(0.5, 0.65, 0.5);
                    matrixStack.rotate(Vector3f.XP.rotationDegrees(90));
                    matrixStack.translate(-0.5, 0.2, -0.5);
                    break;
            }
            super.renderBlockState(entity, partialTicks, state, matrixStack, buffer, light);
            matrixStack.pop();
        } else {
            super.renderBlockState(entity, partialTicks, state, matrixStack, buffer, light);
        }
    }
}
