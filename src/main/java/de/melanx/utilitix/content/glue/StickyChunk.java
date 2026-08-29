package de.melanx.utilitix.content.glue;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import de.melanx.utilitix.network.handler.StickyChunkUpdate;
import net.minecraft.core.Direction;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.common.util.ValueIOSerializable;
import net.neoforged.neoforge.network.PacketDistributor;

import javax.annotation.Nullable;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

public class StickyChunk implements ValueIOSerializable {

    @Nullable
    private LevelChunk chunk;
    private final Map<Integer, StickySection> sections; 

    public StickyChunk() {
        this.sections = new HashMap<>();
    }

    public boolean get(int x, int y, int z, Direction dir) {
        StickySection section = this.getSection(y);

        return section != null && section.get(x, y & 0xF, z, dir);
    }

    public boolean getAny(int x, int y, int z) {
        StickySection section = this.getSection(y);

        if (section == null) {
            return false;
        }

        for (Direction direction : Direction.values()) {
            if (section.get(x, y & 0xF, z, direction)) {
                return true;
            }
        }

        return false;
    }

    public void set(int x, int y, int z, Direction dir, boolean sticky) {
        StickySection section = this.getOrCreateSection(y);
        section.set(x, y & 0xF, z, dir, sticky);
    }

    public byte getData(int x, int y, int z) {
        StickySection section = this.getSection(y);

        return section == null ? (byte) 0 : section.getData(x, y & 0xF, z);
    }

    public void setData(int x, int y, int z, byte data) {
        StickySection section = this.getOrCreateSection(y);
        section.setData(x, y & 0xF, z, data);
    }

    public void clearData(int x, int y, int z) {
        StickySection section = this.getOrCreateSection(y);
        section.clearData(x, y & 0xF, z);
    }

    public void foreach(ChunkAction action) {
        for (Map.Entry<Integer, StickySection> entry : this.sections.entrySet()) {
            if (entry.getValue().canBeDiscarded()) {
                continue;
            }

            SectionAction sectionAction = action.section(entry.getKey(), entry.getKey() << 4);
            if (sectionAction != null) {
                entry.getValue().foreach(sectionAction);
            }
        }
    }
    
    public void sync() {
        if (this.chunk != null && !this.chunk.getLevel().isClientSide()) {
            this.chunk.markUnsaved();
            PacketDistributor.sendToPlayersTrackingChunk((ServerLevel) this.chunk.getLevel(), this.chunk.getPos(), new StickyChunkUpdate.Message(this.chunk.getPos(), this));
        }
    }
    
    @Nullable
    private StickySection getSection(int y) {
        return this.sections.getOrDefault(y >> 4, null);
    }
    
    private StickySection getOrCreateSection(int y) {
        return this.sections.computeIfAbsent(y >> 4, k -> new StickySection(this));
    }

    public void attach(LevelChunk chunk) {
        this.chunk = chunk;
    }

    public void write(FriendlyByteBuf buffer) {
        buffer.writeVarInt(this.sections.size());

        for (Map.Entry<Integer, StickySection> entry : this.sections.entrySet()) {
            buffer.writeVarInt(entry.getKey());
            entry.getValue().writeRawDataToBuffer(buffer);
        }
    }
    
    public void read(FriendlyByteBuf buffer) {
        int size = buffer.readVarInt();
        this.sections.clear();

        for (int i = 0; i < size; i++) {
            int sectionId = buffer.readVarInt();
            StickySection section = new StickySection(this);
            section.readRawDataFromBuffer(buffer);
            this.sections.put(sectionId, section);
        }
    }
    
    // Will clear the argument.
    public void loadFrom(StickyChunk networkChunk) {
        if (networkChunk.chunk != null) {
            throw new IllegalArgumentException("Can't copy data from attached chunk");
        }

        this.sections.clear();
        this.sections.putAll(networkChunk.sections);
        networkChunk.sections.clear();
    }

    // ValueInput/ValueOutput have no generic "list all dynamic keys" accessor (unlike the old CompoundTag),
    // so sections are stored as a list of (sectionId, stickies) pairs instead of dynamically-keyed fields.
    private static final Codec<Pair<Integer, ByteBuffer>> SECTION_CODEC = Codec.pair(Codec.INT, Codec.BYTE_BUFFER);

    @Override
    public void serialize(ValueOutput output) {
        ValueOutput.TypedOutputList<Pair<Integer, ByteBuffer>> list = output.list("Sections", SECTION_CODEC);
        for (Map.Entry<Integer, StickySection> entry : this.sections.entrySet()) {
            if (entry.getValue().canBeDiscarded()) {
                continue;
            }

            list.add(Pair.of(entry.getKey(), ByteBuffer.wrap(entry.getValue().getStickies())));
        }
    }

    @Override
    public void deserialize(ValueInput input) {
        this.sections.clear();

        for (Pair<Integer, ByteBuffer> entry : input.listOrEmpty("Sections", SECTION_CODEC)) {
            StickySection section = new StickySection(this);
            section.setStickies(entry.getSecond().array());
            this.sections.put(entry.getFirst(), section);
        }
    }

    public interface ChunkAction {

        @Nullable
        SectionAction section(int sectionId, int sectionOffset);
    }
    
    public interface SectionAction {

        void start();
        void accept(int x, int y, int z, byte data);
        void stop();
    }
}
