package de.melanx.utilitix.wireless;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.Objects;

public class WorldAndPos {

    public final RegistryKey<World> dimension;
    public final BlockPos pos;

    public WorldAndPos(RegistryKey<World> dimension, BlockPos pos) {
        this.dimension = dimension;
        this.pos = pos;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || this.getClass() != o.getClass()) return false;
        WorldAndPos that = (WorldAndPos) o;
        return Objects.equals(this.dimension, that.dimension) && Objects.equals(this.pos, that.pos);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.dimension, this.pos);
    }

    public CompoundNBT serialize() {
        CompoundNBT nbt = new CompoundNBT();
        nbt.putString("W", this.dimension.getLocation().toString());
        nbt.putIntArray("P", new int[]{this.pos.getX(), this.pos.getY(), this.pos.getZ() });
        return nbt;
    }
    
    @Nullable
    public static WorldAndPos deserialize(CompoundNBT nbt) {
        RegistryKey<World> world = RegistryKey.getOrCreateKey(Registry.WORLD_KEY, new ResourceLocation(nbt.getString("W")));
        int[] loc = nbt.getIntArray("P");
        if (loc.length == 3) {
            BlockPos pos = new BlockPos(loc[0], loc[1], loc[2]);
            return new WorldAndPos(world, pos);
        } else {
            return null;
        }
    }
}
