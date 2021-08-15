package de.melanx.utilitix.content.bell;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Vector3f;
import de.melanx.utilitix.Textures;
import de.melanx.utilitix.registration.ModItems;
import io.github.noeppi_noeppi.libx.annotation.model.Model;
import io.github.noeppi_noeppi.libx.util.LazyValue;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.Material;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BellBlockEntity;

import javax.annotation.Nonnull;

public class RenderBell extends BlockEntityWithoutLevelRenderer {

    public static final Material GRAY_BELL_MATERIAL = new Material(InventoryMenu.BLOCK_ATLAS, Textures.GRAY_BELL_TEXTURE);

    @Model(namespace = "minecraft", value = "item/stick")
    public static BakedModel stickModel = null;
    @Model("item/hand_bell_item")
    public static BakedModel handBellModel = null;
    @Model("item/mob_bell_item")
    public static BakedModel mobBellModel = null;

    private final LazyValue<ItemStack> stick = new LazyValue<>(() -> new ItemStack(Items.STICK));
    @SuppressWarnings("ConstantConditions")
    private final LazyValue<BellBlockEntity> tile = new LazyValue<>(() -> new BellBlockEntity(BlockPos.ZERO, null));
    private BlockEntityRenderer<BellBlockEntity> tileRender = null;

    // TODO
//    private final ModelPart grayscaleModel = new ModelPart(32, 32, 0, 0);

    public RenderBell() {
        super(Minecraft.getInstance().getBlockEntityRenderDispatcher(), Minecraft.getInstance().getEntityModels());
//        this.grayscaleModel.addBox(-3.0F, -6.0F, -3.0F, 6.0F, 7.0F, 6.0F);
//        this.grayscaleModel.setPos(8.0F, 12.0F, 8.0F);
//        ModelPart modelPart = new ModelPart(32, 32, 0, 13);
//        modelPart.addBox(4.0F, 4.0F, 4.0F, 8.0F, 2.0F, 8.0F);
//        modelPart.setPos(-8.0F, -12.0F, -8.0F);
//        this.grayscaleModel.addChild(modelPart);
    }

    @Override
    public void renderByItem(@Nonnull ItemStack stack, @Nonnull ItemTransforms.TransformType transform, @Nonnull PoseStack poseStack, @Nonnull MultiBufferSource buffer, int light, int overlay) {
        Minecraft mc = Minecraft.getInstance();
        VertexConsumer vertex = buffer.getBuffer(RenderType.cutout());
        if (transform == ItemTransforms.TransformType.GUI) {
            mc.getItemRenderer().renderModelLists(stack.getItem() == ModItems.mobBell ? mobBellModel : handBellModel, stack, light, OverlayTexture.NO_OVERLAY, poseStack, vertex);
        } else {
            poseStack.pushPose();
            poseStack.scale(0.7F, 0.7F, 0.7F);
            poseStack.translate(0, 0, 0.25F);
            mc.getItemRenderer().renderModelLists(stickModel, this.stick.get(), light, OverlayTexture.NO_OVERLAY, poseStack, vertex);
            poseStack.popPose();
            if (mc.level != null) {
                BellBlockEntity tile = this.tile.get();
                tile.setLevel(mc.level);
                tile.blockState = Blocks.BELL.defaultBlockState();
                tile.clickDirection = Direction.EAST;
                if (mc.player == null || mc.player.getUseItemRemainingTicks() <= 0) {
                    tile.ticks = 0;
                } else {
                    tile.ticks = Math.round(Mth.lerp((mc.player.getUseItemRemainingTicks() % 10) / 10f, 0, 50));
                }
                tile.shaking = tile.ticks > 0;
                if (this.tileRender == null) {
//                    this.tileRender = BlockEntityRenderDispatcher.instance.getRenderer(tile);
                }
                poseStack.pushPose();
                poseStack.scale(0.7F, 0.7F, 0.7F);
                poseStack.translate(0, 0F, 0.25F);
                poseStack.mulPose(Vector3f.ZP.rotationDegrees(-45));
                poseStack.mulPose(Vector3f.XP.rotationDegrees(180));
                poseStack.translate(-0.475, -1.6, -1);
                if (stack.getItem() == ModItems.mobBell) {
                    float[] color = ItemMobBell.getFloatColor(stack);
                    float ringRotation = -(Mth.sin(tile.ticks + mc.getFrameTime() / (float) Math.PI) / (4 + (tile.ticks + Minecraft.getInstance().getFrameTime()) / 3f));
//                    this.grayscaleModel.xRot = 0;
//                    this.grayscaleModel.zRot = tile.shaking ? ringRotation : 0;
                    VertexConsumer ivertexconsumer = GRAY_BELL_MATERIAL.buffer(buffer, RenderType::entitySolid);
//                    this.grayscaleModel.render(poseStack, ivertexconsumer, light, OverlayTexture.NO_OVERLAY,
//                            color[0], color[1], color[2], 1);
                } else {
                    this.tileRender.render(tile, mc.getFrameTime(), poseStack, buffer,
                            LightTexture.pack(15, 15), OverlayTexture.NO_OVERLAY);
                }
                poseStack.popPose();
            }
        }
    }
}
