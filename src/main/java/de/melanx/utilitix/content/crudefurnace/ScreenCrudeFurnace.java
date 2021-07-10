package de.melanx.utilitix.content.crudefurnace;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

import javax.annotation.Nonnull;

public class ScreenCrudeFurnace extends ContainerScreen<ContainerCrudeFurnace> {

    private static final ResourceLocation GUI = new ResourceLocation("minecraft", "textures/gui/container/furnace.png");

    public int relX;
    public int relY;

    public ScreenCrudeFurnace(ContainerCrudeFurnace container, PlayerInventory inv, ITextComponent title) {
        super(container, inv, title);

    }

    @Override
    public void init(@Nonnull Minecraft mc, int x, int y) {
        super.init(mc, x, y);
        this.relX = (x - this.xSize) / 2;
        this.relY = (y - this.ySize) / 2;
    }

    @Override
    public void render(@Nonnull MatrixStack ms, int x, int y, float partialTicks) {
        this.renderBackground(ms);
        super.render(ms, x, y, partialTicks);
        this.renderHoveredTooltip(ms, x, y);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(@Nonnull MatrixStack ms, float partialTicks, int x, int y) {
        //noinspection deprecation
        GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        //noinspection ConstantConditions
        this.minecraft.getTextureManager().bindTexture(GUI);
        this.blit(ms, this.relX, this.relY, 0, 0, this.xSize, this.ySize);

        if (this.container.tile.isBurning()) {
            int i = this.container.tile.getScaledBurnTime();
            this.blit(ms, this.relX + 56, this.relY + 48 - i, 176, 12 - i, 14, i + 1);
        }

        int i = this.container.tile.getCookProgressionScaled();
        this.blit(ms, this.relX + 79, this.relY + 34, 176, 14, i + 1, 16);
    }
}
