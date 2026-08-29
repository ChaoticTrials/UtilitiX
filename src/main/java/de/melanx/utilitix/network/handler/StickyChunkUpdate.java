package de.melanx.utilitix.network.handler;

import de.melanx.utilitix.UtilitiX;
import de.melanx.utilitix.content.glue.StickyChunk;
import de.melanx.utilitix.registration.ModAttachmentTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.network.registration.HandlerThread;
import org.moddingx.libx.network.PacketHandler;

import javax.annotation.Nonnull;

public class StickyChunkUpdate extends PacketHandler<StickyChunkUpdate.Message> {

    public static final CustomPacketPayload.Type<Message> TYPE = new CustomPacketPayload.Type<>(UtilitiX.getInstance().id("sticky_chunk_update"));

    public StickyChunkUpdate() {
        super(TYPE, PacketFlow.CLIENTBOUND, Message.CODEC, HandlerThread.MAIN);
    }

    @Override
    public void handle(StickyChunkUpdate.Message msg, IPayloadContext ctx) {
        Level level = ctx.player().level();
        if (!level.isClientSide()) {
            this.sendWarning(msg);
            return;
        }

        //noinspection deprecation
        if (!level.hasChunkAt(new BlockPos(msg.pos().getMinBlockX(), 0, msg.pos().getMinBlockZ()))) {
            this.sendWarning(msg);
            return;
        }

        LevelChunk loaded = level.getChunk(msg.pos().x(), msg.pos().z());
        //noinspection ConstantConditions
        if (loaded == null) {
            this.sendWarning(msg);
            return;
        }

        // Sticky data is stored as an attachment on the chunk, not on the level.
        // We create the attachment if needed so the client can render it afterwards.
        StickyChunk glue = loaded.getData(ModAttachmentTypes.stickyChunk);
        // Ensure the instance is attached to the owning chunk (needed for future syncs).
        glue.attach(loaded);
        glue.loadFrom(msg.data());
    }

    private void sendWarning(StickyChunkUpdate.Message msg) {
        UtilitiX.getInstance().logger.warn("Received invalid sticky chunk packet for unloaded chunk: ({},{})", msg.pos().x(), msg.pos().z());
    }

    public record Message(ChunkPos pos, StickyChunk data) implements CustomPacketPayload {

        public static final StreamCodec<RegistryFriendlyByteBuf, Message> CODEC = StreamCodec.of(
                (buffer, msg) -> {
                    buffer.writeInt(msg.pos().x());
                    buffer.writeInt(msg.pos().z());
                    msg.data().write(buffer);
                }, buffer -> {
                    ChunkPos pos = new ChunkPos(buffer.readInt(), buffer.readInt());
                    StickyChunk chunk = new StickyChunk();
                    chunk.read(buffer);
                    return new StickyChunkUpdate.Message(pos, chunk);
                }
        );

        @Nonnull
        @Override
        public Type<? extends CustomPacketPayload> type() {
            return StickyChunkUpdate.TYPE;
        }
    }
}
