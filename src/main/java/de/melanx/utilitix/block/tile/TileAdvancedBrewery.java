package de.melanx.utilitix.block.tile;

import com.google.common.collect.ImmutableSet;
import de.melanx.utilitix.registration.ModItems;
import io.github.noeppi_noeppi.libx.inventory.BaseItemStackHandler;
import io.github.noeppi_noeppi.libx.inventory.ItemStackHandlerWrapper;
import io.github.noeppi_noeppi.libx.mod.registration.TileEntityBase;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.tags.ItemTags;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Set;

//Ingredient-Slot: 0
//Potion-Ingredient-Slot R: 1
//Potion-Ingredient-Slot L: 2
//Output-Slot: 3
//Blaze-Slot: 4
public class TileAdvancedBrewery extends TileEntityBase {

    public static final Set<Item> POTION_ITEMS = ImmutableSet.of(
            Items.POTION, Items.SPLASH_POTION, Items.LINGERING_POTION
    );
    
    private final IItemHandlerModifiable inventory;
    private final LazyOptional<IItemHandler> inventoryTop;
    private final LazyOptional<IItemHandler> inventorySide;
    private final LazyOptional<IItemHandler> inventoryBottom;
    
    public TileAdvancedBrewery(TileEntityType<?> tileEntityTypeIn) {
        super(tileEntityTypeIn);
        BaseItemStackHandler inventory = new BaseItemStackHandler(5, () -> {
            this.markDirty();
            this.markDispatchable();
        }, this::isItemValid);
        inventory.addSlotLimit(1, 1);
        inventory.addSlotLimit(2, 1);
        inventory.addSlotLimit(3, 1);
        this.inventory = inventory;
        this.inventoryTop = ItemStackHandlerWrapper.createLazy(this::getInventory, slot -> false, (slot, stack) -> slot == 0 || slot == 3).cast();
        this.inventorySide = ItemStackHandlerWrapper.createLazy(this::getInventory, slot -> false, (slot, stack) -> slot == 1 || slot == 2 || slot == 4).cast();
        this.inventoryBottom = ItemStackHandlerWrapper.createLazy(this::getInventory, slot -> slot == 0 || slot == 1 || slot == 2, (slot, stack) -> false).cast();
    }
    
    private boolean isItemValid(int slot, ItemStack stack) {
        if (slot >= 0 && slot <= 2) Tags.Items.
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            if (side == null) {
                return LazyOptional.of(this::getInventory).cast();
            }
            switch (side) {
                case DOWN: return this.inventoryBottom.cast();
                case UP: return this.inventoryTop.cast();
                default: return this.inventorySide.cast();
            }
        } else {
            return super.getCapability(cap, side);
        }
    }

    @Nonnull
    public IItemHandlerModifiable getInventory() {
        return this.inventory;
    }
    
    
}
