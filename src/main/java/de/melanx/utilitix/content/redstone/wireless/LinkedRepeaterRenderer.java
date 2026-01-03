package de.melanx.utilitix.content.redstone.wireless;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import de.melanx.utilitix.registration.ModItems;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import org.moddingx.libx.render.ClientTickHandler;

import javax.annotation.Nonnull;

public class LinkedRepeaterRenderer implements BlockEntityRenderer<LinkedRepeaterBlockEntity> {

    private static final ItemStack LINKED_CRYSTAL = new ItemStack(ModItems.linkedCrystal);

    @Override
    public void render(@Nonnull LinkedRepeaterBlockEntity blockEntity, float partialTick, @Nonnull PoseStack poseStack, @Nonnull MultiBufferSource buffer, int combinedLight, int combinedOverlay) {
        if (!blockEntity.getBlockState().getValue(BlockStateProperties.EYE)) {
            return;
        }

        poseStack.pushPose();
        poseStack.translate(0.5, 0.55, 0.5);
        poseStack.scale(0.8f, 0.8f, 0.8f);
        poseStack.mulPose(Axis.YP.rotationDegrees(ClientTickHandler.ticksInGame() + partialTick));
        Minecraft.getInstance().getItemRenderer().renderStatic(LINKED_CRYSTAL, ItemDisplayContext.GROUND, combinedLight, OverlayTexture.NO_OVERLAY, poseStack, buffer, Minecraft.getInstance().level, 0);
        poseStack.popPose();
    }
}
