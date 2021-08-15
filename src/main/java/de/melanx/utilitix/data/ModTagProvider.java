package de.melanx.utilitix.data;

import de.melanx.utilitix.UtilitiX;
import de.melanx.utilitix.registration.ModBlockTags;
import de.melanx.utilitix.registration.ModBlocks;
import de.melanx.utilitix.registration.ModItemTags;
import de.melanx.utilitix.registration.ModItems;
import io.github.noeppi_noeppi.libx.data.provider.CommonTagsProviderBase;
import net.minecraft.data.DataGenerator;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.BaseRailBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.common.data.ExistingFileHelper;

public class ModTagProvider extends CommonTagsProviderBase {

    public ModTagProvider(DataGenerator generator, ExistingFileHelper fileHelper) {
        super(UtilitiX.getInstance(), generator, fileHelper);
    }

    @Override
    public void setup() {
        this.block(ModBlockTags.RAIL_POWER_SOURCES).add(Blocks.REDSTONE_TORCH, Blocks.REDSTONE_WALL_TORCH, ModBlocks.weakRedstoneTorch, ModBlocks.weakRedstoneTorch.wallTorch);

        this.item(ModItemTags.BOTTLES).addTag(ModItemTags.POTIONS);
        this.item(ModItemTags.POTIONS).add(Items.POTION, Items.SPLASH_POTION, Items.LINGERING_POTION);
        this.item(ModItemTags.BOTTLES).add(Items.GLASS_BOTTLE, ModItems.failedPotion);
        this.item(ModItemTags.RAIL_POWER_SOURCES).add(Items.REDSTONE_TORCH, ModBlocks.weakRedstoneTorch.asItem());

        this.copyBlock(BlockTags.RAILS, ItemTags.RAILS);
    }

    @Override
    public void defaultBlockTags(Block block) {
        if (block instanceof BaseRailBlock) {
            this.block(BlockTags.RAILS).add(block);
        }
        super.defaultBlockTags(block);
    }
}
