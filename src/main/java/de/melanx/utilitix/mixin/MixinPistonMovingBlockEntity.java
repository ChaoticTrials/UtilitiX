package de.melanx.utilitix.mixin;

import com.mojang.serialization.Codec;
import de.melanx.utilitix.content.glue.StickyChunk;
import de.melanx.utilitix.registration.ModAttachmentTypes;
import de.melanx.utilitix.util.MixinUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.piston.PistonMovingBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PistonMovingBlockEntity.class)
public class MixinPistonMovingBlockEntity {

    @Unique
    private Byte utilitiX$glueData;

    @Inject(
            method = "tick",
            at = @At("HEAD")
    )
    private static void utilitix$tick(Level level, BlockPos pos, BlockState state, PistonMovingBlockEntity blockEntity, CallbackInfo ci) {
        //noinspection ConstantConditions
        if (((MixinPistonMovingBlockEntity) (Object) blockEntity).utilitiX$glueData != null) {
            return;
        }

        BlockPos fromPos = pos.relative(blockEntity.isExtending() ? blockEntity.getDirection().getOpposite() : blockEntity.getDirection());
        LevelChunk chunk = level.getChunkAt(fromPos);
        StickyChunk glue = chunk.getExistingDataOrNull(ModAttachmentTypes.stickyChunk);
        if (glue == null) {
            return;
        }

        int x = fromPos.getX() & 0xF;
        int y = fromPos.getY();
        int z = fromPos.getZ() & 0xF;
        //noinspection ConstantConditions
        ((MixinPistonMovingBlockEntity) (Object) blockEntity).utilitiX$glueData = glue.getData(x, y, z);
        glue.clearData(x, y, z);
        chunk.markUnsaved();
    }

    @Inject(
            method = "tick",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/Level;setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;I)Z",
                    shift = At.Shift.AFTER
            )
    )
    private static void utilitix$afterSetBlockState(Level level, BlockPos pos, BlockState state, PistonMovingBlockEntity blockEntity, CallbackInfo ci) {
        //noinspection ConstantConditions
        MixinUtil.afterSetBlockState(level, pos, ((MixinPistonMovingBlockEntity) (Object) blockEntity).utilitiX$glueData);
    }

    @Inject(
            method = "finalTick",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/Level;setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;I)Z",
                    shift = At.Shift.AFTER
            )
    )
    public void utilitix$afterSetBlockState(CallbackInfo ci) {
        PistonMovingBlockEntity blockEntity = ((PistonMovingBlockEntity) (Object) this);
        Level level = blockEntity.getLevel();
        BlockPos pos = blockEntity.getBlockPos();
        MixinUtil.afterSetBlockState(level, pos, this.utilitiX$glueData);
    }

    @Inject(
            method = "loadAdditional",
            at = @At("RETURN")
    )
    public void utilitix$read(ValueInput input, CallbackInfo ci) {
        this.utilitiX$glueData = input.read("utilitix_glue_data", Codec.BYTE).orElse(null);
    }

    @Inject(
            method = "saveAdditional",
            at = @At("HEAD")
    )
    public void utilitix$write(ValueOutput output, CallbackInfo ci) {
        if (this.utilitiX$glueData != null) {
            output.store("utilitix_glue_data", Codec.BYTE, this.utilitiX$glueData);
        }
    }
}
