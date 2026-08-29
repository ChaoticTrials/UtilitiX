package de.melanx.utilitix.content.crudefurnace;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;

import javax.annotation.Nonnull;

public class CrudeFurnaceScreen extends AbstractContainerScreen<CrudeFurnaceMenu> {

    private static final Identifier LIT_PROGRESS_SPRITE = Identifier.withDefaultNamespace("container/furnace/lit_progress");
    private static final Identifier BURN_PROGRESS_SPRITE = Identifier.withDefaultNamespace("container/furnace/burn_progress");
    private static final Identifier GUI = Identifier.withDefaultNamespace("textures/gui/container/furnace.png");

    public CrudeFurnaceScreen(CrudeFurnaceMenu menu, Inventory inv, Component title) {
        super(menu, inv, title);
    }

    @Override
    protected void init() {
        super.init();
        this.titleLabelX = (this.imageWidth - this.font.width(this.title)) / 2;
    }

    @Override
    public void extractBackground(@Nonnull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float alpha) {
        super.extractBackground(graphics, mouseX, mouseY, alpha);
        graphics.blit(RenderPipelines.GUI_TEXTURED, GUI, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight, 256, 256);

        if (this.menu.getBlockEntity().isBurning()) {
            int i = this.menu.getBlockEntity().getScaledBurnTime();
            graphics.blitSprite(RenderPipelines.GUI_TEXTURED, LIT_PROGRESS_SPRITE, 14, 14, 0, 14 - i, this.leftPos + 56, this.topPos + 36 + 14 - i, 14, i);
        }

        int i = this.menu.getBlockEntity().getCookProgressionScaled();
        graphics.blitSprite(RenderPipelines.GUI_TEXTURED, BURN_PROGRESS_SPRITE, 24, 16, 0, 0, this.leftPos + 79, this.topPos + 34, i, 16);
    }
}
