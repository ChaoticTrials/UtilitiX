package de.melanx.utilitix.compat.jade;

import de.melanx.utilitix.UtilitiX;
import de.melanx.utilitix.content.glue.StickyChunk;
import de.melanx.utilitix.registration.ModAttachmentTypes;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.chunk.LevelChunk;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;

import javax.annotation.Nonnull;

public class GlueProvider implements IBlockComponentProvider {

    public static final Identifier UID = UtilitiX.getInstance().id("glue_information");
    public static final GlueProvider INSTANCE = new GlueProvider();
    private static final Component INFORMATION = Component.translatable("jade.utilitix.glue_information").withStyle(ChatFormatting.GREEN);

    @Override
    public void appendTooltip(@Nonnull ITooltip tooltip, @Nonnull BlockAccessor accessor, IPluginConfig config) {
        if (!config.get(UtilJade.GLUE_INFORMATION)) {
            return;
        }

        LevelChunk chunk = accessor.getLevel().getChunkAt(accessor.getPosition());
        StickyChunk stickyChunk = chunk.getExistingDataOrNull(ModAttachmentTypes.stickyChunk);
        if (stickyChunk != null && stickyChunk.get(accessor.getPosition().getX(), accessor.getPosition().getY(), accessor.getPosition().getZ(), accessor.getSide())) {
            tooltip.add(INFORMATION);
        }
    }

    @Nonnull
    @Override
    public Identifier getUid() {
        return UID;
    }
}
