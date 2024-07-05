package de.melanx.utilitix.registration;

import de.melanx.utilitix.UtilitiX;
import net.minecraft.client.KeyMapping;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.common.util.Lazy;
import org.lwjgl.glfw.GLFW;

public class ModKeys {

    public static final Lazy<KeyMapping> OPEN_BACKPACK = Lazy.of(() -> {
        KeyMapping key = new KeyMapping(
                "key." + UtilitiX.getInstance().modid + ".open_backpack",
                GLFW.GLFW_KEY_O,
                "key.categories." + UtilitiX.getInstance().modid
        );

        key.setKeyConflictContext(KeyConflictContext.IN_GAME);

        return key;
    });
}
