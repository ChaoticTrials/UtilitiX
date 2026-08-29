package de.melanx.utilitix.content.backpack;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import org.moddingx.libx.render.RenderHelper;

import javax.annotation.Nonnull;

public class BackpackScreen extends AbstractContainerScreen<BackpackMenu> {

    private final BackpackMenu menu;

    public BackpackScreen(BackpackMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title, menu.width, menu.height);
        this.menu = menu;
        this.inventoryLabelX = menu.invX;
        this.inventoryLabelY = menu.invY - 11;
    }

    @Override
    public void extractBackground(@Nonnull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        super.extractBackground(graphics, mouseX, mouseY, partialTick);
        RenderHelper.renderGuiBackground(RenderPipelines.GUI_TEXTURED, graphics, this.leftPos, this.topPos, this.imageWidth, this.imageHeight);
        int i = (this.width - this.imageWidth) / 2;
        int j = (this.height - this.imageHeight) / 2;
        for (Slot slot : this.menu.slots) {
            graphics.blit(RenderPipelines.GUI_TEXTURED, RenderHelper.TEXTURE_CHEST_GUI, i + slot.x - 1, j + slot.y - 1, 25.0F, 35.0F, 18, 18, 256, 256);
        }
    }
}
