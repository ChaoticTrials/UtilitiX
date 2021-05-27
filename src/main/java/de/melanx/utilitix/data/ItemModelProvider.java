package de.melanx.utilitix.data;

import de.melanx.utilitix.UtilitiX;
import de.melanx.utilitix.block.WeakRedstoneTorch;
import de.melanx.utilitix.content.bell.BellBase;
import de.melanx.utilitix.registration.ModBlocks;
import de.melanx.utilitix.registration.ModItems;
import io.github.noeppi_noeppi.libx.data.provider.ItemModelProviderBase;
import net.minecraft.block.AbstractRailBlock;
import net.minecraft.data.DataGenerator;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.data.ExistingFileHelper;

public class ItemModelProvider extends ItemModelProviderBase {

    public ItemModelProvider(DataGenerator generator, ExistingFileHelper helper) {
        super(UtilitiX.getInstance(), generator, helper);
    }

    @Override
    protected void setup() {
        this.handheld(ModItems.minecartTinkerer);
        this.manualModel(ModItems.mobBell);
    }

    @Override
    protected void defaultItem(ResourceLocation id, Item item) {
        if (item instanceof BellBase) {
            super.defaultItem(new ResourceLocation(id.getNamespace(), id.getPath() + "_item"), item);
        } else {
            super.defaultItem(id, item);
        }
    }

    @Override
    protected void defaultBlock(ResourceLocation id, BlockItem item) {
        if (item.getBlock() == ModBlocks.filterRail) {
            this.withExistingParent(id.getPath(), GENERATED).texture("layer0", new ResourceLocation(id.getNamespace(), "block/" + id.getPath() + "_right"));
        } else if (item.getBlock() instanceof WeakRedstoneTorch || item.getBlock() instanceof AbstractRailBlock) {
            this.withExistingParent(id.getPath(), GENERATED).texture("layer0", new ResourceLocation(id.getNamespace(), "block/" + id.getPath()));
        } else if (item.getBlock() == ModBlocks.linkedRepeater) {
            this.withExistingParent(id.getPath(), GENERATED).texture("layer0", new ResourceLocation(id.getNamespace(), "item/" + id.getPath()));
        } else {
            super.defaultBlock(id, item);
        }
    }
}
