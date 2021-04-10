package de.melanx.utilitix.item;

import de.melanx.utilitix.UtilitiX;
import io.github.noeppi_noeppi.libx.inventory.VanillaWrapper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.ChestContainer;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class QuiverContainerProvider implements INamedContainerProvider {

    private final ItemStackHandler handler;
    private final Runnable dirty;

    public QuiverContainerProvider(ItemStackHandler handler, Runnable dirty) {
        this.handler = handler;
        this.dirty = dirty;
    }

    @Nonnull
    @Override
    public ITextComponent getDisplayName() {
        return new TranslationTextComponent("screen." + UtilitiX.getInstance().modid + ".quiver");
    }

    @Nullable
    @Override
    public Container createMenu(int id, @Nonnull PlayerInventory playerInventory, @Nonnull PlayerEntity player) {
        return ChestContainer.createGeneric9X3(id, playerInventory, new VanillaWrapper(this.handler, this.dirty));
    }
}
