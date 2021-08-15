package de.melanx.utilitix.network;

import de.melanx.utilitix.UtilitiX;
import de.melanx.utilitix.content.slime.SlimyCapability;
import de.melanx.utilitix.content.slime.StickyChunk;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fmllegacy.network.NetworkEvent;
import net.minecraftforge.fmllegacy.network.PacketDistributor;

import java.util.function.Supplier;

public class StickyChunkRequestHandler {

    public static void handle(StickyChunkRequestSerializer.StickyChunkRequestMessage msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer sender = ctx.get().getSender();
            //noinspection deprecation
            if (sender != null && sender.getLevel().hasChunkAt(new BlockPos(msg.pos().getMinBlockX(), 0, msg.pos().getMinBlockZ()))) {
                LevelChunk chunk = sender.getLevel().getChunk(msg.pos().x, msg.pos().z);
                //noinspection ConstantConditions
                if (chunk != null && chunk.loaded) {
                    LazyOptional<StickyChunk> cap = chunk.getCapability(SlimyCapability.STICKY_CHUNK);
                    cap.ifPresent(value -> UtilitiX.getNetwork().instance.send(PacketDistributor.PLAYER.with(() -> sender), new StickyChunkUpdateSerializer.StickyChunkUpdateMessage(msg.pos(), value)));
                }
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
