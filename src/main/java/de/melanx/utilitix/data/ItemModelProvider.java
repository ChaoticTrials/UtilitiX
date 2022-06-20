package de.melanx.utilitix.data;

import de.melanx.utilitix.UtilitiX;
import de.melanx.utilitix.block.WeakRedstoneTorch;
import de.melanx.utilitix.content.AncientCompass;
import de.melanx.utilitix.content.bell.BellBase;
import de.melanx.utilitix.item.ItemMobYoinker;
import de.melanx.utilitix.registration.ModBlocks;
import de.melanx.utilitix.registration.ModItems;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.BaseRailBlock;
import net.minecraftforge.client.model.generators.ModelFile.UncheckedModelFile;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.moddingx.libx.annotation.data.Datagen;
import org.moddingx.libx.datagen.provider.ItemModelProviderBase;
import org.moddingx.libx.mod.ModX;

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
        } else if (item instanceof ItemMobYoinker) {
            this.withExistingParent(id.getPath(), GENERATED)
                    .texture("layer0", new ResourceLocation(id.getNamespace(), "item/" + id.getPath())).override()
                    .predicate(UtilitiX.getInstance().resource("filled"), 1)
                    .model(this.withExistingParent(id.getPath() + "_filled", GENERATED)
                            .texture("layer0", "item/" + id.getPath() + "_filled")).end();
        } else if (item instanceof AncientCompass) {
            for (int i = 0; i < 32; ++i) {
                if (i != 16) {
                    String name = id.getPath() + String.format("_%02d", i);
                    this.withExistingParent(name, GENERATED)
                            .texture("layer0", new ResourceLocation(id.getNamespace(), "item/" + name));
                }
            }
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
        } else if (item.getBlock() == ModBlocks.dimmableRedstoneLamp) {
            this.getBuilder(id.getPath()).parent(new UncheckedModelFile(new ResourceLocation(id.getNamespace(), "block/" + id.getPath() + "_" + 7)));
        } else {
            super.defaultBlock(id, item);
        }
    }
}
