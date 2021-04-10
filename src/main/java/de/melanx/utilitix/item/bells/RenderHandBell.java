package de.melanx.utilitix.item.bells;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import de.melanx.utilitix.Textures;
import de.melanx.utilitix.UtilitiX;
import de.melanx.utilitix.registration.ModItems;
import io.github.noeppi_noeppi.libx.annotation.Model;
import net.minecraft.block.Blocks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.client.renderer.model.RenderMaterial;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.tileentity.ItemStackTileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.entity.EntityType;
import net.minecraft.inventory.container.PlayerContainer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.tileentity.BellTileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.LazyValue;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nonnull;
import java.util.Objects;

public class RenderHandBell extends ItemStackTileEntityRenderer {
    
    public static final RenderMaterial GRAY_BELL_MATERIAL = new RenderMaterial(PlayerContainer.LOCATION_BLOCKS_TEXTURE, Textures.GRAY_BELL_TEXTURE);

    @Model(namespace = "minecraft", value = "item/stick")
    public static IBakedModel stickModel = null;
    @Model("item/hand_bell_item")
    public static IBakedModel handBellModel = null;
    @Model("item/mob_bell_item")
    public static IBakedModel mobBellModel = null;

    private final LazyValue<ItemStack> stick = new LazyValue<>(() -> new ItemStack(Items.STICK));
    private final LazyValue<BellTileEntity> tile = new LazyValue<>(BellTileEntity::new);
    private TileEntityRenderer<BellTileEntity> tileRender = null;

    private final ModelRenderer grayscaleModel = new ModelRenderer(32, 32, 0, 0);

    public RenderHandBell() {
        this.grayscaleModel.addBox(-3.0F, -6.0F, -3.0F, 6.0F, 7.0F, 6.0F);
        this.grayscaleModel.setRotationPoint(8.0F, 12.0F, 8.0F);
        ModelRenderer modelrenderer = new ModelRenderer(32, 32, 0, 13);
        modelrenderer.addBox(4.0F, 4.0F, 4.0F, 8.0F, 2.0F, 8.0F);
        modelrenderer.setRotationPoint(-8.0F, -12.0F, -8.0F);
        this.grayscaleModel.addChild(modelrenderer);
    }

    @Override
    public void func_239207_a_(@Nonnull ItemStack stack, @Nonnull ItemCameraTransforms.TransformType transform, @Nonnull MatrixStack matrixStack, @Nonnull IRenderTypeBuffer buffer, int light, int overlay) {
        Minecraft mc = Minecraft.getInstance();
        IVertexBuilder vertex = buffer.getBuffer(RenderType.getCutout());
        if (transform == ItemCameraTransforms.TransformType.GUI) {
            mc.getItemRenderer().renderModel(stack.getItem() == ModItems.mobBell ? mobBellModel : handBellModel, stack, light, OverlayTexture.NO_OVERLAY, matrixStack, vertex);
        } else {
            matrixStack.push();
            matrixStack.scale(0.7F, 0.7F, 0.7F);
            matrixStack.translate(0, 0, 0.25F);
            mc.getItemRenderer().renderModel(stickModel, this.stick.getValue(), light, OverlayTexture.NO_OVERLAY, matrixStack, vertex);
            matrixStack.pop();
            if (mc.world != null) {
                BellTileEntity tile = this.tile.getValue();
                tile.setWorldAndPos(mc.world, BlockPos.ZERO);
                tile.cachedBlockState = Blocks.BELL.getDefaultState();
                tile.ringDirection = Direction.EAST;
                if (mc.player == null || mc.player.getItemInUseCount() <= 0) {
                    tile.ringingTicks = 0;
                } else {
                    tile.ringingTicks = Math.round(MathHelper.lerp((mc.player.getItemInUseCount() % 10) / 10f, 0, 50));
                }
                tile.isRinging = tile.ringingTicks > 0;
                if (this.tileRender == null) {
                    this.tileRender = TileEntityRendererDispatcher.instance.getRenderer(tile);
                }
                matrixStack.push();
                matrixStack.scale(0.7F, 0.7F, 0.7F);
                matrixStack.translate(0, 0F, 0.25F);
                matrixStack.rotate(Vector3f.ZP.rotationDegrees(-45));
                matrixStack.rotate(Vector3f.XP.rotationDegrees(180));
                matrixStack.translate(-0.475, -1.6, -1);
                if (stack.getItem() == ModItems.mobBell) {
                    float[] color = MobBell.getFloatColor(stack);
                    float ringRotation = -(MathHelper.sin(tile.ringingTicks + mc.getRenderPartialTicks() / (float) Math.PI) / (4 + (tile.ringingTicks + Minecraft.getInstance().getRenderPartialTicks()) / 3f));
                    this.grayscaleModel.rotateAngleX = 0;
                    this.grayscaleModel.rotateAngleZ = tile.isRinging ? ringRotation : 0;
                    IVertexBuilder ivertexbuilder = GRAY_BELL_MATERIAL.getBuffer(buffer, RenderType::getEntitySolid);
                    this.grayscaleModel.render(matrixStack, ivertexbuilder, light, OverlayTexture.NO_OVERLAY,
                            color[0], color[1], color[2], 1);
                } else {
                    this.tileRender.render(tile, mc.getRenderPartialTicks(), matrixStack, buffer,
                            LightTexture.packLight(15, 15), OverlayTexture.NO_OVERLAY);
                }
                matrixStack.pop();
            }
        }
    }
}
