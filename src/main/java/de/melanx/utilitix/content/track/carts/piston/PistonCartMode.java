package de.melanx.utilitix.content.track.carts.piston;

import net.minecraft.network.chat.Component;

public enum PistonCartMode {
    IDLE("idle"),
    PLACE("place"),
    REPLACE("replace");

    public final Component name;

    PistonCartMode(String id) {
        this.name = Component.translatable("tooltip.utilitix.piston_cart_mode_" + id);
    }
}
