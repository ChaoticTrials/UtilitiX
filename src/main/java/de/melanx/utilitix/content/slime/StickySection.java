package de.melanx.utilitix.content.slime;

import de.melanx.utilitix.UtilitiX;
import net.minecraft.core.Direction;
import net.minecraft.network.FriendlyByteBuf;

import java.util.HashSet;
import java.util.Set;

public class StickySection {
    
    private byte[] stickies;
    // For performance
    private Set<Integer> indicesWithGlue = null;
    private final StickyChunk chunk;

    public StickySection(StickyChunk chunk) {
        this.chunk = chunk;
        this.stickies = new byte[4096];
    }

    public boolean get(int x, int y, int z, Direction dir) {
        int idx = ((y & 0xF) << 8) | ((z & 0xF) << 4) | (x & 0xF);
        byte data = this.stickies[idx];
        return (data & (1 << dir.ordinal())) != 0;
    }

    public void set(int x, int y, int z, Direction dir, boolean sticky) {
        int idx = ((y & 0xF) << 8) | ((z & 0xF) << 4) | (x & 0xF);
        if (sticky) {
            this.stickies[idx] = (byte) (this.stickies[idx] | ((byte) (1 << dir.ordinal())));
        } else {
            this.stickies[idx] = (byte) (this.stickies[idx] & ~((byte) (1 << dir.ordinal())));
        }
        this.indicesWithGlue = null;
        this.chunk.sync();
    }

    public byte getData(int x, int y, int z) {
        int idx = ((y & 0xF) << 8) | ((z & 0xF) << 4) | (x & 0xF);
        return this.stickies[idx];
    }

    public void setData(int x, int y, int z, byte data) {
        int idx = ((y & 0xF) << 8) | ((z & 0xF) << 4) | (x & 0xF);
        this.stickies[idx] = data;
        this.indicesWithGlue = null;
        this.chunk.sync();
    }

    public void clearData(int x, int y, int z) {
        int idx = ((y & 0xF) << 8) | ((z & 0xF) << 4) | (x & 0xF);
        this.stickies[idx] = 0;
        this.indicesWithGlue = null;
        this.chunk.sync();
    }

    public boolean canBeDiscarded() {
        this.calculateIndicesWithGlueIfNeeded();
        return this.indicesWithGlue.isEmpty();
    }
    
    public void foreach(StickyChunk.SectionAction action) {
        this.calculateIndicesWithGlueIfNeeded();
        action.start();
        try {
            for (int idx : this.indicesWithGlue) {
                byte data = this.stickies[idx];
                if (data != 0) {
                    int y = (idx >>> 8) & 0xF;
                    int z = (idx >>> 4) & 0xF;
                    int x = idx & 0xF;
                    action.accept(x, y, z, data);
                }
            }
        } finally {
            action.stop();
        }
    }

    private void calculateIndicesWithGlueIfNeeded() {
        if (this.indicesWithGlue == null) {
            this.indicesWithGlue = new HashSet<>();
            for (int idx = 0; idx < this.stickies.length; idx++) {
                byte data = this.stickies[idx];
                if (data != 0) {
                    this.indicesWithGlue.add(idx);
                }
            }
        }
    }
    
    public byte[] getStickies() {
        byte[] copy = new byte[4096];
        System.arraycopy(this.stickies, 0, copy, 0, 4096);
        return copy;
    }

    public void setStickies(byte[] data) {
        if (data.length != 4096) {
            UtilitiX.getInstance().logger.error("Invalid size of sticky data for chunk section: " + data.length);
            this.stickies = new byte[4096];
            System.arraycopy(data, 0, this.stickies, 0, 4096);
        } else {
            this.stickies = data;
            this.indicesWithGlue = null;
        }
    }
    
    public void writeRawDataToBuffer(FriendlyByteBuf buffer) {
        buffer.writeBytes(this.stickies);
    }
    
    public void readRawDataFromBuffer(FriendlyByteBuf buffer) {
        this.stickies = new byte[4096];
        buffer.readBytes(this.stickies);
        this.indicesWithGlue = null;
    }
}
