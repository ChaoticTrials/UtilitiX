package de.melanx.utilitix.content.track.carts.piston;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import de.melanx.utilitix.UtilitiX;
import de.melanx.utilitix.network.PistonCartModeCycleSerializer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SimpleSound;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.inventory.container.Slot;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;

import javax.annotation.Nonnull;
import java.awt.*;

public class PistonCartScreen extends ContainerScreen<PistonCartContainer> {

    public static final ResourceLocation TEXTURE = new ResourceLocation(UtilitiX.getInstance().modid, "textures/container/piston_cart.png");

    private int relX;
    private int relY;

    public PistonCartScreen(PistonCartContainer container, PlayerInventory inv, ITextComponent title) {
        super(container, inv, title);
        this.xSize = 176;
        this.ySize = 186;
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
        if (mouseX >= this.relX + 65 && mouseX <= this.relX + 111 && mouseY >= this.relY + 18 && mouseY <= this.relY + 34) {
            this.blit(matrixStack, this.relX + 64, this.relY + 17, 176, 0, 48, 18);
        }
    }

    @Override
    protected void drawGuiContainerForegroundLayer(@Nonnull MatrixStack matrixStack, int mouseX, int mouseY) {
        String s = this.title.getString();
        //noinspection IntegerDivisionInFloatingPointContext
        this.font.drawString(matrixStack, s, (this.xSize / 2) - (this.font.getStringWidth(s) / 2), 5, Color.DARK_GRAY.getRGB());
        this.font.drawString(matrixStack, this.playerInventory.getDisplayName().getString(), 8, this.ySize - 94, Color.DARK_GRAY.getRGB());
        if (this.container.entity != null) {
            //noinspection ConstantConditions
            int modeStrWidth = this.minecraft.fontRenderer.getStringPropertyWidth(this.container.entity.getMode().name);
            //noinspection IntegerDivisionInFloatingPointContext
            this.minecraft.fontRenderer.drawTextWithShadow(matrixStack, this.container.entity.getMode().name, 88 - (modeStrWidth / 2), 22, 0xFFFFFF);
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int clickedButton) {
        if (clickedButton == 0) {
            if (mouseX >= this.relX + 65 && mouseX <= this.relX + 111 && mouseY >= this.relY + 18 && mouseY <= this.relY + 34 && this.container.entity != null) {
                UtilitiX.getNetwork().instance.sendToServer(new PistonCartModeCycleSerializer.PistonCartModeCycleMessage(this.container.entity.getEntityId()));
                Minecraft.getInstance().getSoundHandler().play(SimpleSound.master(SoundEvents.UI_BUTTON_CLICK, 1));
                return true;
            }
        }
        return super.mouseClicked(mouseX, mouseY, clickedButton);
    }
}
