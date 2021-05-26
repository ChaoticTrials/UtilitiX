package de.melanx.utilitix.mixin;

import de.melanx.utilitix.content.slime.SlimyCapability;
import de.melanx.utilitix.content.slime.StickyChunk;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.PistonTileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.common.util.Constants;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PistonTileEntity.class)
public class MixinPistonTileEntity {

    @Unique
    private Byte glueData;

    @Inject(
            method = "Lnet/minecraft/tileentity/PistonTileEntity;tick()V",
            at = @At("HEAD")
    )
    public void tick(CallbackInfo ci) {
        if (this.glueData == null) {
            PistonTileEntity tile = ((PistonTileEntity) (Object) this);
            World world = tile.getWorld();
            BlockPos pos = tile.getPos();
            //noinspection ConstantConditions
            if (world != null && pos != null) {
                BlockPos fromPos = pos.offset(tile.isExtending() ? tile.getFacing().getOpposite() : tile.getFacing());
                Chunk chunk = world.getChunkAt(fromPos);
                //noinspection ConstantConditions
                StickyChunk glue = chunk.getCapability(SlimyCapability.STICKY_CHUNK).orElse(null);
                //noinspection ConstantConditions
                if (glue != null) {
                    int x = fromPos.getX() & 0xF;
                    int y = fromPos.getY();
                    int z = fromPos.getZ() & 0xF;
                    this.glueData = glue.getData(x, y, z);
                    glue.clearData(x, y, z);
                    chunk.markDirty();
                }
            }
        }
    }

    @Inject(
            method = {
                    "Lnet/minecraft/tileentity/PistonTileEntity;tick()V",
                    "Lnet/minecraft/tileentity/PistonTileEntity;clearPistonTileEntity()V"
            },
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/World;setBlockState(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;I)Z",
                    shift = At.Shift.AFTER
            )
    )
    public void afterSetBlockState(CallbackInfo ci) {
        PistonTileEntity tile = ((PistonTileEntity) (Object) this);
        World world = tile.getWorld();
        BlockPos pos = tile.getPos();
        //noinspection ConstantConditions
        if (world != null && pos != null && this.glueData != null) {
            Chunk chunk = world.getChunkAt(pos);
            //noinspection ConstantConditions
            StickyChunk glue = chunk.getCapability(SlimyCapability.STICKY_CHUNK).orElse(null);
            //noinspection ConstantConditions
            if (glue != null) {
                int x = pos.getX() & 0xF;
                int y = pos.getY();
                int z = pos.getZ() & 0xF;
                glue.setData(x, y, z, this.glueData);
                chunk.markDirty();
            }
        }
    }
    
    @Inject(
            method = "Lnet/minecraft/tileentity/PistonTileEntity;read(Lnet/minecraft/block/BlockState;Lnet/minecraft/nbt/CompoundNBT;)V",
            at = @At("RETURN")
    )
    public void read(BlockState state, CompoundNBT nbt, CallbackInfo ci) {
        if (nbt.contains("utilitix_glue_data", Constants.NBT.TAG_ANY_NUMERIC)) {
            this.glueData = nbt.getByte("utilitix_glue_data");
        }
    }
    
    @Inject(
            method = "Lnet/minecraft/tileentity/PistonTileEntity;write(Lnet/minecraft/nbt/CompoundNBT;)Lnet/minecraft/nbt/CompoundNBT;",
            at = @At("HEAD")
    )
    public void write(CompoundNBT nbt, CallbackInfoReturnable<CompoundNBT> cir) {
        if (this.glueData != null) {
            nbt.putByte("utilitix_glue_data", this.glueData);
        }
    }
}
