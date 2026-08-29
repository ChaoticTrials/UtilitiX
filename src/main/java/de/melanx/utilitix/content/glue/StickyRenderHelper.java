package de.melanx.utilitix.content.glue;

import com.mojang.blaze3d.vertex.PoseStack;
import de.melanx.utilitix.config.ClientConfig;
import de.melanx.utilitix.config.FeatureConfig;
import de.melanx.utilitix.registration.ModAttachmentTypes;
import de.melanx.utilitix.util.Textures;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.sprite.SpriteId;
import net.minecraft.core.BlockPos;
import net.minecraft.util.LightCoordsUtil;
import net.minecraft.util.profiling.Profiler;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import org.moddingx.libx.render.RenderHelperBlock;
import org.moddingx.libx.render.RenderHelperLevel;

public class StickyRenderHelper {

    public static void renderWorld(RenderLevelStageEvent.AfterOpaqueBlocks event) {
        if (!ClientConfig.renderGlueOnBlocks || !FeatureConfig.Misc.InWorldChanges.glue) {
            return;
        }

        ClientLevel level = Minecraft.getInstance().level;
        if (level == null) {
            return;
        }

        Profiler.get().push("utilitix_glue");
        TextureAtlasSprite slime = Minecraft.getInstance().getAtlasManager().get(new SpriteId(TextureAtlas.LOCATION_BLOCKS, Textures.GLUE_OVERLAY_TEXTURE));

        if (slime == null) {
            return;
        }

        PoseStack poseStack = event.getPoseStack();
        Camera camera = Minecraft.getInstance().gameRenderer.getMainCamera();
        Frustum clip = event.getLevelRenderState().cameraRenderState.cullFrustum;
        int size = level.getChunkSource().storage.chunks.length();

        Profiler.get().push("render_chunks");
        for (int i = 0; i < size; i++) {
            LevelChunk chunk = level.getChunkSource().storage.chunks.get(i);
            if (chunk == null) {
                continue;
            }

            ChunkPos pos = chunk.getPos();
            if (!clip.isVisible(new AABB(pos.getMinBlockX(), level.getMinY(), pos.getMinBlockZ(), pos.getMaxBlockX() + 1, level.getMaxY() + 2, pos.getMaxBlockZ() + 1))) {
                continue;
            }

            StickyChunk data = chunk.getExistingDataOrNull(ModAttachmentTypes.stickyChunk);
            if (data == null) {
                continue;
            }

            data.foreach(renderChunk(camera, clip, poseStack, pos, chunk, slime));
        }

        Profiler.get().pop(); // render_chunks
        Profiler.get().pop(); // utilitix_glue
    }

    private static StickyChunk.ChunkAction renderChunk(Camera camera, Frustum clip, PoseStack poseStack, ChunkPos pos, LevelChunk chunk, TextureAtlasSprite slime) {
        return (sectionId, sectionOffset) -> {
            if (clip.isVisible(new AABB(pos.getMinBlockX(), sectionOffset, pos.getMinBlockZ(), pos.getMaxBlockX() + 1, sectionOffset + 16, pos.getMaxBlockZ() + 1))) {
                return renderSection(camera, poseStack, pos, sectionOffset, chunk, slime);
            }

            return null;
        };
    }

    private static StickyChunk.SectionAction renderSection(Camera camera, PoseStack poseStack, ChunkPos pos, int sectionOffset, LevelChunk chunk, TextureAtlasSprite slime) {
        return new StickyChunk.SectionAction() {

            @Override
            public void start() {
                poseStack.pushPose();
                RenderHelperLevel.loadCameraPosition(camera, poseStack, pos.getMinBlockX(), sectionOffset, pos.getMinBlockZ());
                Profiler.get().push("render_chunk_glue");
            }

            @Override
            public void accept(int x, int y, int z, byte data) {
                Profiler.get().push("do_render");

                BlockPos block = new BlockPos(pos.getMinBlockX() + x, sectionOffset + y, pos.getMinBlockZ() + z);
                BlockState state = chunk.getBlockState(block);
                int lightValue = state.getLightDampening();
                int light = LightCoordsUtil.pack(lightValue, lightValue);

                poseStack.pushPose();
                poseStack.translate(x, y, z);
                RenderHelperBlock.renderBlockOverlaySprite(state, poseStack, light, OverlayTexture.NO_OVERLAY, slime, state.getSeed(block), dir -> {
                    if ((data & (1 << dir.ordinal())) == 0) {
                        return false;
                    }

                    BlockPos neighborPos = block.relative(dir);
                    BlockState neighbor = chunk.getBlockState(neighborPos);
                    if (state.skipRendering(neighbor, dir)) {
                        return false;
                    }

                    return Block.shouldRenderFace(chunk, block, state, neighbor, dir);
                });
                poseStack.popPose();

                Profiler.get().pop(); // do_render
            }

            @Override
            public void stop() {
                Profiler.get().pop(); // render_chunk_glue
                poseStack.popPose();
            }
        };
    }
}
