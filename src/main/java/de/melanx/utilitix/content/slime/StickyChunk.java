package de.melanx.utilitix.content.slime;

import de.melanx.utilitix.UtilitiX;
import de.melanx.utilitix.network.StickyChunkUpdateSerializer;
import net.minecraft.core.Direction;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraftforge.fmllegacy.network.PacketDistributor;

import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.Set;

public class StickyChunk {

    @Nullable
    private LevelChunk chunk;
    private byte[] stickies;
    // For performance
    private Set<Integer> indicesWithGlue = null;

    public StickyChunk() {
        this.stickies = new byte[65536];
    }

    public boolean get(int x, int y, int z, Direction dir) {
        int idx = ((y & 0xFF) << 8) | ((z & 0xF) << 4) | (x & 0xF);
        byte data = this.stickies[idx];
        return (data & (1 << dir.ordinal())) != 0;
    }

    public void set(int x, int y, int z, Direction dir, boolean sticky) {
        int idx = ((y & 0xFF) << 8) | ((z & 0xF) << 4) | (x & 0xF);
        if (sticky) {
            this.stickies[idx] = (byte) (this.stickies[idx] | ((byte) (1 << dir.ordinal())));
        } else {
            this.stickies[idx] = (byte) (this.stickies[idx] & ~((byte) (1 << dir.ordinal())));
        }
        this.indicesWithGlue = null;
        if (this.chunk != null && !this.chunk.getLevel().isClientSide) {
            this.chunk.markUnsaved();
            UtilitiX.getNetwork().instance.send(PacketDistributor.TRACKING_CHUNK.with(() -> this.chunk), new StickyChunkUpdateSerializer.StickyChunkUpdateMessage(this.chunk.getPos(), this));
        }
    }

    public byte getData(int x, int y, int z) {
        int idx = ((y & 0xFF) << 8) | ((z & 0xF) << 4) | (x & 0xF);
        return this.stickies[idx];
    }

    public void setData(int x, int y, int z, byte data) {
        int idx = ((y & 0xFF) << 8) | ((z & 0xF) << 4) | (x & 0xF);
        this.stickies[idx] = data;
        this.indicesWithGlue = null;
        if (this.chunk != null && !this.chunk.getLevel().isClientSide) {
            this.chunk.markUnsaved();
            UtilitiX.getNetwork().instance.send(PacketDistributor.TRACKING_CHUNK.with(() -> this.chunk), new StickyChunkUpdateSerializer.StickyChunkUpdateMessage(this.chunk.getPos(), this));
        }
    }

    public void clearData(int x, int y, int z) {
        int idx = ((y & 0xFF) << 8) | ((z & 0xF) << 4) | (x & 0xF);
        this.stickies[idx] = 0;
        this.indicesWithGlue = null;
        if (this.chunk != null && !this.chunk.getLevel().isClientSide) {
            this.chunk.markUnsaved();
            UtilitiX.getNetwork().instance.send(PacketDistributor.TRACKING_CHUNK.with(() -> this.chunk), new StickyChunkUpdateSerializer.StickyChunkUpdateMessage(this.chunk.getPos(), this));
        }
    }

    public void foreach(StickyAction action) {
        if (this.indicesWithGlue == null) {
            this.indicesWithGlue = new HashSet<>();
            for (int idx = 0; idx < this.stickies.length; idx++) {
                byte data = this.stickies[idx];
                if (data != 0) {
                    this.indicesWithGlue.add(idx);
                }
            }
        }
        for (int idx : this.indicesWithGlue) {
            byte data = this.stickies[idx];
            if (data != 0) {
                int y = (idx >>> 8) & 0xFF;
                int z = (idx >>> 4) & 0xF;
                int x = idx & 0xF;
                action.accept(x, y, z, data);
            }
        }
    }

    public byte[] getStickies() {
        return this.stickies;
    }

    public void setStickies(byte[] data) {
        if (data.length != 65536)
            throw new IllegalStateException("Invalid size of sticky data for chunk: " + data.length);
        this.stickies = data;
        this.indicesWithGlue = null;
    }

    public void attach(LevelChunk chunk) {
        this.chunk = chunk;
    }

    public interface StickyAction {

        void accept(int x, int y, int z, byte data);
    }
}
