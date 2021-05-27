package de.melanx.utilitix.data;

import de.melanx.utilitix.UtilitiX;
import io.github.noeppi_noeppi.libx.data.provider.BlockTagProviderBase;
import net.minecraft.block.AbstractRailBlock;
import net.minecraft.block.Block;
import net.minecraft.data.DataGenerator;
import net.minecraft.tags.BlockTags;
import net.minecraftforge.common.data.ExistingFileHelper;

public class BlockTagProvider extends BlockTagProviderBase {

    public BlockTagProvider(DataGenerator generator, ExistingFileHelper helper) {
        super(UtilitiX.getInstance(), generator, helper);
    }

    @Override
    protected void setup() {

    }

    @Override
    public void defaultBlockTags(Block block) {
        if (block instanceof AbstractRailBlock) {
            this.getOrCreateBuilder(BlockTags.RAILS).add(block);
        }
        super.defaultBlockTags(block);
    }
}
