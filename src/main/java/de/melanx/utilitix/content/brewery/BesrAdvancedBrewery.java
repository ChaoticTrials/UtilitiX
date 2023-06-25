package de.melanx.utilitix.content.brewery;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.moddingx.libx.render.ClientTickHandler;
import org.moddingx.libx.render.block.RotatedBlockRenderer;

import javax.annotation.Nonnull;

public class BesrAdvancedBrewery extends RotatedBlockRenderer<TileAdvancedBrewery> {

    @Override
    protected void doRender(@Nonnull TileAdvancedBrewery tile, float partialTick, @Nonnull PoseStack poseStack, @Nonnull MultiBufferSource buffer, int light, int overlay) {
        ItemStack ingredient = tile.getInventory().getStackInSlot(0);
        ClientLevel level = Minecraft.getInstance().level;
        if (!ingredient.isEmpty()) {
            poseStack.pushPose();
            poseStack.translate(0.5, 0.66, 0.65);
            poseStack.scale(0.45f, 0.45f, 0.45f);
            poseStack.mulPose(Axis.YP.rotationDegrees(ClientTickHandler.ticksInGame() + partialTick));
            Minecraft.getInstance().getItemRenderer().renderStatic(ingredient, ItemDisplayContext.GROUND, light, overlay, poseStack, buffer, level, 0);
            poseStack.popPose();
        }
        ItemStack main = tile.getInventory().getStackInSlot(3);
        if (!main.isEmpty()) {
            poseStack.pushPose();
            poseStack.translate(0.5, 0.15, 0.18);
            poseStack.scale(0.7f, 0.7f, 0.7f);
            poseStack.mulPose(Axis.YP.rotationDegrees(-(ClientTickHandler.ticksInGame() + partialTick)));
            Minecraft.getInstance().getItemRenderer().renderStatic(main, ItemDisplayContext.GROUND, light, overlay, poseStack, buffer, level, 0);
            poseStack.popPose();
        }
        ItemStack p1 = tile.getInventory().getStackInSlot(1);
        if (!p1.isEmpty()) {
            poseStack.pushPose();
            poseStack.translate(0.82, 0.42, 0.68);
            poseStack.scale(0.6f, 0.6f, 0.6f);
            poseStack.mulPose(Axis.YP.rotationDegrees(-(ClientTickHandler.ticksInGame() + partialTick)));
            Minecraft.getInstance().getItemRenderer().renderStatic(p1, ItemDisplayContext.GROUND, light, overlay, poseStack, buffer, level, 0);
            poseStack.popPose();
        }
        ItemStack p2 = tile.getInventory().getStackInSlot(2);
        if (!p2.isEmpty()) {
            poseStack.pushPose();
            poseStack.translate(0.18, 0.42, 0.68);
            poseStack.scale(0.6f, 0.6f, 0.6f);
            poseStack.mulPose(Axis.YP.rotationDegrees(-(ClientTickHandler.ticksInGame() + partialTick)));
            Minecraft.getInstance().getItemRenderer().renderStatic(p2, ItemDisplayContext.GROUND, light, overlay, poseStack, buffer, level, 0);
            poseStack.popPose();
        }
    }
}
