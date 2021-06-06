package de.melanx.utilitix.content.experiencecrystal;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import de.melanx.utilitix.UtilitiX;
import de.melanx.utilitix.network.ClickScreenButtonHandler;
import de.melanx.utilitix.util.XPUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SimpleSound;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.awt.*;

public class ScreenExperienceCrystal extends ContainerScreen<ContainerExperienceCrystal> {

    private static final ResourceLocation GUI = new ResourceLocation(UtilitiX.getInstance().modid, "textures/container/experience_crystal.png");
    public int relX;
    public int relY;

    public ScreenExperienceCrystal(ContainerExperienceCrystal container, PlayerInventory inv, ITextComponent title) {
        super(container, inv, title);
        this.ySize += 10;
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
        //noinspection deprecation
        GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        //noinspection ConstantConditions
        this.minecraft.getTextureManager().bindTexture(GUI);
        this.blit(ms, this.relX, this.relY, 0, 0, this.xSize, this.ySize);

        Button hoveredButton = this.getHoveredButton(x, y);
        for (Button button : Button.values()) {
            this.renderButton(ms, button, hoveredButton == button);
        }

        super.render(ms, x, y, partialTicks);
        this.renderHoveredTooltip(ms, x, y);

        for (Button button : Button.values()) {
            if (hoveredButton == button) {
                this.renderTooltip(ms, button.component, x, y);
            }
        }
    }

    @Override
    protected void drawGuiContainerForegroundLayer(@Nonnull MatrixStack ms, int x, int y) {
        this.font.drawText(ms, this.title, this.titleX, this.titleY, Color.DARK_GRAY.getRGB());
        this.font.drawText(ms, this.playerInventory.getDisplayName(), this.playerInventoryTitleX, this.playerInventoryTitleY + 10, Color.DARK_GRAY.getRGB());
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(@Nonnull MatrixStack ms, float partialTicks, int x, int y) {
        //noinspection ConstantConditions
        this.minecraft.getTextureManager().bindTexture(GUI);
        this.blit(ms, this.relX + (this.xSize / 2 - 50), this.relY + 49, 0, this.ySize + 40, 100, 7);
        Pair<Integer, Float> xp = XPUtils.getLevelExp(this.container.tile.getXp());
        this.blit(ms, this.relX + (this.xSize / 2 - 49), this.relY + 50, 0, this.ySize + 47, (int) (xp.getRight() * 98), 5);
        StringTextComponent s = new StringTextComponent(String.valueOf(xp.getLeft()));
        int width = this.font.getStringWidth(s.getText());
        this.font.drawText(ms, s, this.relX + ((float) this.xSize / 2) - ((float) width / 2), this.relY + 40, Color.DARK_GRAY.getRGB());
    }

    public void renderButton(MatrixStack ms, Button button, boolean mouseHovered) {
        int xButton = this.relX + button.x;
        int yButton = this.relY + button.y;
        this.blit(ms, xButton, yButton, button.offset, mouseHovered ? this.ySize + 20 : this.ySize, 20, 20);
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
    public boolean mouseClicked(double x, double y, int button) {
        if (button == 0) {
            Button pressed = this.getHoveredButton((int) x, (int) y);
            if (pressed != null) {
                Minecraft.getInstance().getSoundHandler().play(SimpleSound.master(SoundEvents.UI_BUTTON_CLICK, 1));
                UtilitiX.getNetwork().instance.sendToServer(new ClickScreenButtonHandler.Message(this.container.pos, pressed));
            }
        }
        return super.mouseClicked(x, y, button);
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
        private final IFormattableTextComponent component;

        Button(int x, int y, int offset, String translationKey) {
            this.x = x;
            this.y = y;
            this.offset = offset;
            this.component = new TranslationTextComponent("tooltip.utilitix.experience_crystal." + translationKey);
        }
    }
}
