package de.melanx.utilitix.content.track.tinkerer;

import de.melanx.utilitix.UtilitiX;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;

import javax.annotation.Nonnull;

public class MinecartTinkererScreen extends AbstractContainerScreen<MinecartTinkererMenu> {

    public static final Identifier TEXTURE = UtilitiX.getInstance().id("textures/container/minecart_tinkerer.png");

    public MinecartTinkererScreen(MinecartTinkererMenu menu, Inventory inv, Component title) {
        super(menu, inv, title, 175, 131);
    }

    @Override
    public void extractBackground(@Nonnull GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.extractBackground(guiGraphics, mouseX, mouseY, partialTick);
        guiGraphics.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, this.leftPos, this.topPos, 0.0F, 0.0F, this.imageWidth, this.imageHeight, 256, 256);
    }

    @Override
    protected void extractLabels(@Nonnull GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY) {
        guiGraphics.text(this.font, this.title, 8, 6, 0x404040, false);
        guiGraphics.text(this.font, this.playerInventoryTitle, 8, this.imageHeight - 94, 0x404040, false);
    }
}
