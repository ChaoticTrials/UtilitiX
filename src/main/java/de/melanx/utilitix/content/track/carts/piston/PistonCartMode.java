package de.melanx.utilitix.content.track.carts.piston;

import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

public enum PistonCartMode {
    IDLE("idle"),
    PLACE("place"),
    REPLACE("replace");
    
    public final ITextComponent name;

    PistonCartMode(String id) {
        this.name = new TranslationTextComponent("tooltip.utilitix.piston_cart_mode_" + id);
    }
}
