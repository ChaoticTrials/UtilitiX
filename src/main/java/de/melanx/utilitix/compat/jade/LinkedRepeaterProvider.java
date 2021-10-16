package de.melanx.utilitix.compat.jade;

import de.melanx.utilitix.content.wireless.TileLinkedRepeater;
import mcp.mobius.waila.api.BlockAccessor;
import mcp.mobius.waila.api.IComponentProvider;
import mcp.mobius.waila.api.IServerDataProvider;
import mcp.mobius.waila.api.ITooltip;
import mcp.mobius.waila.api.config.IPluginConfig;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.UUID;

public class LinkedRepeaterProvider implements IComponentProvider, IServerDataProvider<BlockEntity> {

    public static final LinkedRepeaterProvider INSTANCE = new LinkedRepeaterProvider();

    @Override
    public void appendTooltip(ITooltip tooltip, BlockAccessor accessor, IPluginConfig config) {
        if (!config.get(UtilJade.LINKED_REPEATER) || !accessor.getServerData().getBoolean("showDetails")) {
            return;
        }

        if (accessor.getServerData().get("LinkId") != null) {
            UUID linkId = accessor.getServerData().getUUID("LinkId");
            tooltip.add(new TranslatableComponent("tooltip.utilitix.valid_link", new TextComponent(linkId.toString()).withStyle(ChatFormatting.GREEN)).withStyle(ChatFormatting.RED));
        } else {
            tooltip.add(new TranslatableComponent("tooltip.utilitix.invalid_link").withStyle(ChatFormatting.RED));
        }
    }

    @Override
    public void appendServerData(CompoundTag data, ServerPlayer player, Level level, BlockEntity blockEntity, boolean showDetails) {
        TileLinkedRepeater linkedRepeater = (TileLinkedRepeater) blockEntity;
        UUID id = linkedRepeater.getLinkId();
        if (id != null) {
            data.putUUID("LinkId", id);
        }
        data.putBoolean("showDetails", showDetails);
    }
}
