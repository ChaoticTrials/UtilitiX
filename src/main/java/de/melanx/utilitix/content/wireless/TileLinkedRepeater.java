package de.melanx.utilitix.content.wireless;

import de.melanx.utilitix.registration.ModBlocks;
import de.melanx.utilitix.registration.ModItems;
import io.github.noeppi_noeppi.libx.mod.registration.TileEntityBase;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.world.TickPriority;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.UUID;

public class TileLinkedRepeater extends TileEntityBase {

    private ItemStack link = ItemStack.EMPTY;
    
    public TileLinkedRepeater(TileEntityType<?> tileEntityTypeIn) {
        super(tileEntityTypeIn);
    }

    @Override
    public void read(@Nonnull BlockState state, @Nonnull CompoundNBT nbt) {
        super.read(state, nbt);
        this.link = ItemStack.read(nbt.getCompound("Link"));
    }

    @Nonnull
    @Override
    public CompoundNBT write(@Nonnull CompoundNBT nbt) {
        nbt.put("Link", this.link.serializeNBT());
        return super.write(nbt);
    }

    public ItemStack getLink() {
        return this.link.copy();
    }

    public void setLink(ItemStack link) {
        UUID oldId = this.getLinkId();
        this.link = link.copy();
        UUID newId = this.getLinkId();
        if (oldId != newId && this.world != null && this.pos != null && !this.world.isRemote) {
            WirelessStorage storage = WirelessStorage.get(this.world);
            storage.remove(this.world, oldId, new WorldAndPos(this.world.getDimensionKey(), this.pos));
            if (newId != null) {
                storage.update(this.world, newId, new WorldAndPos(this.world.getDimensionKey(), this.pos), BlockLinkedRepeater.inputStrength(this.world, this.getBlockState(), this.pos));
            }
            BlockState state = this.getBlockState().with(BlockStateProperties.EYE, newId != null);
            this.world.setBlockState(this.pos, state, 3);
            this.updateContainingBlockInfo();
            this.world.getPendingBlockTicks().scheduleTick(this.pos, ModBlocks.linkedRepeater, 1, TickPriority.EXTREMELY_HIGH);
        }
        this.markDirty();
    }
    
    @Nullable
    public UUID getLinkId() {
        if (!this.link.isEmpty() && this.link.getItem() == ModItems.linkedCrystal) {
            return ItemLinkedCrystal.getId(this.link);
        } else {
            return null;
        }
    }
}
