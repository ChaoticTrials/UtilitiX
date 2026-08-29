package de.melanx.utilitix.client.commands;

import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import de.melanx.utilitix.UtilitiX;
import de.melanx.utilitix.config.ClientConfig;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.MapItem;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.saveddata.maps.MapId;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;
import net.neoforged.fml.loading.FMLPaths;
import org.moddingx.libx.command.CommandUtil;

import java.io.IOException;
import java.nio.file.Path;

public class MapsCommand {

    private static final Path MAPS_PATH = FMLPaths.GAMEDIR.get().resolve("maps");

    public static ArgumentBuilder<CommandSourceStack, ?> register() {
        return Commands.literal("print_map")
                .executes(MapsCommand::printMap)
                .then(Commands.argument("scale", IntegerArgumentType.integer(1))
                        .executes(MapsCommand::printMap));
    }

    private static int printMap(CommandContext<CommandSourceStack> command) throws CommandSyntaxException {
        CommandSourceStack source = command.getSource();
        Entity entity = source.getEntityOrException();

        if (!(entity instanceof Player player)) {
            return 0;
        }

        ItemStack stack = player.getMainHandItem();
        if (!(stack.getItem() instanceof MapItem)) {
            source.sendFailure(Component.translatable("message.utilitix.map_save_command.no_map_found"));
            return 0;
        }

        MapId mapId = stack.get(DataComponents.MAP_ID);
        if (mapId == null) {
            source.sendFailure(Component.translatable("message.utilitix.map_save_command.no_map_found"));
            return 0;
        }

        MapItemSavedData data = MapItem.getSavedData(stack, player.level());
        if (data == null) {
            source.sendFailure(Component.translatable("message.utilitix.map_save_command.no_map_found"));
            return 0;
        }

        NativeImage img = new NativeImage(128, 128, true);
        for (int y = 0; y < 128; y++) {
            for (int x = 0; x < 128; x++) {
                img.setPixel(x, y, MapColor.getColorFromPackedId(data.colors[x + y * 128]));
            }
        }

        if (!MAPS_PATH.toFile().exists() && !MAPS_PATH.toFile().mkdirs()) {
            UtilitiX.getInstance().logger.warn("Could not create Maps directory: {}", MAPS_PATH);
            return 0;
        }

        int mapScale = CommandUtil.getArgumentOrDefault(command, "scale", Integer.class, ClientConfig.mapScale);
        if (mapScale != 1) {
            img = MapsCommand.resize(data, img, mapScale);
        }

        Path path = MAPS_PATH.resolve("map_" + mapId.id() + ".png");
        try {
            img.writeToFile(path);
            source.sendSuccess(() -> Component.translatable("utilitix.map_saved", path.toString()), false);
            return 1;
        } catch (IOException e) {
            source.sendFailure(Component.translatable("message.utilitix.map_save_command"));
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
                img.setPixel(j, i, MapColor.getColorFromPackedId(data.colors[k]));
            }
        }

        return img;
    }
}
