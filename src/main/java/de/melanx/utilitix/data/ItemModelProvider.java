package de.melanx.utilitix.data;

import de.melanx.utilitix.block.WeakRedstoneTorch;
import de.melanx.utilitix.content.bell.BellBase;
import de.melanx.utilitix.registration.ModBlocks;
import de.melanx.utilitix.registration.ModItems;
import io.github.noeppi_noeppi.libx.annotation.data.Datagen;
import io.github.noeppi_noeppi.libx.data.provider.ItemModelProviderBase;
import io.github.noeppi_noeppi.libx.mod.ModX;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.BaseRailBlock;
import net.minecraftforge.common.data.ExistingFileHelper;

@Datagen
public class ItemModelProvider extends ItemModelProviderBase {

    public ItemModelProvider(ModX mod, DataGenerator generator, ExistingFileHelper helper) {
        super(mod, generator, helper);
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
        if (item.getBlock() == ModBlocks.filterRail || item.getBlock() == ModBlocks.reinforcedFilterRail) {
            this.withExistingParent(id.getPath(), GENERATED).texture("layer0", new ResourceLocation(id.getNamespace(), "block/" + id.getPath() + "_right"));
        } else if (item.getBlock() instanceof WeakRedstoneTorch || item.getBlock() instanceof BaseRailBlock) {
            this.withExistingParent(id.getPath(), GENERATED).texture("layer0", new ResourceLocation(id.getNamespace(), "block/" + id.getPath()));
        } else if (item.getBlock() == ModBlocks.linkedRepeater) {
            this.withExistingParent(id.getPath(), GENERATED).texture("layer0", new ResourceLocation(id.getNamespace(), "item/" + id.getPath()));
        } else if (item.getBlock() == ModBlocks.stoneWall) {
            this.withExistingParent(id.getPath(), WALL_PARENT).texture("wall", new ResourceLocation("block/stone"));
        } else {
            super.defaultBlock(id, item);
        }
    }
}
