package de.melanx.utilitix.compat.jade;

import de.melanx.utilitix.UtilitiX;
import de.melanx.utilitix.content.slime.SlimyCapability;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;

public class GlueProvider implements IBlockComponentProvider {

    public static final ResourceLocation UID = UtilitiX.getInstance().resource("glue_information");
    public static final GlueProvider INSTANCE = new GlueProvider();
    private static final Component INFORMATION = Component.translatable("jade.utilitix.glue_information").withStyle(ChatFormatting.GREEN);

    @Override
    public void appendTooltip(ITooltip tooltip, BlockAccessor accessor, IPluginConfig config) {
        if (!config.get(UtilJade.GLUE_INFORMATION)) {
            return;
        }

        accessor.getLevel().getChunkAt(accessor.getPosition()).getCapability(SlimyCapability.STICKY_CHUNK).ifPresent(stickyChunk -> {
            if (stickyChunk.get(accessor.getPosition().getX(), accessor.getPosition().getY(), accessor.getPosition().getZ(), accessor.getSide())) {
                tooltip.add(INFORMATION);
            }
        });
    }

    @Override
    public ResourceLocation getUid() {
        return UID;
    }
}
