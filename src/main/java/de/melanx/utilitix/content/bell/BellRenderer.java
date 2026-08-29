package de.melanx.utilitix.content.bell;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.mojang.serialization.MapCodec;
import de.melanx.utilitix.registration.ModItems;
import de.melanx.utilitix.util.Textures;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.object.bell.BellModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.resources.model.geometry.QuadCollection;
import net.minecraft.client.resources.model.sprite.SpriteGetter;
import net.minecraft.client.resources.model.sprite.SpriteId;
import net.minecraft.core.Direction;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.moddingx.libx.annotation.model.Model;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.Consumer;

public class BellRenderer implements SpecialModelRenderer<BellRenderer.RenderData> {

    private static final SpriteId HAND_BELL_TEXTURE = net.minecraft.client.renderer.blockentity.BellRenderer.BELL_TEXTURE;
    private static final SpriteId MOB_BELL_TEXTURE = new SpriteId(TextureAtlas.LOCATION_BLOCKS, Textures.GRAY_BELL_TEXTURE);
    private static final int[] NO_TINTS = new int[0];

    @Model(namespace = "minecraft", value = "item/stick")
    public static QuadCollection stickModel = null;

    private final BellModel model;
    private final SpriteGetter sprites;

    public BellRenderer(SpriteGetter sprites, BellModel model) {
        this.sprites = sprites;
        this.model = model;
    }

    @Override
    public void submit(@Nullable RenderData argument, @Nonnull PoseStack poseStack, @Nonnull SubmitNodeCollector submitNodeCollector, int lightCoords, int overlayCoords, boolean hasFoil, int outlineColor) {
        if (argument == null) {
            return;
        }

        BellModel.State state = new BellModel.State(argument.ticks(), argument.shakeDirection());
        this.model.setupAnim(state);
        SpriteId texture = argument.mobBell() ? MOB_BELL_TEXTURE : HAND_BELL_TEXTURE;
        int tint = argument.mobBell() ? argument.color() : -1;

        poseStack.pushPose();
        poseStack.scale(0.7F, 0.7F, 0.7F);
        poseStack.translate(0, 0, 0.25F);

        if (stickModel != null) {
            submitNodeCollector.submitItem(poseStack, ItemDisplayContext.NONE, lightCoords, overlayCoords, outlineColor,
                    NO_TINTS, stickModel.getAll(), ItemStackRenderState.FoilType.NONE);
        }

        poseStack.mulPose(Axis.ZP.rotationDegrees(-45));
        poseStack.mulPose(Axis.XP.rotationDegrees(180));
        poseStack.translate(-0.475, -1.6, -1);
        submitNodeCollector.submitModel(this.model, state, poseStack, lightCoords, overlayCoords, tint, texture, this.sprites, outlineColor, null);
        poseStack.popPose();
    }

    @Override
    public void getExtents(@Nonnull Consumer<org.joml.Vector3fc> output) {
        PoseStack poseStack = new PoseStack();
        poseStack.scale(0.7F, 0.7F, 0.7F);
        this.model.setupAnim(new BellModel.State(0.0F, null));
        this.model.root().getExtentsForGui(poseStack, output);
    }

    @Nullable
    @Override
    public RenderData extractArgument(ItemStack stack) {
        boolean mobBell = stack.getItem() == ModItems.mobBell;
        int color = mobBell ? ARGB.opaque(MobBellItem.getColor(stack)) : -1;

        Minecraft mc = Minecraft.getInstance();
        float ticks = 0.0F;
        Direction shakeDirection = null;
        if (mc.player != null && mc.player.getUseItem() == stack && mc.player.getUseItemRemainingTicks() > 0) {
            ticks = Math.round(Mth.lerp((mc.player.getUseItemRemainingTicks() % 10) / 10F, 0, 50));
            shakeDirection = Direction.EAST;
        }

        return new RenderData(mobBell, color, ticks, shakeDirection);
    }

    public record RenderData(boolean mobBell, int color, float ticks, @Nullable Direction shakeDirection) {
    }

    public record Unbaked() implements SpecialModelRenderer.Unbaked<RenderData> {

        public static final MapCodec<Unbaked> MAP_CODEC = MapCodec.unit(new Unbaked());

        @Nonnull
        @Override
        public MapCodec<Unbaked> type() {
            return MAP_CODEC;
        }

        @Override
        public BellRenderer bake(SpecialModelRenderer.BakingContext context) {
            return new BellRenderer(context.sprites(), new BellModel(context.entityModelSet().bakeLayer(ModelLayers.BELL)));
        }
    }
}
