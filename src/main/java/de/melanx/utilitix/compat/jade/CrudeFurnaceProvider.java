package de.melanx.utilitix.compat.jade;

import de.melanx.utilitix.UtilitiX;
import de.melanx.utilitix.content.crudefurnace.TileCrudeFurnace;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.IServerDataProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;
import snownee.jade.api.ui.IElementHelper;
import snownee.jade.impl.ui.ProgressArrowElement;

public class CrudeFurnaceProvider implements IBlockComponentProvider, IServerDataProvider<BlockAccessor> {

    public static final ResourceLocation UID = UtilitiX.getInstance().resource("crude_furnace");
    public static final CrudeFurnaceProvider INSTANCE = new CrudeFurnaceProvider();

    @Override
    public ResourceLocation getUid() {
        return UID;
    }

    @Override
    public void appendTooltip(ITooltip tooltip, BlockAccessor accessor, IPluginConfig config) {
        if (!config.get(UtilJade.CRUDE_FURNACE)) {
            return;
        }

        int progress = accessor.getServerData().getInt("burnTime");
        if (progress == 0) {
            return;
        }

        ListTag items = accessor.getServerData().getList("Items", Tag.TAG_COMPOUND);
        NonNullList<ItemStack> inventory = NonNullList.withSize(3, ItemStack.EMPTY);
        for (int i = 0; i < items.size(); i++) {
            inventory.set(i, ItemStack.of(items.getCompound(i)));
        }

        IElementHelper helper = tooltip.getElementHelper();
        int total = accessor.getServerData().getInt("maxTime");

        tooltip.add(helper.item(inventory.get(0)));
        tooltip.append(helper.item(inventory.get(1)));
        tooltip.append(new ProgressArrowElement((float) progress / total));
        tooltip.append(helper.item(inventory.get(2)));
    }

    @Override
    public void appendServerData(CompoundTag data, BlockAccessor accessor) {
        TileCrudeFurnace furnace = (TileCrudeFurnace) accessor.getBlockEntity();
        ListTag items = new ListTag();
        for (int i = 0; i < 3; i++) {
            items.add(furnace.getInventory().getStackInSlot(i).serializeNBT());
        }

        data.put("Items", items);
        CompoundTag furnaceTag = new CompoundTag();
        furnace.saveAdditional(furnaceTag);
        data.putInt("burnTime", furnaceTag.getInt("burnTime"));
        data.putInt("maxTime", furnace.getRecipe() != null ? furnace.getRecipe().getBurnTime() : 0);
    }
}
