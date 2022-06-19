package de.melanx.utilitix.client.commands;

import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import de.melanx.utilitix.UtilitiX;
import de.melanx.utilitix.UtilitiXConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.MapRenderer;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.MapItem;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;
import net.minecraftforge.fml.loading.FMLPaths;

import java.io.IOException;
import java.nio.file.Path;

public class MapsCommand {

    private static final Path MAPS = FMLPaths.GAMEDIR.get().resolve("maps");

    public static ArgumentBuilder<CommandSourceStack, ?> register() {
        return Commands.literal("printmap")
                .executes(MapsCommand::printmap);
    }

    private static int printmap(CommandContext<CommandSourceStack> command) throws CommandSyntaxException {
        CommandSourceStack source = command.getSource();
        Entity entity = source.getEntityOrException();

        if (!(entity instanceof Player player)) {
            return 0;
        }

        ItemStack stack = player.getMainHandItem();

        if (!(stack.getItem() instanceof MapItem)) {
            return 0;
        }

        if (MapItem.getMapId(stack) == null) {
            return 0;
        }

        //noinspection ConstantConditions
        int mapId = MapItem.getMapId(stack);

        MapItemSavedData data = MapItem.getSavedData(stack, player.level);
        if (data == null) {
            return 0;
        }

        MapRenderer mapRenderer = Minecraft.getInstance().gameRenderer.getMapRenderer();
        MapRenderer.MapInstance mapInstance = mapRenderer.getOrCreateMapInstance(mapId, data);
        NativeImage img = mapInstance.texture.getPixels();

        if (!MAPS.toFile().exists() && !MAPS.toFile().mkdirs()) {
            UtilitiX.getInstance().logger.warn("Could not create Maps directory: {}", MAPS);
            return 0;
        }

        if (img == null) {
            return 0;
        }

        if (UtilitiXConfig.mapScale != 1) {
            img = MapsCommand.resize(data, img, UtilitiXConfig.mapScale);
        }

        Path path = MAPS.resolve("map_" + mapId + ".png");
        try {
            img.writeToFile(path);
            player.sendSystemMessage(Component.translatable("utilitix.map_saved", path));
            return 1;
        } catch (IOException e) {
            player.sendSystemMessage(Component.translatable("message.utilitix.map_save_command"));
            UtilitiX.getInstance().logger.warn("Files to save file: {}", path, e);
            return 0;
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
