package de.melanx.utilitix.network;

import de.melanx.utilitix.UtilitiX;
import de.melanx.utilitix.content.slime.SlimyCapability;
import de.melanx.utilitix.content.slime.StickyChunk;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;
import org.moddingx.libx.network.PacketHandler;
import org.moddingx.libx.network.PacketSerializer;

import java.util.function.Supplier;

public record StickyChunkRequest(ChunkPos pos) {

    public static class Handler implements PacketHandler<StickyChunkRequest> {

        @Override
        public Target target() {
            return Target.MAIN_THREAD;
        }

        @Override
        public boolean handle(StickyChunkRequest msg, Supplier<NetworkEvent.Context> ctx) {
            ServerPlayer sender = ctx.get().getSender();
            //noinspection deprecation
            if (sender != null && sender.level().hasChunkAt(new BlockPos(msg.pos().getMinBlockX(), 0, msg.pos().getMinBlockZ()))) {
                LevelChunk chunk = sender.level().getChunk(msg.pos().x, msg.pos().z);
                //noinspection ConstantConditions
                if (chunk != null && chunk.loaded) {
                    LazyOptional<StickyChunk> cap = chunk.getCapability(SlimyCapability.STICKY_CHUNK);
                    cap.ifPresent(value -> UtilitiX.getNetwork().channel.send(PacketDistributor.PLAYER.with(() -> sender), new StickyChunkUpdate(msg.pos(), value)));
                }
            }

            return true;
        }
    }

    public static class Serializer implements PacketSerializer<StickyChunkRequest> {

        @Override
        public Class<StickyChunkRequest> messageClass() {
            return StickyChunkRequest.class;
        }

        @Override
        public void encode(StickyChunkRequest msg, FriendlyByteBuf buffer) {
            buffer.writeInt(msg.pos.x);
            buffer.writeInt(msg.pos.z);
        }

        @Override
        public StickyChunkRequest decode(FriendlyByteBuf buffer) {
            return new StickyChunkRequest(new ChunkPos(buffer.readInt(), buffer.readInt()));
        }
    }
}
