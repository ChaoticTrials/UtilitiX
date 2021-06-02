package de.melanx.utilitix.content.brewery;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import de.melanx.utilitix.UtilitiX;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;

import javax.annotation.Nonnull;
import java.awt.*;

public class ScreenAdvancedBrewery extends ContainerScreen<ContainerAdvancedBrewery> {

    public static final ResourceLocation TEXTURE = new ResourceLocation(UtilitiX.getInstance().modid, "textures/container/advanced_brewery.png");
    private static final int[] BUBBLE_SIZES = new int[]{ 29, 24, 20, 16, 11, 6, 0 };
    
    private int relX;
    private int relY;
    
    public ScreenAdvancedBrewery(ContainerAdvancedBrewery container, PlayerInventory inv, ITextComponent title) {
        super(container, inv, title);
    }

    @Override
    public void init(@Nonnull Minecraft mc, int x, int y) {
        super.init(mc, x, y);
        this.relX = (x - this.xSize) / 2;
        this.relY = (y - this.ySize) / 2;
    }

    @Override
    public void render(@Nonnull MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(matrixStack);
        super.render(matrixStack, mouseX, mouseY, partialTicks);
        this.renderHoveredTooltip(matrixStack, mouseX, mouseY);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(@Nonnull MatrixStack matrixStack, float partialTicks, int mouseX, int mouseY) {
        //noinspection deprecation
        RenderSystem.color4f(1, 1, 1, 1);
        //noinspection ConstantConditions
        this.minecraft.getTextureManager().bindTexture(TEXTURE);
        this.blit(matrixStack, this.relX, this.relY, 0, 0, this.xSize, this.ySize);
        TileAdvancedBrewery tile = this.container.tile;
        int fuelWidth = MathHelper.clamp(Math.round(((18 * tile.getFuel()) + 19) / 20f), 0, 18);
        if (tile.getFuel() > 0 && fuelWidth > 0) {
            this.blit(matrixStack, this.relX + 60, this.relY + 44, 176, 29, fuelWidth, 4);
        }
        int brewTime = MathHelper.clamp(tile.getBrewTime(), 0, TileAdvancedBrewery.MAX_BREW_TIME);
        if (tile.getFuel() > 0 && brewTime > 0) {
            int textureHeight = MathHelper.clamp(Math.round(28f * (brewTime / (float) TileAdvancedBrewery.MAX_BREW_TIME)), 0, 28);
            if (textureHeight > 0) {
                this.blit(matrixStack, this.relX + 97, this.relY + 16, 176, 0, 9, textureHeight);
            }
            textureHeight = BUBBLE_SIZES[((TileAdvancedBrewery.MAX_BREW_TIME - brewTime) / 2) % BUBBLE_SIZES.length];
            if (textureHeight > 0) {
                this.blit(matrixStack,this.relX + 63, this.relY + 14 + 29 - textureHeight, 185, 29 - textureHeight, 12, textureHeight);
            }
        }
    }

    @Override
    protected void drawGuiContainerForegroundLayer(@Nonnull MatrixStack ms, int mouseX, int mouseY) {
        String s = this.title.getString();
        //noinspection IntegerDivisionInFloatingPointContext
        this.font.drawString(ms, s, (this.xSize / 2) - (this.font.getStringWidth(s) / 2), 5, Color.DARK_GRAY.getRGB());
        this.font.drawString(ms, this.playerInventory.getDisplayName().getString(), 8, this.ySize - 94, Color.DARK_GRAY.getRGB());
    }
}
