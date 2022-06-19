package de.melanx.utilitix.data;

import de.melanx.utilitix.registration.ModBlocks;
import net.minecraft.data.DataGenerator;
import org.moddingx.libx.annotation.data.Datagen;
import org.moddingx.libx.datagen.provider.loot.BlockLootProviderBase;
import org.moddingx.libx.mod.ModX;

@Datagen
public class LootTableProvider extends BlockLootProviderBase {

    public LootTableProvider(ModX mod, DataGenerator generator) {
        super(mod, generator);
    }

    @Override
    protected void setup() {
        this.drops(ModBlocks.experienceCrystal, this.copyNBT("Xp"));
    }
}
