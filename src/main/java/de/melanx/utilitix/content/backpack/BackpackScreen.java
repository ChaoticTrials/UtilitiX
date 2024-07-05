package de.melanx.utilitix.content.backpack;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import org.apache.commons.lang3.tuple.Pair;
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
    public void render(@Nonnull GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(graphics);
        super.render(graphics, mouseX, mouseY, partialTicks);
        this.renderTooltip(graphics, mouseX, mouseY);
    }

    @Override
    protected void renderBg(@Nonnull GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        if (this.minecraft == null) {
            return;
        }

        int x = (this.width - this.imageWidth) / 2;
        int y = (this.height - this.imageHeight) / 2;
        RenderHelper.renderGuiBackground(guiGraphics, x, y, this.imageWidth, this.imageHeight);

        for (Pair<Integer, Integer> slot : this.menu.slotList) {
            guiGraphics.blit(RenderHelper.TEXTURE_CHEST_GUI, x + slot.getLeft() - 1, y + slot.getRight() - 1, 25, 35, 18, 18);
        }

        guiGraphics.blit(RenderHelper.TEXTURE_CHEST_GUI, x + this.menu.invX - 1, y + this.menu.invY - 1, 7, 139, 162, 76);
    }
}
