package de.melanx.utilitix.content.slime;

import de.melanx.utilitix.UtilitiX;
import de.melanx.utilitix.network.StickyChunkUpdateSerializer;
import net.minecraft.util.Direction;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.fml.network.PacketDistributor;

import javax.annotation.Nullable;

public class StickyChunk {
    
    @Nullable
    private Chunk chunk;
    private byte[] stickies;
    
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
        if (this.chunk != null && !this.chunk.getWorld().isRemote) {
            this.chunk.markDirty();
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
        if (this.chunk != null && !this.chunk.getWorld().isRemote) {
            this.chunk.markDirty();
            UtilitiX.getNetwork().instance.send(PacketDistributor.TRACKING_CHUNK.with(() -> this.chunk), new StickyChunkUpdateSerializer.StickyChunkUpdateMessage(this.chunk.getPos(), this));
        }
    }
    
    public void clearData(int x, int y, int z) {
        int idx = ((y & 0xFF) << 8) | ((z & 0xF) << 4) | (x & 0xF);
        this.stickies[idx] = 0;
        if (this.chunk != null && !this.chunk.getWorld().isRemote) {
            this.chunk.markDirty();
            UtilitiX.getNetwork().instance.send(PacketDistributor.TRACKING_CHUNK.with(() -> this.chunk), new StickyChunkUpdateSerializer.StickyChunkUpdateMessage(this.chunk.getPos(), this));
        }
    }
    
    public void foreach(StickyAction action) {
        for (int idx = 0; idx < this.stickies.length; idx++) {
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
        if (data.length != 65536) throw new IllegalStateException("Invalid size of sticky data for chunk: " + data.length);
        this.stickies = data;
    }
    
    public void attach(Chunk chunk) {
        this.chunk = chunk;
    }

    public interface StickyAction {
        
        void accept(int x, int y, int z, byte data);
    }
}
