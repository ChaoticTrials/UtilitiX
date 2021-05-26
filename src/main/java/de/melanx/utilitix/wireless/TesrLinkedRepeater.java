package de.melanx.utilitix.wireless;

import com.mojang.blaze3d.matrix.MatrixStack;
import de.melanx.utilitix.registration.ModItems;
import io.github.noeppi_noeppi.libx.render.ClientTickHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.item.ItemStack;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.math.vector.Vector3f;

import javax.annotation.Nonnull;

public class TesrLinkedRepeater extends TileEntityRenderer<TileLinkedRepeater> {

    public TesrLinkedRepeater(TileEntityRendererDispatcher rendererDispatcherIn) {
        super(rendererDispatcherIn);
    }

    @Override
    public void render(@Nonnull TileLinkedRepeater tile, float partialTicks, @Nonnull MatrixStack matrixStack, @Nonnull IRenderTypeBuffer buffer, int light, int overlay) {
        if (tile.getBlockState().get(BlockStateProperties.EYE)) {
            matrixStack.push();
            matrixStack.translate(0.5, 0.55, 0.5);
            matrixStack.scale(0.8f, 0.8f, 0.8f);
            matrixStack.rotate(Vector3f.YP.rotationDegrees(ClientTickHandler.ticksInGame + partialTicks));
            Minecraft.getInstance().getItemRenderer().renderItem(new ItemStack(ModItems.linkedCrystal), ItemCameraTransforms.TransformType.GROUND, light, OverlayTexture.NO_OVERLAY, matrixStack, buffer);
            matrixStack.pop();
        }
    }
}
