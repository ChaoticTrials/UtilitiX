package de.melanx.utilitix.data;

import de.melanx.utilitix.UtilitiX;
import io.github.noeppi_noeppi.libx.data.provider.BlockLootProviderBase;
import net.minecraft.data.DataGenerator;

public class LootTableProvider extends BlockLootProviderBase {

    public LootTableProvider(DataGenerator generator) {
        super(UtilitiX.getInstance(), generator);
    }

    @Override
    protected void setup() {
    }
}
