package de.melanx.utilitix.compat.jade;

import de.melanx.utilitix.content.experiencecrystal.ExperienceCrystalBlockEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.Identifier;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IServerDataProvider;

import javax.annotation.Nonnull;

public class ExperienceCrystalServerDataProvider implements IServerDataProvider<BlockAccessor> {

    public static final ExperienceCrystalServerDataProvider INSTANCE = new ExperienceCrystalServerDataProvider();

    @Nonnull
    @Override
    public Identifier getUid() {
        return ExperienceCrystalProvider.UID;
    }

    @Override
    public void appendServerData(@Nonnull CompoundTag data, BlockAccessor accessor) {
        ExperienceCrystalBlockEntity crystal = (ExperienceCrystalBlockEntity) accessor.getBlockEntity();
        if (crystal == null) {
            return;
        }

        data.putInt("Xp", crystal.getXp());
        data.putBoolean("ShowDetails", accessor.showDetails());
    }
}
