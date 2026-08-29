package de.melanx.utilitix.content.backpack;

import de.melanx.utilitix.registration.ModItems;
import net.neoforged.neoforge.transfer.access.ItemAccess;
import net.neoforged.neoforge.transfer.item.ItemResource;
import org.moddingx.libx.inventory.StackItemResourceHandler;

import javax.annotation.Nonnull;

public class VariableSizeStackItemHandler extends StackItemResourceHandler {

    public VariableSizeStackItemHandler(int size, ItemAccess itemAccess) {
        super(size, itemAccess);
    }

    @Override
    public boolean isValid(int slot, @Nonnull ItemResource resource) {
        return !resource.is(ModItems.backpack);
    }
}
