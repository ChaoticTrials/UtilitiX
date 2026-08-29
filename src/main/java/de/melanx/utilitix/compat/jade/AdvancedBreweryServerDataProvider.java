package de.melanx.utilitix.compat.jade;

import de.melanx.utilitix.content.brewery.AdvancedBreweryBlockEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.Identifier;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IServerDataProvider;

import javax.annotation.Nonnull;

public class AdvancedBreweryServerDataProvider implements IServerDataProvider<BlockAccessor> {

    public static final AdvancedBreweryServerDataProvider INSTANCE = new AdvancedBreweryServerDataProvider();

    @Nonnull
    @Override
    public Identifier getUid() {
        return UtilJade.ADVANCED_BREWERY;
    }

    @Override
    public void appendServerData(@Nonnull CompoundTag data, BlockAccessor blockAccessor) {
        AdvancedBreweryBlockEntity brewery = (AdvancedBreweryBlockEntity) blockAccessor.getBlockEntity();
        if (brewery == null) {
            return;
        }

        CompoundTag tag = new CompoundTag();
        tag.putInt("time", AdvancedBreweryBlockEntity.MAX_BREW_TIME - brewery.getBrewTime());
        tag.putInt("fuel", brewery.getFuel());
        data.put("AdvancedBrewery", tag);
    }
}
