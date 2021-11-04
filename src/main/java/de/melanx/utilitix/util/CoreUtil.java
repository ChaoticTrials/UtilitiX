package de.melanx.utilitix.util;

import de.melanx.utilitix.UtilitiXConfig;
import net.minecraft.world.entity.player.Player;

public class CoreUtil {

    public static boolean shouldPreventWaterlogging(Player player) {
        return UtilitiXConfig.crouchNoWaterlog && player.isShiftKeyDown();
    }
}
