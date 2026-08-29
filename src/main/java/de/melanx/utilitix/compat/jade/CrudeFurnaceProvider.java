package de.melanx.utilitix.compat.jade;

import de.melanx.utilitix.UtilitiX;
import de.melanx.utilitix.content.crudefurnace.CrudeFurnaceBlockEntity;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;
import snownee.jade.api.ui.JadeUI;

import javax.annotation.Nonnull;

public class CrudeFurnaceProvider implements IBlockComponentProvider {

    public static final Identifier UID = UtilitiX.getInstance().id("crude_furnace");
    public static final CrudeFurnaceProvider INSTANCE = new CrudeFurnaceProvider();

    @Nonnull
    @Override
    public Identifier getUid() {
        return UID;
    }

    @Override
    public void appendTooltip(@Nonnull ITooltip tooltip, @Nonnull BlockAccessor accessor, IPluginConfig config) {
        if (!config.get(UtilJade.CRUDE_FURNACE)) {
            return;
        }

        //noinspection DataFlowIssue
        Level level = accessor.getBlockEntity().getLevel();
        if (level == null) {
            return;
        }

        CompoundTag serverData = accessor.getServerData();
        int progress = serverData.getIntOr("burnTime", 0);
        ListTag items = accessor.getServerData().getListOrEmpty("Items");
        NonNullList<ItemStack> inventory = NonNullList.withSize(3, ItemStack.EMPTY);
        HolderLookup.Provider registryAccess = level.registryAccess();
        for (int i = 0; i < items.size(); i++) {
            inventory.set(i, ItemStack.OPTIONAL_CODEC.parse(registryAccess.createSerializationContext(NbtOps.INSTANCE), items.get(i)).result().orElse(ItemStack.EMPTY));
        }

        int total = serverData.getIntOr("maxTime", 0);


        ItemStack fuel = inventory.get(CrudeFurnaceBlockEntity.FUEL_SLOT);
        ItemStack input = inventory.get(CrudeFurnaceBlockEntity.INPUT_SLOT);
        ItemStack output = inventory.get(CrudeFurnaceBlockEntity.OUTPUT_SLOT);

        if (fuel.isEmpty() && input.isEmpty() && output.isEmpty()) {
            return;
        }

        tooltip.add(JadeUI.item(fuel));
        tooltip.append(JadeUI.item(input));
        tooltip.append(JadeUI.spacer(4, 0));
        tooltip.append(total > 0 ? JadeUI.progressArrow((float) progress / total).offset(-2, 0) : JadeUI.progressArrow(0).offset(-2, 0));
        tooltip.append(JadeUI.item(output));
    }
}
