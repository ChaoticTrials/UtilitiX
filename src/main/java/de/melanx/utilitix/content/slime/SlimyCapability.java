package de.melanx.utilitix.content.slime;

import de.melanx.utilitix.UtilitiX;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class SlimyCapability {

    public static final ResourceLocation KEY = ResourceLocation.fromNamespaceAndPath(UtilitiX.getInstance().modid, "sticky_chunk");

//    public static final Capability<StickyChunk> STICKY_CHUNK = CapabilityManager.get(new CapabilityToken<>() {}); todo

//    public static void attach(AttachCapabilitiesEvent<LevelChunk> event) {
//        if (!event.getCapabilities().containsKey(KEY)) {
//            LazyValue<StickyChunk> capInstance = new LazyValue<>(() -> {
//                StickyChunk instance = new StickyChunk();
//                instance.attach(event.getObject());
//                return instance;
//            });
//            event.addCapability(KEY, new SimpleProvider(STICKY_CHUNK, capInstance));
//        } else {
//            event.getCapabilities().get(KEY).getCapability(STICKY_CHUNK).ifPresent(s -> s.attach(event.getObject()));
//        }
//    }

    public static boolean canGlue(Level level, BlockPos pos, Direction side) {
        BlockState state = level.getBlockState(pos);
        return state.isFaceSturdy(level, pos, side) && !state.isStickyBlock() && state.getDestroySpeed(level, pos) >= 0;
    }
}
