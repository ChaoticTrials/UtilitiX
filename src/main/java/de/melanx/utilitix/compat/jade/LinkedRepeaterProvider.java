package de.melanx.utilitix.compat.jade;

import net.minecraft.ChatFormatting;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;

import javax.annotation.Nonnull;
import java.util.Optional;
import java.util.UUID;

public class LinkedRepeaterProvider implements IBlockComponentProvider {

    public static final LinkedRepeaterProvider INSTANCE = new LinkedRepeaterProvider();

    @Nonnull
    @Override
    public Identifier getUid() {
        return UtilJade.LINKED_REPEATER;
    }

    @Override
    public void appendTooltip(@Nonnull ITooltip tooltip, @Nonnull BlockAccessor accessor, IPluginConfig config) {
        if (!config.get(UtilJade.LINKED_REPEATER) || !accessor.getServerData().getBooleanOr("showDetails", false)) {
            return;
        }

        Optional<UUID> linkId = accessor.getServerData().read("LinkId", UUIDUtil.CODEC);
        if (linkId.isPresent()) {
            tooltip.add(Component.translatable("tooltip.utilitix.valid_link", Component.literal(linkId.get().toString()).withStyle(ChatFormatting.GREEN)).withStyle(ChatFormatting.RED));
        } else {
            tooltip.add(Component.translatable("tooltip.utilitix.invalid_link").withStyle(ChatFormatting.RED));
        }
    }
}
