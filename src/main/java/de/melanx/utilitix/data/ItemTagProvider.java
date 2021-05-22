package de.melanx.utilitix.data;

import de.melanx.utilitix.UtilitiX;
import io.github.noeppi_noeppi.libx.data.provider.BlockTagProviderBase;
import io.github.noeppi_noeppi.libx.data.provider.ItemTagProviderBase;
import net.minecraft.data.DataGenerator;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.tags.ITag;
import net.minecraft.tags.ItemTags;
import net.minecraftforge.common.data.ExistingFileHelper;

public class ItemTagProvider extends ItemTagProviderBase {

    public static final ITag.INamedTag<Item> STICKS = forge("sticks");
    public static final ITag.INamedTag<Item> WOODEN_STICKS = forge("sticks/wooden");

    private static ITag.INamedTag<Item> forge(String name) {
        return ItemTags.makeWrapperTag("forge:" + name);
    }

    public ItemTagProvider(DataGenerator generator, ExistingFileHelper helper, BlockTagProviderBase blockTags) {
        super(UtilitiX.getInstance(), generator, helper, blockTags);
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void setup() {
        this.getOrCreateBuilder(STICKS).add(Items.STICK);
        this.getOrCreateBuilder(WOODEN_STICKS).addTags(STICKS);
    }
}
