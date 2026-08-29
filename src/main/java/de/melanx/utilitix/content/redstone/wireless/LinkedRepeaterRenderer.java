package de.melanx.utilitix.content.redstone.wireless;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import de.melanx.utilitix.registration.ModItems;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.Vec3;
import org.moddingx.libx.render.ClientTickHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class LinkedRepeaterRenderer implements BlockEntityRenderer<LinkedRepeaterBlockEntity, LinkedRepeaterRenderer.LinkedRepeaterRenderState> {

    private static ItemStack linkedCrystal;

    private final ItemModelResolver itemModelResolver;

    private static ItemStack linkedCrystal() {
        if (linkedCrystal == null) {
            linkedCrystal = new ItemStack(ModItems.linkedCrystal);
        }

        return linkedCrystal;
    }

    public LinkedRepeaterRenderer(BlockEntityRendererProvider.Context context) {
        this.itemModelResolver = context.itemModelResolver();
    }

    @Nonnull
    @Override
    public LinkedRepeaterRenderState createRenderState() {
        return new LinkedRepeaterRenderState();
    }

    @Override
    public void extractRenderState(@Nonnull LinkedRepeaterBlockEntity blockEntity, @Nonnull LinkedRepeaterRenderState state, float partialTicks, @Nonnull Vec3 cameraPosition, @Nullable ModelFeatureRenderer.CrumblingOverlay breakProgress) {
        BlockEntityRenderer.super.extractRenderState(blockEntity, state, partialTicks, cameraPosition, breakProgress);
        state.eye = blockEntity.getBlockState().getValue(BlockStateProperties.EYE);
        state.partialTicks = partialTicks;
        if (state.eye) {
            this.itemModelResolver.updateForTopItem(state.crystal, LinkedRepeaterRenderer.linkedCrystal(), ItemDisplayContext.GROUND, blockEntity.getLevel(), null, 0);
        }
    }

    @Override
    public void submit(@Nonnull LinkedRepeaterRenderState state, @Nonnull PoseStack poseStack, @Nonnull SubmitNodeCollector submitNodeCollector, @Nonnull CameraRenderState camera) {
        if (!state.eye) {
            return;
        }

        poseStack.pushPose();
        poseStack.translate(0.5, 0.55, 0.5);
        poseStack.scale(0.8f, 0.8f, 0.8f);
        poseStack.mulPose(Axis.YP.rotationDegrees(ClientTickHandler.ticksInGame() + state.partialTicks));
        state.crystal.submit(poseStack, submitNodeCollector, state.lightCoords, 0, 0);
        poseStack.popPose();
    }

    public static class LinkedRepeaterRenderState extends BlockEntityRenderState {

        public boolean eye;
        public float partialTicks;
        public final ItemStackRenderState crystal = new ItemStackRenderState();
    }
}
