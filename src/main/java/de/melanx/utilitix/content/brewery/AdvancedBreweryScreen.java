package de.melanx.utilitix.content.brewery;

import de.melanx.utilitix.UtilitiX;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;

import javax.annotation.Nonnull;
import java.awt.Color;

public class AdvancedBreweryScreen extends AbstractContainerScreen<AdvancedBreweryMenu> {

    public static final Identifier TEXTURE = Identifier.fromNamespaceAndPath(UtilitiX.getInstance().modid, "textures/container/advanced_brewery.png");
    private static final int[] BUBBLE_SIZES = new int[]{29, 24, 20, 16, 11, 6, 0};

    public AdvancedBreweryScreen(AdvancedBreweryMenu menu, Inventory inv, Component title) {
        super(menu, inv, title);
    }

    @Override
    public void extractBackground(@Nonnull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        super.extractBackground(graphics, mouseX, mouseY, partialTick);
        graphics.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, this.leftPos, this.topPos, 0.0F, 0.0F, this.imageWidth, this.imageHeight, 256, 256);

        AdvancedBreweryBlockEntity blockEntity = this.menu.getBlockEntity();
        int fuelWidth = Mth.clamp(Math.round(((18 * blockEntity.getFuel()) + 19) / 20f), 0, 18);
        if (blockEntity.getFuel() > 0 && fuelWidth > 0) {
            graphics.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, this.leftPos + 60, this.topPos + 44, 176.0F, 29.0F, fuelWidth, 4, 256, 256);
        }

        int brewTime = Mth.clamp(blockEntity.getBrewTime(), 0, AdvancedBreweryBlockEntity.MAX_BREW_TIME);
        if (blockEntity.getFuel() <= 0 || brewTime <= 0) {
            return;
        }

        int textureHeight = Mth.clamp(Math.round(28f * (brewTime / (float) AdvancedBreweryBlockEntity.MAX_BREW_TIME)), 0, 28);
        if (textureHeight > 0) {
            graphics.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, this.leftPos + 97, this.topPos + 16, 176.0F, 0.0F, 9, textureHeight, 256, 256);
        }

        textureHeight = BUBBLE_SIZES[((AdvancedBreweryBlockEntity.MAX_BREW_TIME - brewTime) / 2) % BUBBLE_SIZES.length];
        if (textureHeight > 0) {
            graphics.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, this.leftPos + 63, this.topPos + 14 + 29 - textureHeight, 185.0F, 29.0F - textureHeight, 12, textureHeight, 256, 256);
        }
    }

    @Override
    protected void extractLabels(@Nonnull GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        String s = this.title.getString();
        graphics.text(this.font, s, (this.imageWidth / 2) - (this.font.width(s) / 2), 5, Color.DARK_GRAY.getRGB(), false);
        graphics.text(this.font, this.playerInventoryTitle, 8, this.imageHeight - 94, Color.DARK_GRAY.getRGB(), false);
    }
}
