package de.melanx.utilitix.data;

import de.melanx.utilitix.UtilitiX;
import de.melanx.utilitix.content.AncientCompassItem;
import de.melanx.utilitix.content.bell.BellBase;
import de.melanx.utilitix.content.quiver.QuiverItem;
import de.melanx.utilitix.content.redstone.WeakRedstoneTorchBlock;
import de.melanx.utilitix.item.MobYoinkerItem;
import de.melanx.utilitix.registration.ModBlocks;
import de.melanx.utilitix.registration.ModItems;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.BaseRailBlock;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import org.moddingx.libx.datagen.DatagenContext;
import org.moddingx.libx.datagen.provider.model.ItemModelProviderBase;

public class ItemModelProvider extends ItemModelProviderBase {

    public ItemModelProvider(DatagenContext context) {
        super(context);
    }

    @Override
    protected void setup() {
        this.handheld(ModItems.minecartTinkerer);
        this.manualModel(ModItems.mobBell);
    }

    @Override
    protected void defaultItem(ResourceLocation id, Item item) {
        switch(item) {
            case BellBase bellBase ->
                    super.defaultItem(ResourceLocation.fromNamespaceAndPath(id.getNamespace(), id.getPath() + "_item"), bellBase);
            case MobYoinkerItem mobYoinker -> this.withExistingParent(id.getPath(), GENERATED)
                    .texture("layer0", ResourceLocation.fromNamespaceAndPath(id.getNamespace(), "item/" + id.getPath())).override()
                    .predicate(UtilitiX.getInstance().resource("filled"), 1)
                    .model(this.withExistingParent(id.getPath() + "_filled", GENERATED)
                            .texture("layer0", "item/" + id.getPath() + "_filled")).end();
            case AncientCompassItem ancientCompass -> {
                for (int i = 0; i < 32; ++i) {
                    if (i != 16) {
                        String name = id.getPath() + String.format("_%02d", i);
                        this.withExistingParent(name, GENERATED)
                                .texture("layer0", ResourceLocation.fromNamespaceAndPath(id.getNamespace(), "item/" + name));
                    }
                }
            }
            case QuiverItem quiverItem -> this.withExistingParent(id.getPath(), GENERATED)
                    .texture("layer0", ResourceLocation.fromNamespaceAndPath(id.getNamespace(), "item/" + id.getPath())).override()
                    .predicate(UtilitiX.getInstance().resource("filled"), 1)
                    .model(this.withExistingParent(id.getPath() + "_filled", GENERATED)
                            .texture("layer0", "item/" + id.getPath())
                            .texture("layer1", "item/" + id.getPath() + "_tip")).end();
            case null, default -> super.defaultItem(id, item);
        }
    }

    @Override
    protected void defaultBlock(ResourceLocation id, BlockItem item) {
        if (item.getBlock() == ModBlocks.filterRail || item.getBlock() == ModBlocks.reinforcedFilterRail) {
            this.withExistingParent(id.getPath(), GENERATED).texture("layer0", ResourceLocation.fromNamespaceAndPath(id.getNamespace(), "block/" + id.getPath() + "_right"));
            return;
        }

        if (item.getBlock() instanceof WeakRedstoneTorchBlock || item.getBlock() instanceof BaseRailBlock) {
            this.withExistingParent(id.getPath(), GENERATED).texture("layer0", ResourceLocation.fromNamespaceAndPath(id.getNamespace(), "block/" + id.getPath()));
            return;
        }

        if (item.getBlock() == ModBlocks.linkedRepeater) {
            this.withExistingParent(id.getPath(), GENERATED).texture("layer0", ResourceLocation.fromNamespaceAndPath(id.getNamespace(), "item/" + id.getPath()));
            return;
        }

        if (item.getBlock() == ModBlocks.stoneWall) {
            this.withExistingParent(id.getPath(), WALL_PARENT).texture("wall", ResourceLocation.withDefaultNamespace("block/stone"));
            return;
        }

        if (item.getBlock() == ModBlocks.dimmableRedstoneLamp) {
            this.getBuilder(id.getPath()).parent(new ModelFile.UncheckedModelFile(ResourceLocation.fromNamespaceAndPath(id.getNamespace(), "block/" + id.getPath() + "_" + 7)));
            return;
        }

        super.defaultBlock(id, item);
    }
}
