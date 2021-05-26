package de.melanx.utilitix.content.slime;

import com.mojang.blaze3d.matrix.MatrixStack;
import de.melanx.utilitix.Textures;
import io.github.noeppi_noeppi.libx.render.RenderHelperBlock;
import io.github.noeppi_noeppi.libx.render.RenderHelperWorld;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.inventory.container.PlayerContainer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.client.event.RenderWorldLastEvent;

public class SlimeRender {
    
    public static void renderWorld(RenderWorldLastEvent event) {
        if (Minecraft.getInstance().world != null) {
            Minecraft.getInstance().getTextureManager().bindTexture(PlayerContainer.LOCATION_BLOCKS_TEXTURE);
            TextureAtlasSprite slime = Minecraft.getInstance().getAtlasSpriteGetter(PlayerContainer.LOCATION_BLOCKS_TEXTURE).apply(Textures.GLUE_OVERLAY_TEXTURE);
            if (slime != null) {
                int size = Minecraft.getInstance().world.getChunkProvider().array.chunks.length();
                for (int i = 0; i < size; i++) {
                    Chunk chunk = Minecraft.getInstance().world.getChunkProvider().array.chunks.get(i);
                    if (chunk != null) {
                        ChunkPos pos = chunk.getPos();
                        //noinspection ConstantConditions
                        StickyChunk data = chunk.getCapability(SlimyCapability.STICKY_CHUNK).orElse(null);
                        //noinspection ConstantConditions
                        if (data != null) {
                            event.getMatrixStack().push();
                            RenderHelperWorld.loadProjection(event.getMatrixStack(), pos.getXStart(), 0, pos.getZStart());
                            renderChunk(event.getMatrixStack(), Minecraft.getInstance().getRenderTypeBuffers().getBufferSource(), pos, chunk, data, slime);
                            event.getMatrixStack().pop();
                        }
                    }
                }
                Minecraft.getInstance().getRenderTypeBuffers().getBufferSource().finish();
            }
        }
    }
    
    private static void renderChunk(MatrixStack matrixStack, IRenderTypeBuffer buffer, ChunkPos pos, Chunk chunk, StickyChunk data, TextureAtlasSprite slime) {
        data.foreach((x, y, z, flags) -> {
            BlockPos block = new BlockPos(pos.getXStart() + x, y, pos.getZStart() + z);
            BlockState state = chunk.getBlockState(block);
            int lightValue = state.getLightValue(chunk, block);
            int light = LightTexture.packLight(lightValue, lightValue);
            matrixStack.push();
            matrixStack.translate(x, y, z);
            RenderHelperBlock.renderBlockOverlaySprite(state, matrixStack, buffer, light, OverlayTexture.NO_OVERLAY, slime, state.getPositionRandom(block), dir -> (flags & (1 << dir.ordinal())) != 0);
            Minecraft.getInstance().getRenderTypeBuffers().getCrumblingBufferSource().finish();
            matrixStack.pop();
        });
    }
}
