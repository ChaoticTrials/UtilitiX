package de.melanx.utilitix.content.track.rails;

import io.github.noeppi_noeppi.libx.base.tile.BlockEntityBase;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nonnull;

public class TileControllerRail extends BlockEntityBase {

    private ItemStack filterStack = ItemStack.EMPTY;

    public TileControllerRail(BlockEntityType<?> blockEntityTypeIn, BlockPos pos, BlockState state) {
        super(blockEntityTypeIn, pos, state);
    }

    @Override
    public void load(@Nonnull CompoundTag nbt) {
        super.load(nbt);
        this.filterStack = ItemStack.of(nbt.getCompound("FilterStack"));
    }

    @Nonnull
    @Override
    public CompoundTag save(CompoundTag compound) {
        compound.put("FilterStack", this.filterStack.save(new CompoundTag()));
        return super.save(compound);
    }

    public ItemStack getFilterStack() {
        return this.filterStack;
    }

    public void setFilterStack(ItemStack filterStack) {
        this.filterStack = filterStack.copy();
        this.setChanged();
    }
}
