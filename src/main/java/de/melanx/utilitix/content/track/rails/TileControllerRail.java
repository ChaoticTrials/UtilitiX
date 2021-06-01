package de.melanx.utilitix.content.track.rails;

import io.github.noeppi_noeppi.libx.mod.registration.TileEntityBase;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntityType;

import javax.annotation.Nonnull;

public class TileControllerRail extends TileEntityBase {

    private ItemStack filterStack = ItemStack.EMPTY;

    public TileControllerRail(TileEntityType<?> tileEntityTypeIn) {
        super(tileEntityTypeIn);
    }

    @Override
    public void read(@Nonnull BlockState state, @Nonnull CompoundNBT nbt) {
        super.read(state, nbt);
        this.filterStack = ItemStack.read(nbt.getCompound("FilterStack"));
    }

    @Nonnull
    @Override
    public CompoundNBT write(CompoundNBT nbt) {
        nbt.put("FilterStack", this.filterStack.write(new CompoundNBT()));
        return super.write(nbt);
    }

    public ItemStack getFilterStack() {
        return this.filterStack;
    }

    public void setFilterStack(ItemStack filterStack) {
        this.filterStack = filterStack.copy();
        this.markDirty();
    }
}
