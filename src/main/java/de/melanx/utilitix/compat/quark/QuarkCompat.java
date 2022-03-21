package de.melanx.utilitix.compat.quark;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TranslatableComponent;
import vazkii.quark.base.handler.GeneralConfig;

public class QuarkCompat {

    private static final MutableComponent WARNING = new TranslatableComponent("utilitix.compat.quark.disable_piston_logic_replacement").withStyle(ChatFormatting.RED);

    public static Component warningForGlue() {
        Component component = null;

        if (GeneralConfig.usePistonLogicRepl) {
            component = WARNING;
        }

        return component;
    }
}
