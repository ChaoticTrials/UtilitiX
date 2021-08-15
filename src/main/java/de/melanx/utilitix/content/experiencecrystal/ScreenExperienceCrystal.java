package de.melanx.utilitix.content.experiencecrystal;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import de.melanx.utilitix.UtilitiX;
import de.melanx.utilitix.network.ClickScreenButtonHandler;
import de.melanx.utilitix.util.XPUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Inventory;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.common.MinecraftForge;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.awt.*;

public class ScreenExperienceCrystal extends AbstractContainerScreen<ContainerMenuExperienceCrystal> {

    private static final ResourceLocation GUI = new ResourceLocation(UtilitiX.getInstance().modid, "textures/container/experience_crystal.png");
    public int relX;
    public int relY;

    public ScreenExperienceCrystal(ContainerMenuExperienceCrystal menu, Inventory inv, Component title) {
        super(menu, inv, title);
        this.imageHeight += 10;
        MinecraftForge.EVENT_BUS.addListener(this::onGuiInit);
    }

    private void onGuiInit(GuiScreenEvent.InitGuiEvent event) {
        this.relX = (event.getGui().width - this.imageWidth) / 2;
        this.relY = (event.getGui().height - this.imageHeight) / 2;
    }

    @Override
    public void render(@Nonnull PoseStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, GUI);
        this.blit(matrixStack, this.relX, this.relY, 0, 0, this.imageWidth, this.imageHeight);

        Button hoveredButton = this.getHoveredButton(mouseX, mouseY);
        for (Button button : Button.values()) {
            this.renderButton(matrixStack, button, hoveredButton == button);
        }

        super.render(matrixStack, mouseX, mouseY, partialTicks);
        this.renderTooltip(matrixStack, mouseX, mouseY);

        for (Button button : Button.values()) {
            if (hoveredButton == button) {
                this.renderTooltip(matrixStack, button.component, mouseX, mouseY);
            }
        }
    }

    @Override
    protected void renderLabels(@Nonnull PoseStack poseStack, int mouseX, int mouseY) {
        this.font.draw(poseStack, this.title, this.titleLabelX, this.titleLabelY, Color.DARK_GRAY.getRGB());
        this.font.draw(poseStack, this.playerInventoryTitle, this.inventoryLabelX, this.inventoryLabelY + 10, Color.DARK_GRAY.getRGB());
    }

    @Override
    protected void renderBg(@Nonnull PoseStack poseStack, float partialTicks, int mouseX, int mouseY) {
//        this.minecraft.getTextureManager().bind(GUI);
        this.blit(poseStack, this.relX + (this.imageWidth / 2 - 50), this.relY + 49, 0, this.imageHeight + 40, 100, 7);
        Pair<Integer, Float> xp = XPUtils.getLevelExp(this.menu.getBlockEntity().getXp());
        this.blit(poseStack, this.relX + (this.imageWidth / 2 - 49), this.relY + 50, 0, this.imageHeight + 47, (int) (xp.getRight() * 98), 5);
        TextComponent s = new TextComponent(String.valueOf(xp.getLeft()));
        int width = this.font.width(s.getText());
        this.font.draw(poseStack, s, this.relX + ((float) this.imageWidth / 2) - ((float) width / 2), this.relY + 40, Color.DARK_GRAY.getRGB());
    }

    public void renderButton(PoseStack ms, Button button, boolean mouseHovered) {
        int xButton = this.relX + button.x;
        int yButton = this.relY + button.y;
        this.blit(ms, xButton, yButton, button.offset, mouseHovered ? this.imageHeight + 20 : this.imageHeight, 20, 20);
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
                UtilitiX.getNetwork().instance.sendToServer(new ClickScreenButtonHandler.Message(this.menu.getPos(), pressed));
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
            this.component = new TranslatableComponent("tooltip.utilitix.experience_crystal." + translationKey);
        }
    }
}