package de.melanx.utilitix.content.experiencecrystal;

import de.melanx.utilitix.UtilitiX;
import de.melanx.utilitix.network.handler.ClickScreenButton;
import de.melanx.utilitix.util.XPUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.awt.Color;

public class ExperienceCrystalScreen extends AbstractContainerScreen<ExperienceCrystalMenu> {

    private static final Identifier GUI = Identifier.fromNamespaceAndPath(UtilitiX.getInstance().modid, "textures/container/experience_crystal.png");

    public ExperienceCrystalScreen(ExperienceCrystalMenu menu, Inventory inv, Component title) {
        super(menu, inv, title, 176, 176);
    }

    @Override
    public void extractRenderState(@Nonnull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float alpha) {
        super.extractRenderState(graphics, mouseX, mouseY, alpha);

        Button hoveredButton = this.getHoveredButton(mouseX, mouseY);
        for (Button button : Button.values()) {
            this.renderButton(graphics, button, hoveredButton == button);
        }

        this.extractTooltip(graphics, mouseX, mouseY);

        if (hoveredButton != null) {
            graphics.setTooltipForNextFrame(this.font, hoveredButton.component, mouseX, mouseY);
        }
    }

    @Override
    protected void extractLabels(@Nonnull GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        graphics.text(this.font, this.title, this.titleLabelX, this.titleLabelY, Color.DARK_GRAY.getRGB(), false);
        graphics.text(this.font, this.playerInventoryTitle, this.inventoryLabelX, this.inventoryLabelY, Color.DARK_GRAY.getRGB(), false);
    }

    @Override
    public void extractBackground(@Nonnull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float alpha) {
        super.extractBackground(graphics, mouseX, mouseY, alpha);
        graphics.blit(RenderPipelines.GUI_TEXTURED, GUI, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight, 256, 256);
        graphics.blit(RenderPipelines.GUI_TEXTURED, GUI, this.leftPos + (this.imageWidth / 2 - 50), this.topPos + 49, 0, this.imageHeight + 40, 100, 7, 256, 256);
        Pair<Integer, Float> xp = XPUtils.getLevelExp(this.menu.getBlockEntity().getXp());
        graphics.blit(RenderPipelines.GUI_TEXTURED, GUI, this.leftPos + (this.imageWidth / 2 - 49), this.topPos + 50, 0, this.imageHeight + 47, (int) (xp.getRight() * 98), 5, 256, 256);
        MutableComponent s = Component.literal(String.valueOf(xp.getLeft()));
        int width = this.font.width(s.getString());
        graphics.text(this.font, s.getString(), (int) (this.leftPos + ((float) this.imageWidth / 2) - ((float) width / 2)), this.topPos + 40, Color.DARK_GRAY.getRGB(), false);
    }

    public void renderButton(GuiGraphicsExtractor guiGraphics, Button button, boolean mouseHovered) {
        int xButton = this.leftPos + button.x;
        int yButton = this.topPos + button.y;
        guiGraphics.blit(RenderPipelines.GUI_TEXTURED, GUI, xButton, yButton, button.offset, mouseHovered ? this.imageHeight + 20 : this.imageHeight, 20, 20, 256, 256);
    }

    @Nullable
    private Button getHoveredButton(int x, int y) {
        for (Button button : Button.values()) {
            int xButton = this.leftPos + button.x;
            int yButton = this.topPos + button.y;
            if (x >= xButton && x < xButton + 20 && y >= yButton && y < yButton + 20) {
                return button;
            }
        }

        return null;
    }

    @Override
    public boolean mouseClicked(@Nonnull MouseButtonEvent event, boolean doubleClick) {
        if (event.button() == 0) {
            Button pressed = this.getHoveredButton((int) event.x(), (int) event.y());
            if (pressed != null) {
                Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1));
                ClientPacketDistributor.sendToServer(new ClickScreenButton.Message(this.menu.getPos(), pressed));
            }
        }

        return super.mouseClicked(event, doubleClick);
    }

    public enum Button {
        ADD_ONE(41, 18, 0, "add_1"),
        ADD_TEN(78, 18, 20, "add_10"),
        ADD_ALL(115, 18, 40, "add_all"),
        SUB_ONE(41, 58, 60, "sub_1"),
        SUB_TEN(78, 58, 80, "sub_10"),
        SUB_ALL(115, 58, 100, "sub_all"),
        REPAIR_ONE(11, 38, 120, "repair_hand"),
        REPAIR_ALL(146, 38, 140, "repair_all");

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
