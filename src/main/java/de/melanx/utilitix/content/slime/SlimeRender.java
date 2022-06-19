package de.melanx.utilitix.content.slime;

import com.mojang.blaze3d.vertex.PoseStack;
import de.melanx.utilitix.Textures;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.event.RenderLevelLastEvent;
import org.moddingx.libx.render.RenderHelperBlock;
import org.moddingx.libx.render.RenderHelperLevel;

public class SlimeRender {

    public static void renderWorld(RenderLevelLastEvent event) {
        ClientLevel level = Minecraft.getInstance().level;
        if (level != null) {
            Minecraft.getInstance().getProfiler().push("utilitix_glue");
            Minecraft.getInstance().getTextureManager().getTexture(InventoryMenu.BLOCK_ATLAS);
            TextureAtlasSprite slime = Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(Textures.GLUE_OVERLAY_TEXTURE);
            if (slime != null) {
                PoseStack poseStack = event.getPoseStack();
                int size = level.getChunkSource().storage.chunks.length();
                Frustum clip = new Frustum(poseStack.last().pose(), event.getProjectionMatrix());
                Vec3 projection = Minecraft.getInstance().gameRenderer.getMainCamera().getPosition();
                clip.prepare(projection.x, projection.y, projection.z);
                Minecraft.getInstance().getProfiler().push("render_chunks");
                for (int i = 0; i < size; i++) {
                    LevelChunk chunk = level.getChunkSource().storage.chunks.get(i);
                    if (chunk != null) {
                        ChunkPos pos = chunk.getPos();
                        if (clip.isVisible(new AABB(pos.getMinBlockX(), level.getMinBuildHeight(), pos.getMinBlockZ(), pos.getMaxBlockX() + 1, level.getMaxBuildHeight() + 1, pos.getMaxBlockZ() + 1))) {
                            //noinspection ConstantConditions
                            StickyChunk data = chunk.getCapability(SlimyCapability.STICKY_CHUNK).orElse(null);
                            //noinspection ConstantConditions
                            if (data != null) {
                                data.foreach(renderChunk(clip, poseStack, Minecraft.getInstance().renderBuffers().bufferSource(), pos, chunk, slime));
                            }
                        }
                    }
                }
                Minecraft.getInstance().renderBuffers().bufferSource().endBatch();
                Minecraft.getInstance().getProfiler().pop(); // render_chunks
            }
            Minecraft.getInstance().getProfiler().pop(); // utilitix_glue
        }
    }
    
    private static StickyChunk.ChunkAction renderChunk(Frustum clip, PoseStack poseStack, MultiBufferSource buffer, ChunkPos pos, LevelChunk chunk, TextureAtlasSprite slime) {
        return (sectionId, sectionOffset) -> {
            if (clip.isVisible(new AABB(pos.getMinBlockX(), sectionOffset, pos.getMinBlockZ(), pos.getMaxBlockX() + 1, sectionOffset + 16, pos.getMaxBlockZ() + 1))) {
                return renderSection(poseStack, buffer, pos, sectionOffset, chunk, slime);
            } else {
                return null;
            }
        };
    }
    
    private static StickyChunk.SectionAction renderSection(PoseStack poseStack, MultiBufferSource buffer, ChunkPos pos, int sectionOffset, LevelChunk chunk, TextureAtlasSprite slime) {
        return new StickyChunk.SectionAction() {
            
            @Override
            public void start() {
                poseStack.pushPose();
                RenderHelperLevel.loadProjection(poseStack, pos.getMinBlockX(), sectionOffset, pos.getMinBlockZ());
                Minecraft.getInstance().getProfiler().push("render_chunk_glue");
            }

            @Override
            public void accept(int x, int y, int z, byte data) {
                Minecraft.getInstance().getProfiler().push("do_render");
                BlockPos block = new BlockPos(pos.getMinBlockX() + x, sectionOffset + y, pos.getMinBlockZ() + z);
                BlockState state = chunk.getBlockState(block);
                int lightValue = state.getLightBlock(chunk, block);
                int light = LightTexture.pack(lightValue, lightValue);
                poseStack.pushPose();
                poseStack.translate(x, y, z);
                RenderHelperBlock.renderBlockOverlaySprite(state, poseStack, buffer, light, OverlayTexture.NO_OVERLAY, slime, state.getSeed(block), dir -> (data & (1 << dir.ordinal())) != 0);
                Minecraft.getInstance().renderBuffers().crumblingBufferSource().endBatch();
                poseStack.popPose();
                Minecraft.getInstance().getProfiler().pop(); // do_render
            }

            @Override
            public void stop() {
                Minecraft.getInstance().getProfiler().pop(); // render_chunk_glue
                poseStack.popPose();
            }
        };
    }
}
