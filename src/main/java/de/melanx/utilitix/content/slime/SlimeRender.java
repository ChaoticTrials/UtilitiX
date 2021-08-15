package de.melanx.utilitix.content.slime;

import com.mojang.blaze3d.vertex.PoseStack;
import de.melanx.utilitix.Textures;
import io.github.noeppi_noeppi.libx.render.RenderHelperBlock;
import io.github.noeppi_noeppi.libx.render.RenderHelperLevel;
import net.minecraft.client.Minecraft;
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
import net.minecraftforge.client.event.RenderWorldLastEvent;

public class SlimeRender {
    
    public static void renderWorld(RenderWorldLastEvent event) {
        if (Minecraft.getInstance().level != null) {
            Minecraft.getInstance().getProfiler().push("utilitix_glue");
            Minecraft.getInstance().getTextureManager().getTexture(InventoryMenu.BLOCK_ATLAS); // TODO check
            TextureAtlasSprite slime = Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(Textures.GLUE_OVERLAY_TEXTURE);
            if (slime != null) {
                int size = Minecraft.getInstance().level.getChunkSource().storage.chunks.length();
                Frustum clip = new Frustum(event.getMatrixStack().last().pose(), event.getProjectionMatrix());
                Vec3 projection = Minecraft.getInstance().gameRenderer.getMainCamera().getPosition();
                clip.prepare(projection.x, projection.y, projection.z);
                Minecraft.getInstance().getProfiler().push("render_chunks");
                for (int i = 0; i < size; i++) {
                    LevelChunk chunk = Minecraft.getInstance().level.getChunkSource().storage.chunks.get(i);
                    if (chunk != null) {
                        ChunkPos pos = chunk.getPos();
                        if (clip.isVisible(new AABB(pos.getMinBlockX(), 0, pos.getMinBlockZ(), pos.getMaxBlockX() + 1, 256, pos.getMaxBlockZ() + 1))) {
                            //noinspection ConstantConditions
                            StickyChunk data = chunk.getCapability(SlimyCapability.STICKY_CHUNK).orElse(null);
                            //noinspection ConstantConditions
                            if (data != null) {
                                event.getMatrixStack().pushPose();
                                RenderHelperLevel.loadProjection(event.getMatrixStack(), pos.getMinBlockX(), 0, pos.getMinBlockZ());
                                renderChunk(event.getMatrixStack(), Minecraft.getInstance().renderBuffers().bufferSource(), pos, chunk, data, slime);
                                event.getMatrixStack().popPose();
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

    private static void renderChunk(PoseStack poseStack, MultiBufferSource buffer, ChunkPos pos, LevelChunk chunk, StickyChunk data, TextureAtlasSprite slime) {
        Minecraft.getInstance().getProfiler().push("render_chunk_glue");
        data.foreach((x, y, z, flags) -> {
            Minecraft.getInstance().getProfiler().push("do_render");
            BlockPos block = new BlockPos(pos.getMinBlockX() + x, y, pos.getMinBlockZ() + z);
            BlockState state = chunk.getBlockState(block);
            int lightValue = state.getLightBlock(chunk, block);
            int light = LightTexture.pack(lightValue, lightValue);
            poseStack.pushPose();
            poseStack.translate(x, y, z);
            RenderHelperBlock.renderBlockOverlaySprite(state, poseStack, buffer, light, OverlayTexture.NO_OVERLAY, slime, state.getSeed(block), dir -> (flags & (1 << dir.ordinal())) != 0);
            Minecraft.getInstance().renderBuffers().crumblingBufferSource().endBatch();
            poseStack.popPose();
            Minecraft.getInstance().getProfiler().pop(); // do_render
        });
        Minecraft.getInstance().getProfiler().pop(); // render_chunk_glue
    }
}
