package de.melanx.utilitix.compat.zeta;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import org.violetmoon.zeta.config.ZetaGeneralConfig;

public class ZetaCompat {

    private static final MutableComponent WARNING = Component.translatable("utilitix.compat.zeta.disable_piston_logic_replacement").withStyle(ChatFormatting.RED);

    public static Component warningForGlue() {
        Component component = null;

        if (ZetaGeneralConfig.usePistonLogicRepl) {
            component = WARNING;
        }

        return component;
    }
}
