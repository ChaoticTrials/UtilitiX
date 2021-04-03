package de.melanx.utilitix.data;

import de.melanx.utilitix.UtilitiX;
import de.melanx.utilitix.registration.ModBlocks;
import io.github.noeppi_noeppi.libx.data.provider.BlockStateProviderBase;
import net.minecraft.block.Block;
import net.minecraft.data.DataGenerator;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.generators.BlockModelBuilder;
import net.minecraftforge.client.model.generators.ConfiguredModel;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.client.model.generators.VariantBlockStateBuilder;
import net.minecraftforge.common.data.ExistingFileHelper;

public class BlockStates extends BlockStateProviderBase {
    public BlockStates(DataGenerator generator, ExistingFileHelper fileHelper) {
        super(UtilitiX.getInstance(), generator, fileHelper);
    }

    @Override
    protected void setup() {
        this.torch(ModBlocks.weakRedstoneTorch);
        this.torchWall(ModBlocks.weakRedstoneWallTorch);
    }

    private void torch(Block b) {
        //noinspection ConstantConditions
        String name = b.getRegistryName().getPath();
        this.manualModel(b);
        this.models().torch(name, new ResourceLocation(UtilitiX.getInstance().modid, "block/" + name));
    }

    private void torchWall(Block b) {
        //noinspection ConstantConditions
        String name = b.getRegistryName().getPath();
        this.manualModel(b);
        BlockModelBuilder model = this.models().torchWall(name, new ResourceLocation(UtilitiX.getInstance().modid, "block/" + name.replace("_wall", "")));
    }

    @Override
    protected void defaultState(ResourceLocation id, Block block, ModelFile model) {
        if (block.getStateContainer().getProperties().contains(BlockStateProperties.HORIZONTAL_FACING)) {
            VariantBlockStateBuilder builder = this.getVariantBuilder(block);
            for (Direction direction : BlockStateProperties.HORIZONTAL_FACING.getAllowedValues()) {
                builder.partialState().with(BlockStateProperties.HORIZONTAL_FACING, direction)
                        .addModels(new ConfiguredModel(model, direction.getHorizontalIndex() == -1 ? direction.getOpposite().getAxisDirection().getOffset() * 90 : 0, (int) direction.getOpposite().getHorizontalAngle() - 90, false));
            }
        } else if (block.getStateContainer().getProperties().contains(BlockStateProperties.FACING)) {
            VariantBlockStateBuilder builder = this.getVariantBuilder(block);
            for (Direction direction : BlockStateProperties.FACING.getAllowedValues()) {
                builder.partialState().with(BlockStateProperties.FACING, direction)
                        .addModels(new ConfiguredModel(model, direction.getHorizontalIndex() == -1 ? direction.getOpposite().getAxisDirection().getOffset() * 90 : 0, (int) direction.getOpposite().getHorizontalAngle() - 90, false));
            }
        } else {
            this.simpleBlock(block, model);
        }
    }
}
