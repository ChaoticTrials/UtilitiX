package de.melanx.utilitix.registration;

import de.melanx.utilitix.UtilitiX;
import net.minecraft.client.KeyMapping;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.client.settings.KeyConflictContext;
import net.neoforged.neoforge.common.util.Lazy;
import org.lwjgl.glfw.GLFW;

public class ModKeys {

    public static final Lazy<KeyMapping.Category> CATEGORY = Lazy.of(() -> KeyMapping.Category.register(
            Identifier.fromNamespaceAndPath(UtilitiX.getInstance().modid, "key.categories." + UtilitiX.getInstance().modid)
    ));

    public static final Lazy<KeyMapping> OPEN_BACKPACK = Lazy.of(() -> {
        KeyMapping key = new KeyMapping(
                "key." + UtilitiX.getInstance().modid + ".open_backpack",
                GLFW.GLFW_KEY_O,
                CATEGORY.get()
        );

        key.setKeyConflictContext(KeyConflictContext.IN_GAME);

        return key;
    });
}
