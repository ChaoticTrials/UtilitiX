package de.melanx.utilitix.content.backpack;

import de.melanx.utilitix.registration.ModItems;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.MutableDataComponentHolder;
import org.moddingx.libx.inventory.StackItemHandler;

import javax.annotation.Nonnull;
import java.lang.reflect.Field;

public class VariableSizeStackItemHandler extends StackItemHandler {

    protected int size;
    protected final int maxSize;

    public VariableSizeStackItemHandler(int baseSize, int maxSize, MutableDataComponentHolder dataComponentHolder) {
        super(baseSize, dataComponentHolder);
        this.size = baseSize;
        this.maxSize = maxSize;
    }

    public void setSlots(int size) {
        try {
            Field field = StackItemHandler.class.getDeclaredField("size");
            field.setAccessible(true);
            field.set(this, Math.min(size, this.maxSize));
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
        return !stack.is(ModItems.backpack);
    }
}
