package de.melanx.utilitix.data;

import de.melanx.utilitix.registration.ModBlockTags;
import de.melanx.utilitix.registration.ModBlocks;
import de.melanx.utilitix.registration.ModItemTags;
import de.melanx.utilitix.registration.ModItems;
import net.minecraft.data.DataGenerator;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.BaseRailBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.moddingx.libx.annotation.data.Datagen;
import org.moddingx.libx.datagen.provider.CommonTagsProviderBase;
import org.moddingx.libx.mod.ModX;

@Datagen
public class ModTagProvider extends CommonTagsProviderBase {

    public ModTagProvider(ModX mod, DataGenerator generator, ExistingFileHelper helper) {
        super(mod, generator, helper);
    }

    @Override
    public void setup() {
        this.block(ModBlockTags.RAIL_POWER_SOURCES).add(Blocks.REDSTONE_TORCH, Blocks.REDSTONE_WALL_TORCH, ModBlocks.weakRedstoneTorch, ModBlocks.weakRedstoneTorch.wallTorch);
        this.block(BlockTags.WALLS).add(ModBlocks.stoneWall);

        this.item(ModItemTags.BOTTLES).addTag(ModItemTags.POTIONS);
        this.item(ModItemTags.POTIONS).add(Items.POTION, Items.SPLASH_POTION, Items.LINGERING_POTION);
        this.item(ModItemTags.BOTTLES).add(Items.GLASS_BOTTLE, ModItems.failedPotion);
        this.item(ModItemTags.RAIL_POWER_SOURCES).add(Items.REDSTONE_TORCH, ModBlocks.weakRedstoneTorch.asItem());
        this.item(Tags.Items.SHEARS).add(ModItems.diamondShears);
        this.item(ItemTags.CHEST_BOATS).addTag(ModItemTags.SHULKER_CHEST_BOATS);
        this.item(ModItemTags.SHULKER_CHEST_BOATS).add(ModItems.oakShulkerBoat, ModItems.spruceShulkerBoat, ModItems.birchShulkerBoat, ModItems.jungleShulkerBoat, ModItems.acaciaShulkerBoat, ModItems.darkOakShulkerBoat, ModItems.mangroveShulkerBoat);

        this.copyBlock(BlockTags.RAILS, ItemTags.RAILS);
        this.copyBlock(BlockTags.WALLS, ItemTags.WALLS);
    }

    @Override
    public void defaultBlockTags(Block block) {
        if (block instanceof BaseRailBlock) {
            this.block(BlockTags.RAILS).add(block);
        }

        if (block != ModBlocks.linkedRepeater && block != ModBlocks.weakRedstoneTorch) {
            this.block(BlockTags.MINEABLE_WITH_PICKAXE).add(block);
        }

        super.defaultBlockTags(block);
    }
}
