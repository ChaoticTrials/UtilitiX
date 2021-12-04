package de.melanx.utilitix.content.wireless;

import io.github.noeppi_noeppi.libx.util.NBTX;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.Objects;

public class WorldAndPos {

    public final ResourceKey<Level> dimension;
    public final BlockPos pos;

    public WorldAndPos(ResourceKey<Level> dimension, BlockPos pos) {
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

    public CompoundTag serialize() {
        CompoundTag nbt = new CompoundTag();
        nbt.putString("W", this.dimension.location().toString());
        //noinspection UnstableApiUsage
        NBTX.putPos(nbt, "P", this.pos);
        nbt.putIntArray("P", new int[]{this.pos.getX(), this.pos.getY(), this.pos.getZ()});
        return nbt;
    }

    @Nullable
    public static WorldAndPos deserialize(CompoundTag nbt) {
        ResourceKey<Level> world = ResourceKey.create(Registry.DIMENSION_REGISTRY, new ResourceLocation(nbt.getString("W")));
        //noinspection UnstableApiUsage
        BlockPos pos = NBTX.getPos(nbt, "P");
        return pos == null ? null : new WorldAndPos(world, pos);
    }
}
