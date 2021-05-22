package de.melanx.utilitix.data;

import de.melanx.utilitix.UtilitiX;
import de.melanx.utilitix.registration.ModBlocks;
import io.github.noeppi_noeppi.libx.data.provider.BlockStateProviderBase;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.common.data.ExistingFileHelper;

public class BlockStates extends BlockStateProviderBase {
    
    public BlockStates(DataGenerator generator, ExistingFileHelper fileHelper) {
        super(UtilitiX.getInstance(), generator, fileHelper);
    }

    @Override
    protected void setup() {
        this.manualState(ModBlocks.weakRedstoneTorch);
        this.manualState(ModBlocks.weakRedstoneTorch.wallTorch);
    }
}
