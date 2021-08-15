package de.melanx.utilitix;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraftforge.client.event.TextureStitchEvent;

public class Textures {

    public static final ResourceLocation GRAY_BELL_TEXTURE = new ResourceLocation(UtilitiX.getInstance().modid, "special/gray_bell");
    public static final ResourceLocation GLUE_OVERLAY_TEXTURE = new ResourceLocation(UtilitiX.getInstance().modid, "special/glue_ball_overlay");
    
    public static void registerTextures(TextureStitchEvent.Pre event) {
        if (event.getMap().location().equals(InventoryMenu.BLOCK_ATLAS)) {
            event.addSprite(GRAY_BELL_TEXTURE);
            event.addSprite(GLUE_OVERLAY_TEXTURE);
        }
    }
}
