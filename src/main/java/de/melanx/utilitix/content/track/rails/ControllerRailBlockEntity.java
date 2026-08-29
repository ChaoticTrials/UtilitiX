package de.melanx.utilitix.content.track.rails;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.moddingx.libx.base.tile.BlockEntityBase;

import javax.annotation.Nonnull;

public class ControllerRailBlockEntity extends BlockEntityBase {

    private ItemStack filterStack = ItemStack.EMPTY;

    public ControllerRailBlockEntity(BlockEntityType<?> blockEntityTypeIn, BlockPos pos, BlockState state) {
        super(blockEntityTypeIn, pos, state);
    }

    @Override
    protected void loadAdditional(@Nonnull ValueInput input) {
        super.loadAdditional(input);
        this.filterStack = input.read("FilterStack", ItemStack.OPTIONAL_CODEC).orElse(ItemStack.EMPTY);
    }

    @Override
    protected void saveAdditional(@Nonnull ValueOutput output) {
        super.saveAdditional(output);
        output.store("FilterStack", ItemStack.OPTIONAL_CODEC, this.filterStack);
    }

    public ItemStack getFilterStack() {
        return this.filterStack;
    }

    public void setFilterStack(ItemStack filterStack) {
        this.filterStack = filterStack.copy();
        this.setChanged();
    }

    @Override
    public void preRemoveSideEffects(@Nonnull BlockPos pos, @Nonnull BlockState state) {
        super.preRemoveSideEffects(pos, state);

        if (!this.filterStack.isEmpty() && this.level != null) {
            ItemEntity entity = new ItemEntity(this.level, pos.getX() + 0.5D, pos.getY() + 0.1D, pos.getZ() + 0.5D, this.filterStack.copy());
            this.level.addFreshEntity(entity);
        }
    }
}
