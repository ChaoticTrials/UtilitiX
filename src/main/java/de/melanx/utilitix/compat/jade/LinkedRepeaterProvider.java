package de.melanx.utilitix.compat.jade;

import de.melanx.utilitix.UtilitiX;
import de.melanx.utilitix.content.wireless.TileLinkedRepeater;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.IServerDataProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;

import java.util.UUID;

public class LinkedRepeaterProvider implements IBlockComponentProvider, IServerDataProvider<BlockAccessor> {

    public static final ResourceLocation UID = UtilitiX.getInstance().resource("linked_repeater");
    public static final LinkedRepeaterProvider INSTANCE = new LinkedRepeaterProvider();

    @Override
    public ResourceLocation getUid() {
        return UID;
    }

    @Override
    public void appendTooltip(ITooltip tooltip, BlockAccessor accessor, IPluginConfig config) {
        if (!config.get(UtilJade.LINKED_REPEATER) || !accessor.getServerData().getBoolean("showDetails")) {
            return;
        }

        if (accessor.getServerData().get("LinkId") != null) {
            UUID linkId = accessor.getServerData().getUUID("LinkId");
            tooltip.add(Component.translatable("tooltip.utilitix.valid_link", Component.literal(linkId.toString()).withStyle(ChatFormatting.GREEN)).withStyle(ChatFormatting.RED));
        } else {
            tooltip.add(Component.translatable("tooltip.utilitix.invalid_link").withStyle(ChatFormatting.RED));
        }
    }

    @Override
    public void appendServerData(CompoundTag data, BlockAccessor accessor) {
        TileLinkedRepeater linkedRepeater = (TileLinkedRepeater) accessor.getBlockEntity();
        UUID id = linkedRepeater.getLinkId();
        if (id != null) {
            data.putUUID("LinkId", id);
        }
        data.putBoolean("showDetails", accessor.showDetails());
    }
}
