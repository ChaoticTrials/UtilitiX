package de.melanx.utilitix.data;

import de.melanx.utilitix.client.AncientCompassAngleProperty;
import de.melanx.utilitix.content.AncientCompassItem;
import de.melanx.utilitix.content.MobYoinkerItem;
import de.melanx.utilitix.content.backpack.BackpackItem;
import de.melanx.utilitix.content.bell.BellRenderer;
import de.melanx.utilitix.content.bell.HandBellItem;
import de.melanx.utilitix.content.bell.MobBellItem;
import de.melanx.utilitix.content.bell.MobBellTintSource;
import de.melanx.utilitix.content.quiver.QuiverItem;
import de.melanx.utilitix.content.redstone.WeakRedstoneTorchBlock;
import de.melanx.utilitix.registration.ModBlocks;
import de.melanx.utilitix.registration.ModDataComponentTypes;
import de.melanx.utilitix.registration.ModItems;
import net.minecraft.client.color.item.Dye;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.model.*;
import net.minecraft.client.renderer.item.ItemModel;
import net.minecraft.client.renderer.item.RangeSelectItemModel;
import net.minecraft.client.renderer.item.properties.select.ComponentContents;
import net.minecraft.client.renderer.item.properties.select.DisplayContext;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.level.block.BaseRailBlock;
import org.moddingx.libx.datagen.DatagenContext;
import org.moddingx.libx.datagen.provider.model.ItemModelProviderBase;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

public class ItemModelProvider extends ItemModelProviderBase {

    public ItemModelProvider(DatagenContext context) {
        super(context);
    }

    @Override
    protected void setup() {
        this.handheld(ModItems.minecartTinkerer);
    }

    @Override
    protected void defaultItem(Item item, ItemModelGenerators itemModels) {
        Identifier id = BuiltInRegistries.ITEM.getKey(item);
        switch(item) {
            case BackpackItem backpackItem -> {
                Identifier model = itemModels.createFlatItemModel(backpackItem, ModelTemplates.FLAT_ITEM);
                itemModels.itemModelOutput.accept(backpackItem, ItemModelUtils.tintedModel(model, new Dye(0XFFA06540)));
            }
            case HandBellItem handBellItem -> {
                Identifier flatModel = itemModels.createFlatItemModel(handBellItem, "_item", ModelTemplates.FLAT_ITEM);
                Identifier specialModel = Identifier.fromNamespaceAndPath(id.getNamespace(), "item/" + id.getPath());
                itemModels.itemModelOutput.accept(item, ItemModelUtils.select(
                        new DisplayContext(),
                        ItemModelUtils.specialModel(specialModel, new BellRenderer.Unbaked()),
                        ItemModelUtils.when(ItemDisplayContext.GUI, ItemModelUtils.plainModel(flatModel))
                ));
            }
            case MobBellItem mobBellItem -> {
                TextureMapping flatMapping = new TextureMapping()
                        .put(TextureSlot.LAYER0, this.material(Identifier.fromNamespaceAndPath(id.getNamespace(), "item/" + id.getPath() + "_stick")))
                        .put(TextureSlot.LAYER1, this.material(Identifier.fromNamespaceAndPath(id.getNamespace(), "item/" + id.getPath() + "_bell")));
                Identifier flatModelTarget = Identifier.fromNamespaceAndPath(id.getNamespace(), "item/" + id.getPath() + "_item");
                Identifier flatModel = ModelTemplates.TWO_LAYERED_ITEM.create(flatModelTarget, flatMapping, itemModels.modelOutput);
                Identifier specialModel = Identifier.fromNamespaceAndPath(id.getNamespace(), "item/" + id.getPath());
                itemModels.itemModelOutput.accept(item, ItemModelUtils.select(
                        new DisplayContext(),
                        ItemModelUtils.specialModel(specialModel, new BellRenderer.Unbaked()),
                        ItemModelUtils.when(ItemDisplayContext.GUI, ItemModelUtils.tintedModel(flatModel,
                                ItemModelUtils.constantTint(0xFFFFFF), new MobBellTintSource()))
                ));
            }
            case MobYoinkerItem mobYoinker -> {
                Identifier emptyModel = itemModels.createFlatItemModel(mobYoinker, ModelTemplates.FLAT_ITEM);
                Identifier filledModel = itemModels.createFlatItemModel(mobYoinker, "_filled", ModelTemplates.FLAT_ITEM);
                itemModels.itemModelOutput.accept(mobYoinker, ItemModelUtils.select(
                        new ComponentContents<>(ModDataComponentTypes.filled),
                        ItemModelUtils.plainModel(emptyModel),
                        ItemModelUtils.when(true, ItemModelUtils.plainModel(filledModel))
                ));
            }
            case AncientCompassItem ancientCompass -> {
                List<RangeSelectItemModel.Entry> overrides = new ArrayList<>();
                ItemModel.Unbaked base = ItemModelUtils.plainModel(itemModels.createFlatItemModel(ancientCompass, "_16", ModelTemplates.FLAT_ITEM));
                overrides.add(ItemModelUtils.override(base, 0.0F));
                for (int i = 1; i < 32; i++) {
                    int textureIndex = Mth.positiveModulo(i - 16, 32);
                    ItemModel.Unbaked frame = ItemModelUtils.plainModel(itemModels.createFlatItemModel(ancientCompass, String.format(Locale.ROOT, "_%02d", textureIndex), ModelTemplates.FLAT_ITEM));
                    overrides.add(ItemModelUtils.override(frame, i - 0.5F));
                }
                overrides.add(ItemModelUtils.override(base, 31.5F));

                itemModels.itemModelOutput.accept(ancientCompass, ItemModelUtils.rangeSelect(new AncientCompassAngleProperty(true), 32.0F, overrides));
            }
            case QuiverItem quiverItem -> {
                Identifier emptyModel = itemModels.createFlatItemModel(quiverItem, ModelTemplates.FLAT_ITEM);
                TextureMapping filledMapping = new TextureMapping()
                        .put(TextureSlot.LAYER0, this.material(Identifier.fromNamespaceAndPath(id.getNamespace(), "item/" + id.getPath())))
                        .put(TextureSlot.LAYER1, this.material(Identifier.fromNamespaceAndPath(id.getNamespace(), "item/" + id.getPath() + "_tip")));
                Identifier filledModelTarget = Identifier.fromNamespaceAndPath(id.getNamespace(), "item/" + id.getPath() + "_filled");
                Identifier filledModel = ModelTemplates.TWO_LAYERED_ITEM.create(filledModelTarget, filledMapping, itemModels.modelOutput);
                itemModels.itemModelOutput.accept(quiverItem, ItemModelUtils.select(
                        new ComponentContents<>(ModDataComponentTypes.filled),
                        ItemModelUtils.plainModel(emptyModel),
                        ItemModelUtils.when(true, ItemModelUtils.plainModel(filledModel))
                ));
            }
            default -> super.defaultItem(item, itemModels);
        }
    }

    @Override
    protected void defaultBlock(Identifier id, BlockItem item, ItemModelGenerators itemModels) {
        if (item.getBlock() == ModBlocks.filterRail || item.getBlock() == ModBlocks.reinforcedFilterRail) {
            Identifier model = this.createItemModel(id, GENERATED, TextureMapping.layer0(this.material(Identifier.fromNamespaceAndPath(id.getNamespace(), "block/" + id.getPath() + "_right"))), TextureSlot.LAYER0, itemModels);
            itemModels.itemModelOutput.accept(item, ItemModelUtils.plainModel(model));
            return;
        }

        if (item.getBlock() instanceof WeakRedstoneTorchBlock || item.getBlock() instanceof BaseRailBlock) {
            Identifier model = this.createItemModel(id, GENERATED, TextureMapping.layer0(this.material(Identifier.fromNamespaceAndPath(id.getNamespace(), "block/" + id.getPath()))), TextureSlot.LAYER0, itemModels);
            itemModels.itemModelOutput.accept(item, ItemModelUtils.plainModel(model));
            return;
        }

        if (item.getBlock() == ModBlocks.linkedRepeater) {
            Identifier model = this.createItemModel(id, GENERATED, TextureMapping.layer0(this.material(Identifier.fromNamespaceAndPath(id.getNamespace(), "item/" + id.getPath()))), TextureSlot.LAYER0, itemModels);
            itemModels.itemModelOutput.accept(item, ItemModelUtils.plainModel(model));
            return;
        }

        if (item.getBlock() == ModBlocks.stoneWall) {
            Identifier model = this.createItemModel(id, WALL_PARENT, TextureMapping.singleSlot(TextureSlot.WALL, this.material(Identifier.withDefaultNamespace("block/stone"))), TextureSlot.WALL, itemModels);
            itemModels.itemModelOutput.accept(item, ItemModelUtils.plainModel(model));
            return;
        }

        if (item.getBlock() == ModBlocks.dimmableRedstoneLamp) {
            ModelTemplate template = new ModelTemplate(Optional.of(Identifier.fromNamespaceAndPath(id.getNamespace(), "block/" + id.getPath() + "_7")), Optional.empty());
            Identifier model = template.create(Identifier.fromNamespaceAndPath(id.getNamespace(), "item/" + id.getPath()), new TextureMapping(), itemModels.modelOutput);
            itemModels.itemModelOutput.accept(item, ItemModelUtils.plainModel(model));
            return;
        }

        super.defaultBlock(id, item, itemModels);
    }

    private Identifier createItemModel(Identifier id, Identifier parent, TextureMapping texture, TextureSlot requiredSlot, ItemModelGenerators itemModels) {
        ModelTemplate template = new ModelTemplate(Optional.of(parent), Optional.empty(), requiredSlot);
        Identifier model = Identifier.fromNamespaceAndPath(id.getNamespace(), "item/" + id.getPath());

        return template.create(model, texture, itemModels.modelOutput);
    }
}
