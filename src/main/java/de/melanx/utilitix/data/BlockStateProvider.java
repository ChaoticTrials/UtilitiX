package de.melanx.utilitix.data;

import de.melanx.utilitix.UtilitiX;
import de.melanx.utilitix.block.ComparatorRedirector;
import de.melanx.utilitix.registration.ModBlocks;
import io.github.noeppi_noeppi.libx.data.provider.BlockStateProviderBase;
import net.minecraft.block.Block;
import net.minecraft.data.DataGenerator;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.generators.ConfiguredModel;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.client.model.generators.VariantBlockStateBuilder;
import net.minecraftforge.common.data.ExistingFileHelper;

public class BlockStateProvider extends BlockStateProviderBase {

    public static final ResourceLocation LINKED_REPEATER_PARENT = new ResourceLocation(UtilitiX.getInstance().modid, "block/linked_repeater_base");

    public static final ResourceLocation TEXTURE_REPEATER_OFF = new ResourceLocation("minecraft", "block/repeater");
    public static final ResourceLocation TEXTURE_REPEATER_ON = new ResourceLocation("minecraft", "block/repeater_on");
    
    public static final ResourceLocation TEXTURE_TORCH_OFF = new ResourceLocation("minecraft", "block/redstone_torch_off");
    public static final ResourceLocation TEXTURE_TORCH_ON = new ResourceLocation("minecraft", "block/redstone_torch");
    
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
    protected void defaultState(ResourceLocation id, Block block, ModelFile model) {
        if (block == ModBlocks.linkedRepeater) {
            VariantBlockStateBuilder builder = this.getVariantBuilder(block);
            ModelFile modelOn = this.models().withExistingParent(id.getPath() + "_on", LINKED_REPEATER_PARENT)
                    .texture("repeater", TEXTURE_REPEATER_ON)
                    .texture("torch", TEXTURE_TORCH_ON);
            ModelFile modelOff = this.models().withExistingParent(id.getPath() + "_off", LINKED_REPEATER_PARENT)
                    .texture("repeater", TEXTURE_REPEATER_OFF)
                    .texture("torch", TEXTURE_TORCH_OFF);
            for (Direction dir : BlockStateProperties.HORIZONTAL_FACING.getAllowedValues()) {
                for (int power : BlockStateProperties.POWER_0_15.getAllowedValues()) {
                    builder.partialState()
                            .with(BlockStateProperties.HORIZONTAL_FACING, dir)
                            .with(BlockStateProperties.POWER_0_15, power)
                            .addModels(new ConfiguredModel(power > 0 ? modelOn : modelOff, 0, (int) dir.getHorizontalAngle(), false));
                }
            }
        } else {
            super.defaultState(id, block, model);
        }
    }

    @Override
    protected ModelFile defaultModel(ResourceLocation id, Block block) {
        if (block == ModBlocks.linkedRepeater) {
            return null;
        } else if (block instanceof ComparatorRedirector) {
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
