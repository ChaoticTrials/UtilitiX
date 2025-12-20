package de.melanx.utilitix.content.brewery;

import com.mojang.blaze3d.systems.RenderSystem;
import de.melanx.utilitix.UtilitiX;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;

import javax.annotation.Nonnull;
import java.awt.Color;

public class ScreenAdvancedBrewery extends AbstractContainerScreen<ContainerMenuAdvancedBrewery> {

    public static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(UtilitiX.getInstance().modid, "textures/container/advanced_brewery.png");
    private static final int[] BUBBLE_SIZES = new int[]{29, 24, 20, 16, 11, 6, 0};

    public ScreenAdvancedBrewery(ContainerMenuAdvancedBrewery menu, Inventory inv, Component title) {
        super(menu, inv, title);
    }

    @Override
    public void render(@Nonnull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(guiGraphics, mouseX, mouseY, partialTick);
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        this.renderTooltip(guiGraphics, mouseX, mouseY);
    }

    @Override
    protected void renderBg(@Nonnull GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        guiGraphics.blit(TEXTURE, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight);
        TileAdvancedBrewery tile = this.menu.getBlockEntity();
        int fuelWidth = Mth.clamp(Math.round(((18 * tile.getFuel()) + 19) / 20f), 0, 18);
        if (tile.getFuel() > 0 && fuelWidth > 0) {
            guiGraphics.blit(TEXTURE, this.leftPos + 60, this.topPos + 44, 176, 29, fuelWidth, 4);
        }
        int brewTime = Mth.clamp(tile.getBrewTime(), 0, TileAdvancedBrewery.MAX_BREW_TIME);
        if (tile.getFuel() > 0 && brewTime > 0) {
            int textureHeight = Mth.clamp(Math.round(28f * (brewTime / (float) TileAdvancedBrewery.MAX_BREW_TIME)), 0, 28);
            if (textureHeight > 0) {
                guiGraphics.blit(TEXTURE, this.leftPos + 97, this.topPos + 16, 176, 0, 9, textureHeight);
            }
            textureHeight = BUBBLE_SIZES[((TileAdvancedBrewery.MAX_BREW_TIME - brewTime) / 2) % BUBBLE_SIZES.length];
            if (textureHeight > 0) {
                guiGraphics.blit(TEXTURE, this.leftPos + 63, this.topPos + 14 + 29 - textureHeight, 185, 29 - textureHeight, 12, textureHeight);
            }
        }
    }

    @Override
    protected void renderLabels(@Nonnull GuiGraphics guiGraphics, int mouseX, int mouseY) {
        String s = this.title.getString();
        guiGraphics.drawString(this.font, s, this.leftPos + (this.imageWidth / 2) - (this.font.width(s) / 2), this.topPos + 5, Color.DARK_GRAY.getRGB(), false);
        guiGraphics.drawString(this.font, this.playerInventoryTitle, this.leftPos + 8, this.topPos + this.imageHeight - 94, Color.DARK_GRAY.getRGB(), false);
    }
}
