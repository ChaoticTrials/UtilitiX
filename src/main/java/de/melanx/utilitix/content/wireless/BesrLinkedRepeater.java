package de.melanx.utilitix.content.wireless;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import de.melanx.utilitix.registration.ModItems;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import org.moddingx.libx.render.ClientTickHandler;

import javax.annotation.Nonnull;

public class BesrLinkedRepeater implements BlockEntityRenderer<TileLinkedRepeater> {

    @Override
    public void render(@Nonnull TileLinkedRepeater blockEntity, float partialTicks, @Nonnull PoseStack poseStack, @Nonnull MultiBufferSource buffer, int combinedLight, int combinedOverlay) {
        if (blockEntity.getBlockState().getValue(BlockStateProperties.EYE)) {
            poseStack.pushPose();
            poseStack.translate(0.5, 0.55, 0.5);
            poseStack.scale(0.8f, 0.8f, 0.8f);
            poseStack.mulPose(Vector3f.YP.rotationDegrees(ClientTickHandler.ticksInGame + partialTicks));
            Minecraft.getInstance().getItemRenderer().renderStatic(new ItemStack(ModItems.linkedCrystal), ItemTransforms.TransformType.GROUND, combinedLight, OverlayTexture.NO_OVERLAY, poseStack, buffer, 0);
            poseStack.popPose();
        }
    }
}
