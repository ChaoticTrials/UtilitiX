package de.melanx.utilitix.content.bell;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import de.melanx.utilitix.registration.ModItems;
import de.melanx.utilitix.util.Textures;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.Material;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BellBlockEntity;
import org.moddingx.libx.annotation.model.Model;
import org.moddingx.libx.util.lazy.LazyValue;

import javax.annotation.Nonnull;

public class BellRenderer extends BlockEntityWithoutLevelRenderer {

    public static final Material GRAY_BELL_MATERIAL = new Material(InventoryMenu.BLOCK_ATLAS, Textures.GRAY_BELL_TEXTURE);

    @Model(namespace = "minecraft", value = "item/stick")
    public static BakedModel stickModel = null;
    @Model("item/hand_bell_item")
    public static BakedModel handBellModel = null;
    @Model("item/mob_bell_item")
    public static BakedModel mobBellModel = null;

    private final LazyValue<ItemStack> stick = new LazyValue<>(() -> new ItemStack(Items.STICK));
    @SuppressWarnings("ConstantConditions")
    private final LazyValue<BellBlockEntity> lazyBellBlockEntity = new LazyValue<>(() -> new BellBlockEntity(BlockPos.ZERO, Blocks.BELL.defaultBlockState()));
    private BlockEntityRenderer<BellBlockEntity> blockEntityRenderer = null;

    private final ModelPart grayscaleModel;

    public BellRenderer(BlockEntityRendererProvider.Context context) {
        super(Minecraft.getInstance().getBlockEntityRenderDispatcher(), Minecraft.getInstance().getEntityModels());
        ModelPart part = context.bakeLayer(ModelLayers.BELL);
        this.grayscaleModel = part.getChild("bell_body");
    }

    @Override
    public void renderByItem(@Nonnull ItemStack stack, @Nonnull ItemDisplayContext transform, @Nonnull PoseStack poseStack, @Nonnull MultiBufferSource buffer, int light, int overlay) {
        Minecraft mc = Minecraft.getInstance();
        VertexConsumer vertex = buffer.getBuffer(RenderType.cutout());
        if (transform == ItemDisplayContext.GUI) {
            mc.getItemRenderer().renderModelLists(stack.getItem() == ModItems.mobBell ? mobBellModel : handBellModel, stack, light, OverlayTexture.NO_OVERLAY, poseStack, vertex);
            return;
        }

        poseStack.pushPose();
        poseStack.scale(0.7F, 0.7F, 0.7F);
        poseStack.translate(0, 0, 0.25F);
        mc.getItemRenderer().renderModelLists(stickModel, this.stick.get(), light, OverlayTexture.NO_OVERLAY, poseStack, vertex);
        poseStack.popPose();

        if (mc.level != null) {
            BellBlockEntity blockEntity = this.lazyBellBlockEntity.get();
            blockEntity.setLevel(mc.level);
            blockEntity.clickDirection = Direction.EAST;

            if (mc.player == null || mc.player.getUseItemRemainingTicks() <= 0) {
                blockEntity.ticks = 0;
            } else {
                blockEntity.ticks = Math.round(Mth.lerp((mc.player.getUseItemRemainingTicks() % 10) / 10f, 0, 50));
            }

            blockEntity.shaking = blockEntity.ticks > 0;
            if (this.blockEntityRenderer == null) {
                this.blockEntityRenderer = Minecraft.getInstance().getBlockEntityRenderDispatcher().getRenderer(blockEntity);
            }

            poseStack.pushPose();
            poseStack.scale(0.7F, 0.7F, 0.7F);
            poseStack.translate(0, 0F, 0.25F);
            poseStack.mulPose(Axis.ZP.rotationDegrees(-45));
            poseStack.mulPose(Axis.XP.rotationDegrees(180));
            poseStack.translate(-0.475, -1.6, -1);

            if (stack.getItem() != ModItems.mobBell) {
                this.blockEntityRenderer.render(blockEntity, mc.getFrameTimeNs(), poseStack, buffer, LightTexture.pack(15, 15), OverlayTexture.NO_OVERLAY);
            } else {
                int color = MobBellItem.getColor(stack);
                float ringRotation = -(Mth.sin(blockEntity.ticks + mc.getFrameTimeNs() / (float) Math.PI) / (4 + (blockEntity.ticks + Minecraft.getInstance().getFrameTimeNs()) / 3f));

                this.grayscaleModel.xRot = 0;
                this.grayscaleModel.zRot = blockEntity.shaking ? ringRotation : 0;

                VertexConsumer vertexConsumer = GRAY_BELL_MATERIAL.buffer(buffer, RenderType::entitySolid);
                this.grayscaleModel.render(poseStack, vertexConsumer, light, OverlayTexture.NO_OVERLAY, color);
            }

            poseStack.popPose();
        }
    }
}
