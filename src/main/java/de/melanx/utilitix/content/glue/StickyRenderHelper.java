package de.melanx.utilitix.content.glue;

import com.mojang.blaze3d.vertex.PoseStack;
import de.melanx.utilitix.config.ClientConfig;
import de.melanx.utilitix.config.FeatureConfig;
import de.melanx.utilitix.util.Textures;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import net.neoforged.neoforge.event.level.ChunkEvent;
import net.neoforged.neoforge.event.level.LevelEvent;
import org.moddingx.libx.render.RenderHelperBlock;
import org.moddingx.libx.render.RenderHelperLevel;

import javax.annotation.Nullable;
import java.util.IdentityHashMap;
import java.util.Map;

public class StickyRenderHelper {

    private static final Map<LevelChunk, StickyChunk> CHUNKS_WITH_GLUE = new IdentityHashMap<>();
    @Nullable private static ClientLevel INDEXED_LEVEL;

    public static void unloadChunk(ChunkEvent.Unload event) {
        if (event.getLevel() == StickyRenderHelper.INDEXED_LEVEL && event.getChunk() instanceof LevelChunk chunk) {
            StickyRenderHelper.CHUNKS_WITH_GLUE.remove(chunk);
        }
    }

    public static void unloadLevel(LevelEvent.Unload event) {
        if (event.getLevel() == StickyRenderHelper.INDEXED_LEVEL) {
            StickyRenderHelper.selectLevel(null);
        }
    }

    public static void renderWorld(RenderLevelStageEvent event) {
        if (!ClientConfig.renderGlueOnBlocks || !FeatureConfig.Misc.InWorldChanges.glue) {
            return;
        }

        ClientLevel level = Minecraft.getInstance().level;
        if (level == null || event.getStage() != RenderLevelStageEvent.Stage.AFTER_SOLID_BLOCKS) {
            return;
        }

        StickyRenderHelper.selectLevel(level);
        if (StickyRenderHelper.CHUNKS_WITH_GLUE.isEmpty()) {
            return;
        }

        Minecraft.getInstance().getProfiler().push("utilitix_glue");
        Minecraft.getInstance().getTextureManager().getTexture(InventoryMenu.BLOCK_ATLAS);
        TextureAtlasSprite slime = Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(Textures.GLUE_OVERLAY_TEXTURE);

        if (slime == null) {
            Minecraft.getInstance().getProfiler().pop(); // utilitix_glue
            return;
        }

        PoseStack poseStack = event.getPoseStack();
        Frustum clip = event.getFrustum();
        Vec3 projection = event.getCamera().getPosition();
        clip.prepare(projection.x, projection.y, projection.z);

        Minecraft.getInstance().getProfiler().push("render_chunks");

        for (Map.Entry<LevelChunk, StickyChunk> entry : StickyRenderHelper.CHUNKS_WITH_GLUE.entrySet()) {
            LevelChunk levelChunk = entry.getKey();
            if (StickyRenderHelper.isStale(level, levelChunk)) {
                continue;
            }

            ChunkPos pos = levelChunk.getPos();
            if (clip.isVisible(new AABB(pos.getMinBlockX(), level.getMinBuildHeight(), pos.getMinBlockZ(), pos.getMaxBlockX() + 1, level.getMaxBuildHeight() + 1, pos.getMaxBlockZ() + 1))) {
                entry.getValue().foreach(StickyRenderHelper.renderChunk(event.getCamera(), clip, poseStack, pos, levelChunk, slime));
            }
        }

        Minecraft.getInstance().getProfiler().pop(); // render_chunks
        Minecraft.getInstance().getProfiler().pop(); // utilitix_glue
    }

    public static void clientTick(ClientTickEvent.Post event) {
        ClientLevel level = Minecraft.getInstance().level;
        StickyRenderHelper.selectLevel(level);

        if (level != null && !StickyRenderHelper.CHUNKS_WITH_GLUE.isEmpty()) {
            StickyRenderHelper.CHUNKS_WITH_GLUE.keySet().removeIf(chunk -> StickyRenderHelper.isStale(level, chunk));
        }
    }

    private static boolean isStale(ClientLevel level, LevelChunk chunk) {
        ChunkPos pos = chunk.getPos();

        return level.getChunkSource().getChunk(pos.x, pos.z, ChunkStatus.FULL, false) != chunk;
    }

    private static void selectLevel(ClientLevel level) {
        if (StickyRenderHelper.INDEXED_LEVEL != level) {
            StickyRenderHelper.CHUNKS_WITH_GLUE.clear();
            StickyRenderHelper.INDEXED_LEVEL = level;
        }
    }

    protected static void updateChunk(LevelChunk chunk, StickyChunk glue, boolean hasGlue) {
        if (chunk.getLevel() instanceof ClientLevel level && level == Minecraft.getInstance().level) {
            StickyRenderHelper.selectLevel(level);
            if (hasGlue) {
                StickyRenderHelper.CHUNKS_WITH_GLUE.put(chunk, glue);
            } else {
                StickyRenderHelper.CHUNKS_WITH_GLUE.remove(chunk);
            }
        }
    }

    private static StickyChunk.ChunkAction renderChunk(Camera camera, Frustum clip, PoseStack poseStack, ChunkPos pos, LevelChunk chunk, TextureAtlasSprite slime) {
        return (sectionId, sectionOffset) -> {
            if (clip.isVisible(new AABB(pos.getMinBlockX(), sectionOffset, pos.getMinBlockZ(), pos.getMaxBlockX() + 1, sectionOffset + 16, pos.getMaxBlockZ() + 1))) {
                return StickyRenderHelper.renderSection(camera, poseStack, pos, sectionOffset, chunk, slime);
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
                RenderHelperBlock.renderBlockOverlaySprite(state, poseStack, light, OverlayTexture.NO_OVERLAY, slime, state.getSeed(block), dir -> {
                    if ((data & (1 << dir.ordinal())) == 0) {
                        return false;
                    }

                    BlockPos neighborPos = block.relative(dir);
                    BlockState neighbor = chunk.getBlockState(neighborPos);
                    if (state.skipRendering(neighbor, dir)) {
                        return false;
                    }

                    return Block.shouldRenderFace(state, chunk, block, dir, neighborPos);
                });
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
