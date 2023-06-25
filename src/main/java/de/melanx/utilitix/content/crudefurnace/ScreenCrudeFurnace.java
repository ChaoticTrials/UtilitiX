package de.melanx.utilitix.content.crudefurnace;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.common.MinecraftForge;

import javax.annotation.Nonnull;

public class ScreenCrudeFurnace extends AbstractContainerScreen<ContainerMenuCrudeFurnace> {

    private static final ResourceLocation GUI = new ResourceLocation("minecraft", "textures/gui/container/furnace.png");

    public int relX;
    public int relY;

    public ScreenCrudeFurnace(ContainerMenuCrudeFurnace menu, Inventory inv, Component title) {
        super(menu, inv, title);
        MinecraftForge.EVENT_BUS.addListener(this::onGuiInit);
    }

    private void onGuiInit(ScreenEvent.Init event) {
        this.relX = (event.getScreen().width - this.imageWidth) / 2;
        this.relY = (event.getScreen().height - this.imageHeight) / 2;
    }

    @Override
    public void render(@Nonnull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(guiGraphics);
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        this.renderTooltip(guiGraphics, mouseX, mouseY);
    }

    @Override
    protected void renderBg(@Nonnull GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, GUI);
        guiGraphics.blit(GUI, this.relX, this.relY, 0, 0, this.imageWidth, this.imageHeight);

        if (this.menu.getBlockEntity().isBurning()) {
            int i = this.menu.getBlockEntity().getScaledBurnTime();
            guiGraphics.blit(GUI, this.relX + 56, this.relY + 48 - i, 176, 12 - i, 14, i + 1);
        }

        int i = this.menu.getBlockEntity().getCookProgressionScaled();
        guiGraphics.blit(GUI, this.relX + 79, this.relY + 34, 176, 14, i + 1, 16);
    }
}
