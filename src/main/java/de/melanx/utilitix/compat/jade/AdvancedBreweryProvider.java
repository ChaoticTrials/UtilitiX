package de.melanx.utilitix.compat.jade;

import de.melanx.utilitix.UtilitiX;
import de.melanx.utilitix.content.brewery.TileAdvancedBrewery;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.IServerDataProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;
import snownee.jade.api.ui.IElementHelper;
import snownee.jade.impl.ui.ElementHelper;

public class AdvancedBreweryProvider implements IBlockComponentProvider, IServerDataProvider<BlockAccessor> {

    public static final ResourceLocation UID = UtilitiX.getInstance().resource("advanced_brewery");
    public static final AdvancedBreweryProvider INSTANCE = new AdvancedBreweryProvider();
    private static final ItemStack BLAZE_POWDER = new ItemStack(Items.BLAZE_POWDER);
    private static final ItemStack CLOCK = new ItemStack(Items.CLOCK);

    @Override
    public ResourceLocation getUid() {
        return UID;
    }

    @Override
    public void appendTooltip(ITooltip tooltip, BlockAccessor accessor, IPluginConfig config) {
        if (!config.get(UtilJade.ADVANCED_BREWERY)) {
            return;
        }

        CompoundTag tag = accessor.getServerData().getCompound("AdvancedBrewery");
        int fuel = tag.getInt("fuel");
        int time = tag.getInt("time");

        IElementHelper helper = tooltip.getElementHelper();

        tooltip.add(helper.smallItem(BLAZE_POWDER));
        tooltip.append(helper.text(Component.translatable(Integer.toString(fuel))));

        if (time > 0 && time != TileAdvancedBrewery.MAX_BREW_TIME) {
            tooltip.append(helper.spacer(5, 0));
            tooltip.append(helper.item(CLOCK, 0.75f));
            tooltip.append(helper.text(Component.translatable("jade.seconds", time / 20)).translate(ElementHelper.SMALL_ITEM_OFFSET));
        }
    }

    @Override
    public void appendServerData(CompoundTag data, BlockAccessor blockAccessor) {
        TileAdvancedBrewery brewery = (TileAdvancedBrewery) blockAccessor.getBlockEntity();
        CompoundTag tag = new CompoundTag();
        tag.putInt("time", TileAdvancedBrewery.MAX_BREW_TIME - brewery.getBrewTime());
        tag.putInt("fuel", brewery.getFuel());
        data.put("AdvancedBrewery", tag);
    }
}
