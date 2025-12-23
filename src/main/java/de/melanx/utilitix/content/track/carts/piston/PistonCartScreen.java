package de.melanx.utilitix.content.track.carts.piston;

import com.mojang.blaze3d.systems.RenderSystem;
import de.melanx.utilitix.UtilitiX;
import de.melanx.utilitix.network.PistonCartModeCycle;
import de.melanx.utilitix.registration.ModItemTags;
import de.melanx.utilitix.util.GhostItemRenderHelper;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.PacketDistributor;

import javax.annotation.Nonnull;
import java.awt.Color;
import java.util.List;

public class PistonCartScreen extends AbstractContainerScreen<PistonCartContainerMenu> {

    public static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(UtilitiX.getInstance().modid, "textures/container/piston_cart.png");
    private static final int TORCHES_SLOT = 12;
    private static final int INPUT_SIZE = 12;
    private static final int SLOT_OFFSET = 18;
    private final List<ItemStack> railItems;
    private final List<ItemStack> torchItems;

    public PistonCartScreen(PistonCartContainerMenu menu, Inventory inv, Component title) {
        super(menu, inv, title);
        this.imageWidth = 176;
        this.imageHeight = 186;
        Registry<Item> itemRegistry = this.menu.getLevel().registryAccess().registryOrThrow(Registries.ITEM);
        this.railItems = itemRegistry.getOrCreateTag(ItemTags.RAILS).stream().map(ItemStack::new).toList();
        this.torchItems = itemRegistry.getOrCreateTag(ModItemTags.RAIL_POWER_SOURCES).stream().map(ItemStack::new).toList();
    }

    @Override
    protected void init() {
        super.init();
        this.addRenderableWidget(Button.builder(Component.empty(), button -> {
                    PacketDistributor.sendToServer(new PistonCartModeCycle.Message(this.menu.entity.getId()));
                })
                .pos(this.leftPos + 64, this.topPos + 17)
                .size(48, 18)
                .build());
    }

    @Override
    public void render(@Nonnull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(guiGraphics, mouseX, mouseY, partialTick);
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        this.renderTooltip(guiGraphics, mouseX, mouseY);
    }

    @Override
    protected void renderBg(@Nonnull GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, TEXTURE);
        guiGraphics.blit(TEXTURE, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight);

        if (!this.menu.getSlot(PistonCartScreen.TORCHES_SLOT).hasItem()) {
            GhostItemRenderHelper.renderGhostItem(this.torchItems, guiGraphics, this.leftPos + 80, this.topPos + 72);
        }

        for (int i = 0; i < PistonCartScreen.INPUT_SIZE; i++) {
            ItemStack stack = this.menu.getSlot(i).getItem();
            if (stack.isEmpty()) {
                GhostItemRenderHelper.renderGhostItem(this.railItems, guiGraphics, this.leftPos + 8 + (i % 3) * PistonCartScreen.SLOT_OFFSET, this.topPos + PistonCartScreen.SLOT_OFFSET + (i / 3) * PistonCartScreen.SLOT_OFFSET);
            }
        }
    }

    @Override
    protected void renderLabels(@Nonnull GuiGraphics guiGraphics, int mouseX, int mouseY) {
        String s = this.title.getString();
        guiGraphics.drawString(this.font, s, (float) ((this.imageWidth / 2) - (this.font.width(s) / 2)), 5, Color.DARK_GRAY.getRGB(), false);
        guiGraphics.drawString(this.font, this.playerInventoryTitle, 8, this.imageHeight - 94, Color.DARK_GRAY.getRGB(), false);
        if (this.menu.entity != null) {
            //noinspection ConstantConditions
            int modeStrWidth = this.font.width(this.menu.entity.getMode().name);
            guiGraphics.drawString(this.font, this.menu.entity.getMode().name.getString(), (float) (88 - (modeStrWidth / 2)), 22, 0xFFFFFF, true);
        }
    }
}
