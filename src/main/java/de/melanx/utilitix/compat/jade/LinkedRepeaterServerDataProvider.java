package de.melanx.utilitix.compat.jade;

import de.melanx.utilitix.content.redstone.wireless.LinkedRepeaterBlockEntity;
import net.minecraft.core.UUIDUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.Identifier;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IServerDataProvider;

import javax.annotation.Nonnull;
import java.util.UUID;

public class LinkedRepeaterServerDataProvider implements IServerDataProvider<BlockAccessor> {

    public static final LinkedRepeaterServerDataProvider INSTANCE = new LinkedRepeaterServerDataProvider();

    @Nonnull
    @Override
    public Identifier getUid() {
        return LinkedRepeaterProvider.UID;
    }

    @Override
    public void appendServerData(@Nonnull CompoundTag data, BlockAccessor accessor) {
        LinkedRepeaterBlockEntity linkedRepeater = (LinkedRepeaterBlockEntity) accessor.getBlockEntity();
        //noinspection DataFlowIssue
        UUID id = linkedRepeater.getLinkId();
        if (id != null) {
            data.store("LinkId", UUIDUtil.CODEC, id);
        }
        data.putBoolean("showDetails", accessor.showDetails());
    }
}
