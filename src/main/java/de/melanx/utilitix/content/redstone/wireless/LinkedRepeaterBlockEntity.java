package de.melanx.utilitix.content.redstone.wireless;

import de.melanx.utilitix.registration.ModBlocks;
import de.melanx.utilitix.registration.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.ticks.TickPriority;
import org.moddingx.libx.base.tile.BlockEntityBase;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.UUID;

public class LinkedRepeaterBlockEntity extends BlockEntityBase {

    private ItemStack link = ItemStack.EMPTY;

    public LinkedRepeaterBlockEntity(BlockEntityType<?> blockEntityTypeIn, BlockPos pos, BlockState state) {
        super(blockEntityTypeIn, pos, state);
    }

    @Override
    protected void loadAdditional(@Nonnull ValueInput input) {
        super.loadAdditional(input);
        this.link = input.read("Link", ItemStack.OPTIONAL_CODEC).orElse(ItemStack.EMPTY).copy();
    }

    @Override
    protected void saveAdditional(@Nonnull ValueOutput output) {
        super.saveAdditional(output);
        output.store("Link", ItemStack.OPTIONAL_CODEC, this.link);
    }

    @Override
    public void preRemoveSideEffects(@Nonnull BlockPos pos, @Nonnull BlockState state) {
        super.preRemoveSideEffects(pos, state);
        if (this.level != null) {
            WirelessRedstoneSavedData.get(this.level).remove(this.level, this.getLinkId(), GlobalPos.of(this.level.dimension(), pos));

            if (!this.link.isEmpty()) {
                ItemEntity entity = new ItemEntity(this.level, pos.getX() + 0.5D, pos.getY() + 0.1D, pos.getZ() + 0.5D, this.link.copy());
                this.level.addFreshEntity(entity);
            }
        }
    }

    public ItemStack getLink() {
        return this.link.copy();
    }

    public void setLink(ItemStack link) {
        UUID oldId = this.getLinkId();
        this.link = link.copy();
        UUID newId = this.getLinkId();

        if (oldId != newId && this.level != null && !this.level.isClientSide()) {
            WirelessRedstoneSavedData storage = WirelessRedstoneSavedData.get(this.level);
            storage.remove(this.level, oldId, GlobalPos.of(this.level.dimension(), this.worldPosition));

            if (newId != null) {
                storage.update(this.level, newId, GlobalPos.of(this.level.dimension(), this.worldPosition), LinkedRepeaterBlock.inputStrength(this.level, this.getBlockState(), this.worldPosition));
            }

            BlockState state = this.getBlockState().setValue(BlockStateProperties.EYE, newId != null);
            this.level.setBlock(this.worldPosition, state, Block.UPDATE_ALL);
            this.level.scheduleTick(this.worldPosition, ModBlocks.linkedRepeater, 1, TickPriority.EXTREMELY_HIGH);
        }

        this.setChanged();
    }

    @Nullable
    public UUID getLinkId() {
        if (!this.link.isEmpty() && this.link.getItem() == ModItems.linkedCrystal) {
            return LinkedCrystalItem.getId(this.link);
        }

        return null;
    }
}
