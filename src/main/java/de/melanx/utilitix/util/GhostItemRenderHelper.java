package de.melanx.utilitix.util;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.world.item.ItemStack;
import org.moddingx.libx.render.ClientTickHandler;

import java.util.List;

public class GhostItemRenderHelper {

    public static void renderGhostItem(List<ItemStack> stacks, GuiGraphicsExtractor guiGraphics, int x, int y) {
        if (stacks.isEmpty()) {
            return;
        }

        ItemStack stack = stacks.get((((ClientTickHandler.ticksInGame() / 20) % stacks.size()) + stacks.size()) % stacks.size());
        renderGhostItem(stack, guiGraphics, x, y);
    }

    public static void renderGhostItem(ItemStack stack, GuiGraphicsExtractor guiGraphics, int x, int y) {
        if (stack.isEmpty()) {
            return;
        }

        // Vanilla's own recipe-book ghost-slot rendering (see GhostSlots#extractRenderState)
        guiGraphics.fill(x, y, x + 16, y + 16, 0x30FFFFFF);
        guiGraphics.fakeItem(stack, x, y);
        guiGraphics.fill(x, y, x + 16, y + 16, 0x30FFFFFF);
    }
}
