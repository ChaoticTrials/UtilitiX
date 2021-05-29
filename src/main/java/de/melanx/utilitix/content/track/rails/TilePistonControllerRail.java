package de.melanx.utilitix.content.track.rails;

import de.melanx.utilitix.content.track.carts.piston.PistonCartMode;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntityType;

import javax.annotation.Nonnull;
import java.util.NoSuchElementException;

public class TilePistonControllerRail extends TileControllerRail {

    private PistonCartMode mode = PistonCartMode.IDLE;
    
    public TilePistonControllerRail(TileEntityType<?> tileEntityTypeIn) {
        super(tileEntityTypeIn);
    }
    
    public PistonCartMode getMode() {
        return this.mode;
    }

    public void setMode(PistonCartMode mode) {
        this.mode = mode;
        this.markDirty();
    }
    
    @Override
    public void read(@Nonnull BlockState state, @Nonnull CompoundNBT nbt) {
        super.read(state, nbt);
        String modeName = nbt.getString("Mode");
        try {
            this.mode = PistonCartMode.valueOf(modeName);
        } catch (IllegalArgumentException | NoSuchElementException e) {
            this.mode = PistonCartMode.IDLE;
        }
    }

    @Nonnull
    @Override
    public CompoundNBT write(CompoundNBT nbt) {
        nbt.putString("Mode", this.mode.name());
        return super.write(nbt);
    }
}
