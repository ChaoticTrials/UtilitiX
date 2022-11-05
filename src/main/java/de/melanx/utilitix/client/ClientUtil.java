package de.melanx.utilitix.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class ClientUtil {

    public static boolean canGild(ArmorItem armor, ItemStack stack, Level level) {
        if (!(level instanceof ClientLevel) || Minecraft.getInstance().player == null) return false;
        return !armor.makesPiglinsNeutral(stack, Minecraft.getInstance().player);
    }
}
