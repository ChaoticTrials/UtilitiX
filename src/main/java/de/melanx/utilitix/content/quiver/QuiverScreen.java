package de.melanx.utilitix.content.quiver;

import de.melanx.utilitix.UtilitiX;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;

import javax.annotation.Nonnull;

public class QuiverScreen extends AbstractContainerScreen<QuiverMenu> {

    public static final Identifier TEXTURE = UtilitiX.getInstance().id("textures/container/quiver.png");

    public QuiverScreen(QuiverMenu menu, Inventory inv, Component title) {
        super(menu, inv, title, 175, 131);
    }

    @Override
    public void extractBackground(@Nonnull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float alpha) {
        super.extractBackground(graphics, mouseX, mouseY, alpha);
        graphics.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight, 256, 256);
    }

    @Override
    protected void extractLabels(@Nonnull GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        graphics.text(this.font, this.title, this.titleLabelX, this.titleLabelY, 0xFF404040, false);
        graphics.text(this.font, this.playerInventoryTitle, 8, this.imageHeight - 93, 0xFF404040, false);
    }
}
