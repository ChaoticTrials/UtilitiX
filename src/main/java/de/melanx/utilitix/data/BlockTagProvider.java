package de.melanx.utilitix.data;

import de.melanx.utilitix.UtilitiX;
import io.github.noeppi_noeppi.libx.data.provider.BlockTagProviderBase;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.common.data.ExistingFileHelper;

public class BlockTagProvider extends BlockTagProviderBase {

    public BlockTagProvider(DataGenerator generator, ExistingFileHelper helper) {
        super(UtilitiX.getInstance(), generator, helper);
    }

    @Override
    protected void setup() {

    }
}
