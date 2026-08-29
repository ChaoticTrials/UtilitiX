package de.melanx.utilitix.content.brewery;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.transfer.item.ItemUtil;
import org.moddingx.libx.render.ClientTickHandler;
import org.moddingx.libx.render.block.RotatedBlockRenderer;
import org.moddingx.libx.render.block.TransformingBlockRenderer;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class AdvancedBreweryRenderer extends RotatedBlockRenderer<AdvancedBreweryBlockEntity, AdvancedBreweryRenderer.AdvancedBreweryRenderState> {

    private final ItemModelResolver itemModelResolver;

    public AdvancedBreweryRenderer(BlockEntityRendererProvider.Context context) {
        this.itemModelResolver = context.itemModelResolver();
    }

    @Override
    public void extractRenderState(@Nonnull AdvancedBreweryBlockEntity blockEntity, @Nonnull AdvancedBreweryRenderState renderState, float partialTicks, @Nonnull Vec3 cameraPos, @Nullable ModelFeatureRenderer.CrumblingOverlay breakProgress) {
        super.extractRenderState(blockEntity, renderState, partialTicks, cameraPos, breakProgress);

        ItemStack ingredient = ItemUtil.getStack(blockEntity.getInventory(), 0);
        this.itemModelResolver.updateForTopItem(renderState.ingredient, ingredient, ItemDisplayContext.GROUND, blockEntity.getLevel(), null, 0);

        ItemStack potion1 = ItemUtil.getStack(blockEntity.getInventory(), 1);
        this.itemModelResolver.updateForTopItem(renderState.potion1, potion1, ItemDisplayContext.GROUND, blockEntity.getLevel(), null, 0);

        ItemStack potion2 = ItemUtil.getStack(blockEntity.getInventory(), 2);
        this.itemModelResolver.updateForTopItem(renderState.potion2, potion2, ItemDisplayContext.GROUND, blockEntity.getLevel(), null, 0);

        ItemStack main = ItemUtil.getStack(blockEntity.getInventory(), 3);
        this.itemModelResolver.updateForTopItem(renderState.main, main, ItemDisplayContext.GROUND, blockEntity.getLevel(), null, 0);
    }

    @Override
    protected void doRender(@Nonnull AdvancedBreweryRenderState renderState, @Nonnull PoseStack poseStack, @Nonnull SubmitNodeCollector nodeCollector, int light, int overlay) {
        float angle = ClientTickHandler.ticksInGame() + renderState.partialTicks;

        if (!renderState.ingredient.isEmpty()) {
            poseStack.pushPose();
            poseStack.translate(0.5, 0.66, 0.65);
            poseStack.scale(0.45f, 0.45f, 0.45f);
            poseStack.mulPose(Axis.YP.rotationDegrees(angle));
            renderState.ingredient.submit(poseStack, nodeCollector, light, overlay, 0);
            poseStack.popPose();
        }

        if (!renderState.main.isEmpty()) {
            poseStack.pushPose();
            poseStack.translate(0.5, 0.15, 0.18);
            poseStack.scale(0.7f, 0.7f, 0.7f);
            poseStack.mulPose(Axis.YP.rotationDegrees(-angle));
            renderState.main.submit(poseStack, nodeCollector, light, overlay, 0);
            poseStack.popPose();
        }

        if (!renderState.potion1.isEmpty()) {
            poseStack.pushPose();
            poseStack.translate(0.82, 0.42, 0.68);
            poseStack.scale(0.6f, 0.6f, 0.6f);
            poseStack.mulPose(Axis.YP.rotationDegrees(-angle));
            renderState.potion1.submit(poseStack, nodeCollector, light, overlay, 0);
            poseStack.popPose();
        }

        if (!renderState.potion2.isEmpty()) {
            poseStack.pushPose();
            poseStack.translate(0.18, 0.42, 0.68);
            poseStack.scale(0.6f, 0.6f, 0.6f);
            poseStack.mulPose(Axis.YP.rotationDegrees(-angle));
            renderState.potion2.submit(poseStack, nodeCollector, light, overlay, 0);
            poseStack.popPose();
        }
    }

    @Nonnull
    @Override
    public AdvancedBreweryRenderState createRenderState() {
        return new AdvancedBreweryRenderState();
    }

    public static class AdvancedBreweryRenderState extends TransformingBlockRenderer.RenderState {

        public final ItemStackRenderState ingredient = new ItemStackRenderState();
        public final ItemStackRenderState main = new ItemStackRenderState();
        public final ItemStackRenderState potion1 = new ItemStackRenderState();
        public final ItemStackRenderState potion2 = new ItemStackRenderState();
    }
}
