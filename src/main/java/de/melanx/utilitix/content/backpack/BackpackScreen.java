package de.melanx.utilitix.content.backpack;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import org.moddingx.libx.render.RenderHelper;

import javax.annotation.Nonnull;

public class BackpackScreen extends AbstractContainerScreen<BackpackMenu> {

    private final BackpackMenu menu;

    public BackpackScreen(BackpackMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.menu = menu;
        this.imageWidth = menu.width;
        this.imageHeight = menu.height;
        this.inventoryLabelX = menu.invX;
        this.inventoryLabelY = menu.invY - 11;
    }

    @Override
    protected void renderBg(@Nonnull GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        RenderHelper.renderGuiBackground(graphics, this.leftPos, this.topPos, this.imageWidth, this.imageHeight);
        int i = (this.width - this.imageWidth) / 2;
        int j = (this.height - this.imageHeight) / 2;
        for (Slot slot : this.menu.slots) {
            graphics.blit(RenderHelper.TEXTURE_CHEST_GUI, i + slot.x - 1, j + slot.y - 1, 25, 35, 18, 18);
        }
    }

    @Override
    public void render(@Nonnull GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        super.render(graphics, mouseX, mouseY, partialTick);

        this.renderTooltip(graphics, mouseX, mouseY);
    }
}
