package de.melanx.utilitix.content.track.rails;

import de.melanx.utilitix.content.track.carts.piston.PistonCartMode;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nonnull;
import java.util.NoSuchElementException;

public class TilePistonControllerRail extends TileControllerRail {

    private PistonCartMode mode = PistonCartMode.IDLE;

    public TilePistonControllerRail(BlockEntityType<?> blockEntityTypeIn, BlockPos pos, BlockState state) {
        super(blockEntityTypeIn, pos, state);
    }

    public PistonCartMode getMode() {
        return this.mode;
    }

    public void setMode(PistonCartMode mode) {
        this.mode = mode;
        this.setChanged();
    }

    @Override
    public void loadAdditional(@Nonnull CompoundTag tag, @Nonnull HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        String modeName = tag.getString("Mode");
        try {
            this.mode = PistonCartMode.valueOf(modeName);
        } catch (IllegalArgumentException | NoSuchElementException e) {
            this.mode = PistonCartMode.IDLE;
        }
    }

    @Override
    public void saveAdditional(@Nonnull CompoundTag nbt, @Nonnull HolderLookup.Provider registries) {
        nbt.putString("Mode", this.mode.name());
    }
}
