package de.melanx.utilitix.network;

import de.melanx.utilitix.UtilitiX;
import de.melanx.utilitix.content.slime.SlimyCapability;
import de.melanx.utilitix.content.slime.StickyChunk;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.PacketDistributor;

import java.util.function.Supplier;

public class StickyChunkRequestHandler {

    public static void handle(StickyChunkRequestSerializer.StickyChunkRequestMessage msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayerEntity sender = ctx.get().getSender();
            //noinspection deprecation
            if (sender != null && sender.getServerWorld().isBlockLoaded(new BlockPos(msg.pos.getXStart(), 0, msg.pos.getZStart()))) {
                Chunk chunk = sender.getServerWorld().getChunk(msg.pos.x, msg.pos.z);
                //noinspection ConstantConditions
                if (chunk != null && chunk.loaded) {
                    LazyOptional<StickyChunk> cap = chunk.getCapability(SlimyCapability.STICKY_CHUNK);
                    cap.ifPresent(value -> UtilitiX.getNetwork().instance.send(PacketDistributor.PLAYER.with(() -> sender), new StickyChunkUpdateSerializer.StickyChunkUpdateMessage(msg.pos, value)));
                }
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
