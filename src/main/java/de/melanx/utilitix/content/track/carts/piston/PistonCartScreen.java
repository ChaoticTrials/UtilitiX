package de.melanx.utilitix.content.track.carts.piston;

import de.melanx.utilitix.UtilitiX;
import de.melanx.utilitix.network.handler.PistonCartModeCycle;
import de.melanx.utilitix.registration.ModItemTags;
import de.melanx.utilitix.util.GhostItemRenderHelper;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;

import javax.annotation.Nonnull;
import java.awt.Color;
import java.util.List;
import java.util.stream.StreamSupport;

public class PistonCartScreen extends AbstractContainerScreen<PistonCartMenu> {

    public static final Identifier TEXTURE = Identifier.fromNamespaceAndPath(UtilitiX.getInstance().modid, "textures/container/piston_cart.png");
    private static final int TORCHES_SLOT = 12;
    private static final int INPUT_SIZE = 12;
    private static final int SLOT_OFFSET = 18;
    private final List<ItemStack> railItems;
    private final List<ItemStack> torchItems;
    private Button modeButton;

    public PistonCartScreen(PistonCartMenu menu, Inventory inv, Component title) {
        super(menu, inv, title, 176, 186);
        Registry<Item> itemRegistry = this.menu.getLevel().registryAccess().lookupOrThrow(Registries.ITEM);
        this.railItems = StreamSupport.stream(itemRegistry.getTagOrEmpty(ItemTags.RAILS).spliterator(), false).map(holder -> new ItemStack(holder)).toList();
        this.torchItems = StreamSupport.stream(itemRegistry.getTagOrEmpty(ModItemTags.RAIL_POWER_SOURCES).spliterator(), false).map(holder -> new ItemStack(holder)).toList();
    }

    @Override
    protected void init() {
        super.init();
        this.modeButton = this.addRenderableWidget(Button.builder(Component.empty(), _ -> {
                    ClientPacketDistributor.sendToServer(new PistonCartModeCycle.Message(this.menu.entity.getId()));
                })
                .pos(this.leftPos + 64, this.topPos + 17)
                .size(48, 18)
                .build());
    }

    @Override
    public void extractBackground(@Nonnull GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.extractBackground(guiGraphics, mouseX, mouseY, partialTick);
        guiGraphics.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, this.leftPos, this.topPos, 0.0F, 0.0F, this.imageWidth, this.imageHeight, 256, 256);

        if (this.menu.entity != null) {
            this.modeButton.setMessage(this.menu.entity.getMode().name);
        }

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
    protected void extractLabels(@Nonnull GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY) {
        String s = this.title.getString();
        guiGraphics.text(this.font, s, (this.imageWidth / 2) - (this.font.width(s) / 2), 5, Color.DARK_GRAY.getRGB(), false);
        guiGraphics.text(this.font, this.playerInventoryTitle, 8, this.imageHeight - 94, Color.DARK_GRAY.getRGB(), false);
    }
}
