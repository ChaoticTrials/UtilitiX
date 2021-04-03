package de.melanx.utilitix.item.bells;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import io.github.noeppi_noeppi.libx.annotation.Model;
import net.minecraft.block.Blocks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.tileentity.ItemStackTileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.tileentity.BellTileEntity;
import net.minecraft.util.LazyValue;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3f;

import javax.annotation.Nonnull;

public class RenderHandBell extends ItemStackTileEntityRenderer {

    @Model(namespace = "minecraft", value = "item/stick")
    public static IBakedModel stickModel = null;

    private final LazyValue<ItemStack> stick = new LazyValue<>(() -> new ItemStack(Items.STICK));
    private final LazyValue<BellTileEntity> tile = new LazyValue<>(BellTileEntity::new);
    private TileEntityRenderer<BellTileEntity> tileRender = null;

    @Override
    public void func_239207_a_(@Nonnull ItemStack stack, @Nonnull ItemCameraTransforms.TransformType transform, @Nonnull MatrixStack matrixStack, @Nonnull IRenderTypeBuffer buffer, int light, int overlay) {
        Minecraft mc = Minecraft.getInstance();
        IVertexBuilder vertex = buffer.getBuffer(RenderType.getCutout());
        matrixStack.push();
        matrixStack.scale(0.7F, 0.7F, 0.7F);
        matrixStack.translate(0, 0, 0.25F);
        mc.getItemRenderer().renderModel(stickModel, this.stick.getValue(), light, OverlayTexture.NO_OVERLAY, matrixStack, vertex);
        matrixStack.pop();
        if (mc.world != null) {
            BellTileEntity tile = this.tile.getValue();
            tile.setWorldAndPos(mc.world, BlockPos.ZERO);
            tile.cachedBlockState = Blocks.BELL.getDefaultState();
            if (this.tileRender == null) {
                this.tileRender = TileEntityRendererDispatcher.instance.getRenderer(tile);
            }
            matrixStack.push();
            matrixStack.scale(0.7F, 0.7F, 0.7F);
            matrixStack.translate(0, 0F, 0.25F);
            matrixStack.rotate(Vector3f.ZP.rotationDegrees(-45));
            matrixStack.rotate(Vector3f.XP.rotationDegrees(180));
            matrixStack.translate(-0.475, -1.6, -1);
            this.tileRender.render(tile, mc.getRenderPartialTicks(), matrixStack, buffer, light, OverlayTexture.NO_OVERLAY);
            matrixStack.pop();
        }
    }
}
