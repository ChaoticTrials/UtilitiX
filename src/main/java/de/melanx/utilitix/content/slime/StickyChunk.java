package de.melanx.utilitix.content.slime;

import de.melanx.utilitix.UtilitiX;
import de.melanx.utilitix.network.StickyChunkUpdate;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraftforge.network.PacketDistributor;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

public class StickyChunk {

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
            if (!entry.getValue().canBeDiscarded()) {
                SectionAction sectionAction = action.section(entry.getKey(), entry.getKey() << 4);
                if (sectionAction != null) {
                    entry.getValue().foreach(sectionAction);
                }
            }
        }
    }
    
    public void sync() {
        if (this.chunk != null && !this.chunk.getLevel().isClientSide) {
            this.chunk.setUnsaved(true);
            UtilitiX.getNetwork().channel.send(PacketDistributor.TRACKING_CHUNK.with(() -> this.chunk), new StickyChunkUpdate(this.chunk.getPos(), this));
        }
    }
    
    @Nullable
    private StickySection getSection(int y) {
        return this.sections.getOrDefault(y >> 4, null);
    }
    
    private StickySection getOrCreateSection(int y) {
        // No need to sync as the newly created section is empty.
        return this.sections.computeIfAbsent(y >> 4, k -> new StickySection(this));
    }

    public void attach(LevelChunk chunk) {
        this.chunk = chunk;
    }

    public CompoundTag write() {
        CompoundTag nbt = new CompoundTag();
        for (Map.Entry<Integer, StickySection> entry : this.sections.entrySet()) {
            if (!entry.getValue().canBeDiscarded()) {
                nbt.putByteArray(Integer.toString(entry.getKey()), entry.getValue().getStickies());
            }
        }
        return nbt;
    }
    
    public void read(CompoundTag nbt) {
        this.sections.clear();
        for (String key : nbt.getAllKeys()) {
            if (!nbt.contains(key, Tag.TAG_BYTE_ARRAY)) {
                UtilitiX.getInstance().logger.error("Invalid chunk section value in sticky chunk for: " + key);
                continue;
            }
            try {
                int sectionId = Integer.parseInt(key);
                StickySection section = new StickySection(this);
                section.setStickies(nbt.getByteArray(key));
                this.sections.put(sectionId, section);
            } catch (NumberFormatException e) {
                UtilitiX.getInstance().logger.error("Invalid chunk section id in sticky chunk: " + key);
            }
        }
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
