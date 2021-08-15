package de.melanx.utilitix.content.slime;

import de.melanx.utilitix.UtilitiX;
import io.github.noeppi_noeppi.libx.util.LazyValue;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.ByteArrayTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class SlimyCapability {

    public static final ResourceLocation KEY = new ResourceLocation(UtilitiX.getInstance().modid, "sticky_chunk");

    @CapabilityInject(StickyChunk.class)
    public static Capability<StickyChunk> STICKY_CHUNK;

    public static void register() {
        CapabilityManager.INSTANCE.register(StickyChunk.class);
    }

    public static void attach(AttachCapabilitiesEvent<LevelChunk> event) {
        if (!event.getCapabilities().containsKey(KEY)) {
            LazyValue<StickyChunk> capInstance = new LazyValue<>(() -> {
                StickyChunk instance = new StickyChunk();
                instance.attach(event.getObject());
                return instance;
            });
            event.addCapability(KEY, new SimpleProvider(STICKY_CHUNK, capInstance));
        } else {
            event.getCapabilities().get(KEY).getCapability(STICKY_CHUNK).ifPresent(s -> s.attach(event.getObject()));
        }
    }

    public static boolean canGlue(Level level, BlockPos pos, Direction side) {
        BlockState state = level.getBlockState(pos);
        return state.isFaceSturdy(level, pos, side) && !state.isStickyBlock() && state.getDestroySpeed(level, pos) >= 0;
    }

    public static class SimpleProvider implements ICapabilityProvider, INBTSerializable<Tag> {

        public final Capability<?> capability;
        public final LazyValue<?> value;

        public <T> SimpleProvider(Capability<T> capability, LazyValue<? extends T> value) {
            this.capability = capability;
            this.value = value;
        }

        @Nonnull
        @Override
        public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
            //noinspection NullableProblems
            return cap == this.capability ? LazyOptional.of(this.value::get).cast() : LazyOptional.empty();
        }

        @Override
        public Tag serializeNBT() {
            return new ByteArrayTag((byte[]) this.value.get());
        }

        @Override
        public void deserializeNBT(Tag nbt) {
            ((StickyChunk) this.value.get()).setStickies(((ByteArrayTag) nbt).getAsByteArray());
        }
    }
}
