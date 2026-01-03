package de.melanx.utilitix.content.quiver;

import de.melanx.utilitix.registration.ModDataComponentTypes;
import net.minecraft.core.component.DataComponents;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemContainerContents;

import javax.annotation.Nonnull;

public class QuiverContainer extends SimpleContainer {

    private final ItemStack quiver;

    public QuiverContainer(ItemStack stack) {
        super(QuiverItem.SLOT_SIZE);
        this.quiver = stack;

        ItemContainerContents contents = stack.getOrDefault(DataComponents.CONTAINER, ItemContainerContents.EMPTY);
        contents.copyInto(this.getItems());
    }

    @Override
    public void setChanged() {
        super.setChanged();
        this.quiver.set(DataComponents.CONTAINER, ItemContainerContents.fromItems(this.getItems()));
        this.quiver.set(ModDataComponentTypes.filled, !this.isEmpty());
    }

    @Override
    public boolean canPlaceItem(int slot, @Nonnull ItemStack stack) {
        return stack.is(ItemTags.ARROWS);
    }
}
