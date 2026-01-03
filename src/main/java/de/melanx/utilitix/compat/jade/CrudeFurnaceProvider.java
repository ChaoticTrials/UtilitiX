package de.melanx.utilitix.compat.jade;

import de.melanx.utilitix.UtilitiX;
import de.melanx.utilitix.content.crudefurnace.CrudeFurnaceBlockEntity;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec2;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.IServerDataProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;
import snownee.jade.api.ui.IElementHelper;

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

        Level level = accessor.getBlockEntity().getLevel();
        if (level == null) {
            return;
        }

        CompoundTag serverData = accessor.getServerData();
        int progress = serverData.getInt("burnTime");
        ListTag items = accessor.getServerData().getList("Items", Tag.TAG_COMPOUND);
        NonNullList<ItemStack> inventory = NonNullList.withSize(3, ItemStack.EMPTY);
        RegistryAccess registryAccess = level.registryAccess();
        for (int i = 0; i < items.size(); i++) {
            inventory.set(i, ItemStack.parseOptional(registryAccess, items.getCompound(i)));
        }

        IElementHelper helper = IElementHelper.get();
        int total = serverData.getInt("maxTime");

        tooltip.add(helper.item(inventory.get(CrudeFurnaceBlockEntity.FUEL_SLOT)));
        tooltip.append(helper.item(inventory.get(CrudeFurnaceBlockEntity.INPUT_SLOT)));
        tooltip.append(helper.spacer(4, 0));
        tooltip.append(helper.progress((float) progress / total).translate(new Vec2(-2.0F, 0.0F)));
        tooltip.append(helper.item(inventory.get(CrudeFurnaceBlockEntity.OUTPUT_SLOT)));
    }

    @Override
    public void appendServerData(CompoundTag data, BlockAccessor accessor) {
        CrudeFurnaceBlockEntity furnace = (CrudeFurnaceBlockEntity) accessor.getBlockEntity();
        ListTag items = new ListTag();
        Level level = furnace.getLevel();
        if (level == null) {
            return;
        }

        for (int i = 0; i < 3; i++) {
            items.add(furnace.getInventory().getStackInSlot(i).saveOptional(level.registryAccess()));
        }

        data.put("Items", items);
        CompoundTag furnaceTag = new CompoundTag();

        furnace.saveAdditional(furnaceTag, level.registryAccess());

        data.put("Inventory", furnaceTag.getCompound("Inventory"));
        data.putInt("burnTime", furnaceTag.getInt("burnTime"));
        data.putInt("maxTime", furnace.getRecipe() != null ? furnace.getRecipe().getBurnTime() : 0);
    }
}
