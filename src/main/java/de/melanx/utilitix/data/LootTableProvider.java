package de.melanx.utilitix.data;

import org.moddingx.libx.datagen.DatagenContext;
import org.moddingx.libx.datagen.provider.loot.BlockLootProviderBase;

public class LootTableProvider extends BlockLootProviderBase {

    public LootTableProvider(DatagenContext context) {
        super(context);
    }

    @Override
    protected void setup() {
//        this.drops(ModBlocks.experienceCrystal, this.noSilk(), this.copyNBT("Xp")); todo
    }
}
