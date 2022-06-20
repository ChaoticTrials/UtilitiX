package de.melanx.utilitix.content.shulkerboat;

import com.google.common.collect.ImmutableMap;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.datafixers.util.Pair;
import com.mojang.math.Vector3f;
import net.minecraft.client.model.BoatModel;
import net.minecraft.client.model.ShulkerModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.entity.BoatRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.vehicle.Boat;

import javax.annotation.Nonnull;
import java.util.Map;
import java.util.stream.Stream;

public class ShulkerBoatRenderer extends BoatRenderer {

    private final Map<Boat.Type, Pair<ResourceLocation, BoatModel>> boatResources;
    private final ShulkerModel<?> shulkerModel;

    public ShulkerBoatRenderer(EntityRendererProvider.Context context) {
        super(context, true);
        this.boatResources = Stream.of(Boat.Type.values()).collect(ImmutableMap.toImmutableMap(type -> type, type -> {
            ResourceLocation location = new ResourceLocation("minecraft", "textures/entity/boat/" + type.getName() + ".png");
            BoatModel model = new BoatModel(context.bakeLayer(ModelLayers.createBoatModelName(type)), false);
            return Pair.of(location, model);
        }));
        this.shulkerModel = new ShulkerModel<>(context.bakeLayer(ModelLayers.SHULKER));
    }

    @Override
    public void render(@Nonnull Boat boat, float entityYaw, float partialTick, @Nonnull PoseStack poseStack, @Nonnull MultiBufferSource buffer, int packedLight) {
        super.render(boat, entityYaw, partialTick, poseStack, buffer, packedLight);
        RenderType renderType = this.shulkerModel.renderType(new ResourceLocation("minecraft", "textures/" + Sheets.DEFAULT_SHULKER_TEXTURE_LOCATION.texture().getPath() + ".png"));
        VertexConsumer vertexConsumer = buffer.getBuffer(renderType);
        poseStack.pushPose();
        poseStack.mulPose(Vector3f.YP.rotationDegrees(180.0F - entityYaw));
        float remainingHurtTime = (float) boat.getHurtTime() - partialTick;
        if (remainingHurtTime > 0) {
            float damage = Math.max(0, boat.getDamage() - partialTick);
            poseStack.mulPose(Vector3f.XP.rotationDegrees(Mth.sin(remainingHurtTime) * remainingHurtTime * damage / 10.0F * (float) boat.getHurtDir()));
        }

        poseStack.translate(0, 1.39, 0.475);
        poseStack.scale(0.8f, 0.8f, 0.8f);
        poseStack.scale(-1.0F, -1.0F, 1.0F);
        poseStack.mulPose(Vector3f.YP.rotationDegrees(90.0F));
        this.shulkerModel.renderToBuffer(poseStack, vertexConsumer, packedLight, OverlayTexture.NO_OVERLAY, 1, 1, 1, 1);
        poseStack.popPose();
    }

    @Nonnull
    @Override
    public Pair<ResourceLocation, BoatModel> getModelWithLocation(@Nonnull Boat boat) {
        return this.boatResources.get(boat.getBoatType());
    }
}
