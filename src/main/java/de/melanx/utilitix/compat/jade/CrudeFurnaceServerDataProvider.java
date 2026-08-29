package de.melanx.utilitix.compat.jade;

import de.melanx.utilitix.content.crudefurnace.CrudeFurnaceBlockEntity;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.transfer.item.ItemUtil;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IServerDataProvider;

import javax.annotation.Nonnull;

public class CrudeFurnaceServerDataProvider implements IServerDataProvider<BlockAccessor> {

    public static final CrudeFurnaceServerDataProvider INSTANCE = new CrudeFurnaceServerDataProvider();

    @Nonnull
    @Override
    public Identifier getUid() {
        return CrudeFurnaceProvider.UID;
    }

    @Override
    public void appendServerData(@Nonnull CompoundTag data, BlockAccessor accessor) {
        CrudeFurnaceBlockEntity furnace = (CrudeFurnaceBlockEntity) accessor.getBlockEntity();
        ListTag items = new ListTag();
        //noinspection DataFlowIssue
        Level level = furnace.getLevel();
        if (level == null) {
            return;
        }

        HolderLookup.Provider registryAccess = level.registryAccess();
        for (int i = 0; i < 3; i++) {
            ItemStack stack = ItemUtil.getStack(furnace.getInventory(), i);
            items.add(ItemStack.OPTIONAL_CODEC.encodeStart(registryAccess.createSerializationContext(NbtOps.INSTANCE), stack).result().orElse(new CompoundTag()));
        }

        data.put("Items", items);
        data.putInt("burnTime", furnace.getBurnTime());
        data.putInt("maxTime", furnace.getRecipe() != null ? furnace.getRecipe().getBurnTime() : 0);
    }
}
