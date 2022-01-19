package de.melanx.utilitix.client;

import com.mojang.blaze3d.platform.NativeImage;
import de.melanx.utilitix.UtilitiX;
import de.melanx.utilitix.UtilitiXConfig;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.MapRenderer;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.MapItem;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.loading.FMLPaths;
import org.lwjgl.glfw.GLFW;

import java.io.IOException;
import java.nio.file.Path;

public class ClientEventListener {

    private static final Path MAPS = FMLPaths.GAMEDIR.get().resolve("maps");

    @SubscribeEvent
    public void onKeyInput(InputEvent.KeyInputEvent event) {
        ClientEventListener.handleAction(event.getAction(), event.getKey());
    }

    @SubscribeEvent
    public void lul(InputEvent.MouseInputEvent event) {
        ClientEventListener.handleAction(event.getAction(), event.getButton());
    }

    private static void handleAction(int action, int button) {
        if (action == GLFW.GLFW_PRESS) {
            if (Keys.SAVE_MAP.getKey().getValue() == button) {
                LocalPlayer player = Minecraft.getInstance().player;
                if (player != null) {
                    ItemStack stack = player.getMainHandItem();
                    if (!(stack.getItem() instanceof MapItem)) {
                        return;
                    }

                    if (MapItem.getMapId(stack) == null) {
                        return;
                    }

                    //noinspection ConstantConditions
                    int mapId = MapItem.getMapId(stack);

                    MapItemSavedData data = MapItem.getSavedData(stack, player.level);
                    if (data == null) {
                        return;
                    }

                    MapRenderer mapRenderer = Minecraft.getInstance().gameRenderer.getMapRenderer();
                    MapRenderer.MapInstance mapInstance = mapRenderer.getOrCreateMapInstance(mapId, data);
                    NativeImage img = mapInstance.texture.getPixels();

                    if (!MAPS.toFile().exists() && !MAPS.toFile().mkdirs()) {
                        UtilitiX.getInstance().logger.warn("Could not create Maps directory: {}", MAPS);
                        return;
                    }

                    if (img == null) {
                        return;
                    }

                    if (UtilitiXConfig.mapScale != 1) {
                        img = ClientEventListener.resize(data, img, UtilitiXConfig.mapScale);
                    }

                    Path path = MAPS.resolve("map_" + mapId + ".png");
                    try {
                        img.writeToFile(path);
                        player.sendMessage(new TranslatableComponent("utilitix.map_saved", path), Util.NIL_UUID);
                    } catch (IOException e) {
                        player.sendMessage(new TextComponent("Something went wrong saving the map. More information in the log."), Util.NIL_UUID);
                        UtilitiX.getInstance().logger.warn("Files to save file: {}", path, e);
                    }
                }
            }
        }
    }

    private static NativeImage resize(MapItemSavedData data, NativeImage original, int scale) {
        int size = 128 * scale;
        NativeImage img = new NativeImage(original.getWidth() * scale, original.getHeight() * scale, true);
        for (int i = 0; i < size; ++i) {
            for (int j = 0; j < size; ++j) {
                int k = (j / scale) + (i / scale) * 128;
                img.setPixelRGBA(j, i, MaterialColor.getColorFromPackedId(data.colors[k]));
            }
        }

        return img;
    }
}
