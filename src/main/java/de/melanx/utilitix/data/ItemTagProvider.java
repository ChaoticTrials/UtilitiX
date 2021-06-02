package de.melanx.utilitix.data;

import de.melanx.utilitix.UtilitiX;
import de.melanx.utilitix.registration.ModBlockTags;
import de.melanx.utilitix.registration.ModBlocks;
import de.melanx.utilitix.registration.ModItemTags;
import de.melanx.utilitix.registration.ModItems;
import io.github.noeppi_noeppi.libx.data.provider.BlockTagProviderBase;
import io.github.noeppi_noeppi.libx.data.provider.ItemTagProviderBase;
import net.minecraft.data.DataGenerator;
import net.minecraft.item.Items;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraftforge.common.data.ExistingFileHelper;

public class ItemTagProvider extends ItemTagProviderBase {

    public ItemTagProvider(DataGenerator generator, ExistingFileHelper helper, BlockTagProviderBase blockTags) {
        super(UtilitiX.getInstance(), generator, helper, blockTags);
    }

    @Override
    protected void setup() {
        this.getOrCreateBuilder(ModItemTags.BOTTLES).addTag(ModItemTags.POTIONS);
        this.getOrCreateBuilder(ModItemTags.POTIONS).add(Items.POTION, Items.SPLASH_POTION, Items.LINGERING_POTION);
        this.getOrCreateBuilder(ModItemTags.BOTTLES).add(Items.GLASS_BOTTLE, ModItems.failedPotion);
        this.getOrCreateBuilder(ModItemTags.RAIL_POWER_SOURCES).add(Items.REDSTONE_TORCH, ModBlocks.weakRedstoneTorch.asItem());
        this.copy(BlockTags.RAILS, ItemTags.RAILS);
    }
}
