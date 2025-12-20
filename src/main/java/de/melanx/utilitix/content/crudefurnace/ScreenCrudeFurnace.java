package de.melanx.utilitix.content.crudefurnace;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

import javax.annotation.Nonnull;

public class ScreenCrudeFurnace extends AbstractContainerScreen<ContainerMenuCrudeFurnace> {

    private static final ResourceLocation LIT_PROGRESS_SPRITE = ResourceLocation.withDefaultNamespace("container/furnace/lit_progress");
    private static final ResourceLocation BURN_PROGRESS_SPRITE = ResourceLocation.withDefaultNamespace("container/furnace/burn_progress");
    private static final ResourceLocation GUI = ResourceLocation.withDefaultNamespace("textures/gui/container/furnace.png");

    public ScreenCrudeFurnace(ContainerMenuCrudeFurnace menu, Inventory inv, Component title) {
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
        guiGraphics.blit(GUI, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight);

        if (this.menu.getBlockEntity().isBurning()) {
            int i = this.menu.getBlockEntity().getScaledBurnTime();
            guiGraphics.blitSprite(LIT_PROGRESS_SPRITE, 14, 14, 0, 14 - i, this.leftPos + 56, this.topPos + 36 + 14 - i, 14, i);
        }

        int i = this.menu.getBlockEntity().getCookProgressionScaled();
        guiGraphics.blitSprite(BURN_PROGRESS_SPRITE, 24, 16, 0, 0, this.leftPos + 79, this.topPos + 34, i, 16);
    }
}
