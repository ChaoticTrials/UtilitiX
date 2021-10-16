package de.melanx.utilitix.compat.jade;

import de.melanx.utilitix.content.crudefurnace.TileCrudeFurnace;
import mcp.mobius.waila.api.BlockAccessor;
import mcp.mobius.waila.api.IComponentProvider;
import mcp.mobius.waila.api.IServerDataProvider;
import mcp.mobius.waila.api.ITooltip;
import mcp.mobius.waila.api.config.IPluginConfig;
import mcp.mobius.waila.api.ui.IElementHelper;
import mcp.mobius.waila.impl.ui.ProgressArrowElement;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

public class CrudeFurnaceProvider implements IComponentProvider, IServerDataProvider<BlockEntity> {

    public static final CrudeFurnaceProvider INSTANCE = new CrudeFurnaceProvider();

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
    public void appendServerData(CompoundTag data, ServerPlayer player, Level level, BlockEntity blockEntity, boolean showDetails) {
        TileCrudeFurnace furnace = (TileCrudeFurnace) blockEntity;
        ListTag items = new ListTag();
        for (int i = 0; i < 3; i++) {
            items.add(furnace.getInventory().getStackInSlot(i).serializeNBT());
        }

        data.put("Items", items);
        CompoundTag furnaceTag = furnace.save(new CompoundTag());
        data.putInt("burnTime", furnaceTag.getInt("burnTime"));
        data.putInt("maxTime", furnace.getRecipe() != null ? furnace.getRecipe().getBurnTime() : 0);
    }
}
