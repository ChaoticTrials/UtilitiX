package de.melanx.utilitix.network.handler;

import de.melanx.utilitix.UtilitiX;
import de.melanx.utilitix.content.glue.StickyChunk;
import de.melanx.utilitix.registration.ModAttachmentTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.LevelChunk;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.network.registration.HandlerThread;
import org.moddingx.libx.network.PacketHandler;

import javax.annotation.Nonnull;
import java.util.Optional;

public class StickyChunkRequest extends PacketHandler<StickyChunkRequest.Message> {

    public static final CustomPacketPayload.Type<Message> TYPE = new CustomPacketPayload.Type<>(UtilitiX.getInstance().id("sticky_chunk_request"));

    public StickyChunkRequest() {
        super(TYPE, PacketFlow.SERVERBOUND, Message.CODEC, HandlerThread.MAIN);
    }

    @Override
    public void handle(Message msg, IPayloadContext ctx) {
        if (!(ctx.player() instanceof ServerPlayer sender)) {
            return;
        }

        //noinspection deprecation
        if (!sender.level().hasChunkAt(new BlockPos(msg.pos().getMinBlockX(), 0, msg.pos().getMinBlockZ()))) {
            return;
        }

        LevelChunk chunk = sender.level().getChunk(msg.pos().x(), msg.pos().z());
        //noinspection ConstantConditions
        if (chunk != null && chunk.loaded) {
            // Sticky data is stored as an attachment on the chunk, not on the level.
            Optional<StickyChunk> stickyChunk = chunk.getExistingData(ModAttachmentTypes.stickyChunk);
            stickyChunk.ifPresent(value -> PacketDistributor.sendToPlayer(sender, new StickyChunkUpdate.Message(msg.pos(), value)));
        }
    }

    public record Message(ChunkPos pos) implements CustomPacketPayload {

        public static final StreamCodec<RegistryFriendlyByteBuf, Message> CODEC = StreamCodec.of(
                (buffer, msg) -> {
                    buffer.writeInt(msg.pos.x());
                    buffer.writeInt(msg.pos.z());
                }, buffer -> new Message(new ChunkPos(buffer.readInt(), buffer.readInt()))
        );

        @Nonnull
        @Override
        public Type<? extends CustomPacketPayload> type() {
            return StickyChunkRequest.TYPE;
        }
    }
}
