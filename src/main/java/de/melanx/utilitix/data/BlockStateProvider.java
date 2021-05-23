package de.melanx.utilitix.data;

import de.melanx.utilitix.UtilitiX;
import de.melanx.utilitix.block.ComparatorRedirector;
import de.melanx.utilitix.registration.ModBlocks;
import io.github.noeppi_noeppi.libx.data.provider.BlockStateProviderBase;
import net.minecraft.block.Block;
import net.minecraft.data.DataGenerator;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.common.data.ExistingFileHelper;

public class BlockStateProvider extends BlockStateProviderBase {

    public BlockStateProvider(DataGenerator generator, ExistingFileHelper fileHelper) {
        super(UtilitiX.getInstance(), generator, fileHelper);
    }

    @Override
    protected void setup() {
        this.manualState(ModBlocks.weakRedstoneTorch);
        this.manualState(ModBlocks.weakRedstoneTorch.wallTorch);
        this.manualModel(ModBlocks.advancedBrewery);
    }

    @Override
    protected ModelFile defaultModel(ResourceLocation id, Block block) {
        if (block instanceof ComparatorRedirector) {
            ResourceLocation top = new ResourceLocation(UtilitiX.getInstance().modid, "block/comparator_redirector_top");
            ResourceLocation bottom = new ResourceLocation(UtilitiX.getInstance().modid, "block/comparator_redirector_bottom");
            if (((ComparatorRedirector) block).direction == Direction.DOWN) {
                ResourceLocation tmp = top;
                top = bottom;
                bottom = tmp;
            }
            return this.models().cubeBottomTop(id.getPath(),
                    new ResourceLocation(UtilitiX.getInstance().modid, "block/comparator_redirector_side"),
                    top, bottom
            );
        } else {
            return super.defaultModel(id, block);
        }
    }
}
