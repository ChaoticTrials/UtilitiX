package de.melanx.utilitix.content.brewery;

import com.mojang.blaze3d.matrix.MatrixStack;
import io.github.noeppi_noeppi.libx.block.tesr.HorizontalRotatedTesr;
import io.github.noeppi_noeppi.libx.render.ClientTickHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.vector.Vector3f;

import javax.annotation.Nonnull;

public class TesrAdvancedBrewery extends HorizontalRotatedTesr<TileAdvancedBrewery> {

    public TesrAdvancedBrewery(TileEntityRendererDispatcher rendererDispatcherIn) {
        super(rendererDispatcherIn);
    }

    @Override
    protected void doRender(@Nonnull TileAdvancedBrewery tile, float partialTicks, @Nonnull MatrixStack matrixStack, @Nonnull IRenderTypeBuffer buffer, int light, int overlay) {
        ItemStack ingredient = tile.getInventory().getStackInSlot(0);
        if (!ingredient.isEmpty()) {
            matrixStack.push();
            matrixStack.translate(0.5, 0.66, 0.65);
            matrixStack.scale(0.45f, 0.45f, 0.45f);
            matrixStack.rotate(Vector3f.YP.rotationDegrees(ClientTickHandler.ticksInGame + partialTicks));
            Minecraft.getInstance().getItemRenderer().renderItem(ingredient, ItemCameraTransforms.TransformType.GROUND, light, overlay, matrixStack, buffer);
            matrixStack.pop();
        }
        ItemStack main = tile.getInventory().getStackInSlot(3);
        if (!main.isEmpty()) {
            matrixStack.push();
            matrixStack.translate(0.5, 0.15, 0.18);
            matrixStack.scale(0.7f, 0.7f, 0.7f);
            matrixStack.rotate(Vector3f.YP.rotationDegrees(-(ClientTickHandler.ticksInGame + partialTicks)));
            Minecraft.getInstance().getItemRenderer().renderItem(main, ItemCameraTransforms.TransformType.GROUND, light, overlay, matrixStack, buffer);
            matrixStack.pop();
        }
        ItemStack p1 = tile.getInventory().getStackInSlot(1);
        if (!p1.isEmpty()) {
            matrixStack.push();
            matrixStack.translate(0.82, 0.42, 0.68);
            matrixStack.scale(0.6f, 0.6f, 0.6f);
            matrixStack.rotate(Vector3f.YP.rotationDegrees(-(ClientTickHandler.ticksInGame + partialTicks)));
            Minecraft.getInstance().getItemRenderer().renderItem(p1, ItemCameraTransforms.TransformType.GROUND, light, overlay, matrixStack, buffer);
            matrixStack.pop();
        }
        ItemStack p2 = tile.getInventory().getStackInSlot(2);
        if (!p2.isEmpty()) {
            matrixStack.push();
            matrixStack.translate(0.18, 0.42, 0.68);
            matrixStack.scale(0.6f, 0.6f, 0.6f);
            matrixStack.rotate(Vector3f.YP.rotationDegrees(-(ClientTickHandler.ticksInGame + partialTicks)));
            Minecraft.getInstance().getItemRenderer().renderItem(p2, ItemCameraTransforms.TransformType.GROUND, light, overlay, matrixStack, buffer);
            matrixStack.pop();
        }
    }
}
