package de.melanx.utilitix.mixin;

import de.melanx.utilitix.content.slime.SlimyCapability;
import de.melanx.utilitix.content.slime.StickyChunk;
import de.melanx.utilitix.util.MixinUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.piston.PistonMovingBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PistonMovingBlockEntity.class)
public class MixinPistonMovingBlockEntity {

    @Unique
    private Byte glueData;

    @Inject(
            method = "tick",
            at = @At("HEAD")
    )
    private static void tick(Level level, BlockPos pos, BlockState state, PistonMovingBlockEntity blockEntity, CallbackInfo ci) {
        //noinspection ConstantConditions
        if (((MixinPistonMovingBlockEntity) (Object) blockEntity).glueData == null) {
            BlockPos fromPos = pos.relative(blockEntity.isExtending() ? blockEntity.getDirection().getOpposite() : blockEntity.getDirection());
            LevelChunk chunk = level.getChunkAt(fromPos);
            //noinspection ConstantConditions
            StickyChunk glue = chunk.getCapability(SlimyCapability.STICKY_CHUNK).orElse(null);
            //noinspection ConstantConditions
            if (glue != null) {
                int x = fromPos.getX() & 0xF;
                int y = fromPos.getY();
                int z = fromPos.getZ() & 0xF;
                //noinspection ConstantConditions
                ((MixinPistonMovingBlockEntity) (Object) blockEntity).glueData = glue.getData(x, y, z);
                glue.clearData(x, y, z);
                chunk.setUnsaved(true);
            }
        }
    }

    @Inject(
            method = "tick",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/Level;setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;I)Z",
                    shift = At.Shift.AFTER
            )
    )
    private static void afterSetBlockState(Level level, BlockPos pos, BlockState state, PistonMovingBlockEntity blockEntity, CallbackInfo ci) {
        //noinspection ConstantConditions
        MixinUtil.afterSetBlockState(level, pos, ((MixinPistonMovingBlockEntity) (Object) blockEntity).glueData);
    }

    @Inject(
            method = "finalTick",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/Level;setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;I)Z",
                    shift = At.Shift.AFTER
            )
    )
    public void afterSetBlockState(CallbackInfo ci) {
        PistonMovingBlockEntity blockEntity = ((PistonMovingBlockEntity) (Object) this);
        Level level = blockEntity.getLevel();
        BlockPos pos = blockEntity.getBlockPos();
        MixinUtil.afterSetBlockState(level, pos, this.glueData);
    }

    @Inject(
            method = "load",
            at = @At("RETURN")
    )
    public void read(CompoundTag nbt, CallbackInfo ci) {
        if (nbt.contains("utilitix_glue_data", Tag.TAG_ANY_NUMERIC)) {
            this.glueData = nbt.getByte("utilitix_glue_data");
        }
    }

    @Inject(
            method = "saveAdditional",
            at = @At("HEAD")
    )
    public void write(CompoundTag nbt, CallbackInfo ci) {
        if (this.glueData != null) {
            nbt.putByte("utilitix_glue_data", this.glueData);
        }
    }
}
