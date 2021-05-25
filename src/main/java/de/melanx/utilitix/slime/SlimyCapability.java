package de.melanx.utilitix.slime;

import de.melanx.utilitix.UtilitiX;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.ByteArrayNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraft.util.LazyValue;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.fml.DistExecutor;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class SlimyCapability {
    
    public static final ResourceLocation KEY = new ResourceLocation(UtilitiX.getInstance().modid, "sticky_chunk");
    
    @CapabilityInject(StickyChunk.class)
    public static Capability<StickyChunk> STICKY_CHUNK;
    
    public static void register() {
        CapabilityManager.INSTANCE.register(StickyChunk.class, new Storage(), StickyChunk::new);
    }
    
    public static void attach(AttachCapabilitiesEvent<Chunk> event) {
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
    
    public static boolean canGlue(World world, BlockPos pos, Direction side) {
        BlockState state = world.getBlockState(pos);
        return state.isSolidSide(world, pos, side) && !state.isStickyBlock() && state.getBlockHardness(world, pos) >= 0;
    }
    
    public static class Storage implements Capability.IStorage<StickyChunk> {

        @Nullable
        @Override
        public INBT writeNBT(Capability<StickyChunk> capability, StickyChunk instance, Direction side) {
            return new ByteArrayNBT(instance.getStickies());
        }

        @Override
        public void readNBT(Capability<StickyChunk> capability, StickyChunk instance, Direction side, INBT nbt) {
            if (nbt instanceof ByteArrayNBT && ((ByteArrayNBT) nbt).getByteArray().length == 65536) {
                instance.setStickies(((ByteArrayNBT) nbt).getByteArray());
            }
        }
    }
    
    public static class SimpleProvider implements ICapabilityProvider, INBTSerializable<INBT> {

        public final Capability<?> capability;
        public final LazyValue<?> value;

        public <T> SimpleProvider(Capability<T> capability, LazyValue<? extends T> value) {
            this.capability = capability;
            this.value = value;
        }

        @Nonnull
        @Override
        public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
            return cap == this.capability ? LazyOptional.of(this.value::getValue).cast() : LazyOptional.empty();
        }

        @Override
        public INBT serializeNBT() {
            //noinspection unchecked
            return ((Capability<Object>) this.capability).writeNBT(this.value.getValue(), null);
        }

        @Override
        public void deserializeNBT(INBT nbt) {
            //noinspection unchecked
            ((Capability<Object>) this.capability).readNBT(this.value.getValue(), null, nbt);
        }
    }
}
