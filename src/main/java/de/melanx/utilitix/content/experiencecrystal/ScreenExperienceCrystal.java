package de.melanx.utilitix.content.experiencecrystal;

import com.mojang.blaze3d.systems.RenderSystem;
import de.melanx.utilitix.UtilitiX;
import de.melanx.utilitix.network.ClickScreenButton;
import de.melanx.utilitix.util.XPUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Inventory;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.common.MinecraftForge;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.awt.Color;

public class ScreenExperienceCrystal extends AbstractContainerScreen<ContainerMenuExperienceCrystal> {

    private static final ResourceLocation GUI = new ResourceLocation(UtilitiX.getInstance().modid, "textures/container/experience_crystal.png");
    public int relX;
    public int relY;

    public ScreenExperienceCrystal(ContainerMenuExperienceCrystal menu, Inventory inv, Component title) {
        super(menu, inv, title);
        this.imageHeight = 176;
        MinecraftForge.EVENT_BUS.addListener(this::onGuiInit);
    }

    private void onGuiInit(ScreenEvent.Init event) {
        this.relX = (event.getScreen().width - this.imageWidth) / 2;
        this.relY = (event.getScreen().height - this.imageHeight) / 2;
    }

    @Override
    public void render(@Nonnull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(guiGraphics);
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, GUI);
        guiGraphics.blit(GUI, this.relX, this.relY, 0, 0, this.imageWidth, this.imageHeight);

        Button hoveredButton = this.getHoveredButton(mouseX, mouseY);
        for (Button button : Button.values()) {
            this.renderButton(guiGraphics, button, hoveredButton == button);
        }

        super.render(guiGraphics, mouseX, mouseY, partialTick);
        this.renderTooltip(guiGraphics, mouseX, mouseY);

        for (Button button : Button.values()) {
            if (hoveredButton == button) {
                guiGraphics.renderTooltip(this.font, button.component, mouseX, mouseY);
            }
        }
    }

    @Override
    protected void renderLabels(@Nonnull GuiGraphics guiGraphics, int mouseX, int mouseY) {
        guiGraphics.drawString(this.font, this.title, this.titleLabelX, this.titleLabelY, Color.DARK_GRAY.getRGB(), false);
        guiGraphics.drawString(this.font, this.playerInventoryTitle, this.inventoryLabelX, this.inventoryLabelY + 10, Color.DARK_GRAY.getRGB(), false);
    }

    @Override
    protected void renderBg(@Nonnull GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        guiGraphics.blit(GUI, this.relX + (this.imageWidth / 2 - 50), this.relY + 49, 0, this.imageHeight + 40, 100, 7);
        Pair<Integer, Float> xp = XPUtils.getLevelExp(this.menu.getBlockEntity().getXp());
        guiGraphics.blit(GUI, this.relX + (this.imageWidth / 2 - 49), this.relY + 50, 0, this.imageHeight + 47, (int) (xp.getRight() * 98), 5);
        MutableComponent s = Component.literal(String.valueOf(xp.getLeft()));
        int width = this.font.width(s.getString());
        guiGraphics.drawString(this.font, s.getString(), this.relX + ((float) this.imageWidth / 2) - ((float) width / 2), this.relY + 40, Color.DARK_GRAY.getRGB(), false);
    }

    public void renderButton(GuiGraphics guiGraphics, Button button, boolean mouseHovered) {
        int xButton = this.relX + button.x;
        int yButton = this.relY + button.y;
        guiGraphics.blit(GUI, xButton, yButton, button.offset, mouseHovered ? this.imageHeight + 20 : this.imageHeight, 20, 20);
    }

    @Nullable
    private Button getHoveredButton(int x, int y) {
        for (Button button : Button.values()) {
            int xButton = this.relX + button.x;
            int yButton = this.relY + button.y;
            if (x >= xButton && x < xButton + 20 && y >= yButton && y < yButton + 20) {
                return button;
            }
        }

        return null;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0) {
            Button pressed = this.getHoveredButton((int) mouseX, (int) mouseY);
            if (pressed != null) {
                Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1));
                UtilitiX.getNetwork().channel.sendToServer(new ClickScreenButton(this.menu.getPos(), pressed));
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    public enum Button {
        ADD_ONE(41, 18, 0, "add_1"),
        ADD_TEN(78, 18, 20, "add_10"),
        ADD_ALL(115, 18, 40, "add_all"),
        SUB_ONE(41, 58, 60, "sub_1"),
        SUB_TEN(78, 58, 80, "sub_10"),
        SUB_ALL(115, 58, 100, "sub_all");

        private final int x;
        private final int y;
        private final int offset;
        private final MutableComponent component;

        Button(int x, int y, int offset, String translationKey) {
            this.x = x;
            this.y = y;
            this.offset = offset;
            this.component = Component.translatable("tooltip.utilitix.experience_crystal." + translationKey);
        }
    }
}
