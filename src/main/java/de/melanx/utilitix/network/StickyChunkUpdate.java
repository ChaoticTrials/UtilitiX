package de.melanx.utilitix.network;

import de.melanx.utilitix.UtilitiX;
import de.melanx.utilitix.content.slime.SlimyCapability;
import de.melanx.utilitix.content.slime.StickyChunk;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraftforge.network.NetworkEvent;
import org.moddingx.libx.network.PacketHandler;
import org.moddingx.libx.network.PacketSerializer;

import java.util.function.Supplier;

public record StickyChunkUpdate(ChunkPos pos, StickyChunk data) {

    public static class Handler implements PacketHandler<StickyChunkUpdate> {

        @Override
        public Target target() {
            return Target.MAIN_THREAD;
        }

        @Override
        public boolean handle(StickyChunkUpdate msg, Supplier<NetworkEvent.Context> ctx) {
            Level level = Minecraft.getInstance().level;
            if (level != null) {
                //noinspection deprecation
                if (level.hasChunkAt(new BlockPos(msg.pos().getMinBlockX(), 0, msg.pos().getMinBlockZ()))) {
                    LevelChunk loaded = level.getChunk(msg.pos().x, msg.pos().z);
                    //noinspection ConstantConditions
                    if (loaded != null) {
                        //noinspection ConstantConditions
                        StickyChunk glue = loaded.getCapability(SlimyCapability.STICKY_CHUNK).orElse(null);
                        //noinspection ConstantConditions
                        if (glue != null) {
                            glue.loadFrom(msg.data());
                            return true;
                        }
                    }
                }
            }

            UtilitiX.getInstance().logger.warn("Received invalid sticky chunk packet for unloaded chunk: (" + msg.pos().x + "," + msg.pos().z + ")");
            return true;
        }
    }

    public static class Serializer implements PacketSerializer<StickyChunkUpdate> {

        @Override
        public Class<StickyChunkUpdate> messageClass() {
            return StickyChunkUpdate.class;
        }

        @Override
        public void encode(StickyChunkUpdate msg, FriendlyByteBuf buffer) {
            buffer.writeInt(msg.pos().x);
            buffer.writeInt(msg.pos().z);
            msg.data().write(buffer);
        }

        @Override
        public StickyChunkUpdate decode(FriendlyByteBuf buffer) {
            ChunkPos pos = new ChunkPos(buffer.readInt(), buffer.readInt());
            StickyChunk chunk = new StickyChunk();
            chunk.read(buffer);
            return new StickyChunkUpdate(pos, chunk);
        }
    }
}
