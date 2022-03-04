package de.melanx.utilitix.compat.jade;

import de.melanx.utilitix.content.brewery.TileAdvancedBrewery;
import mcp.mobius.waila.api.BlockAccessor;
import mcp.mobius.waila.api.IComponentProvider;
import mcp.mobius.waila.api.IServerDataProvider;
import mcp.mobius.waila.api.ITooltip;
import mcp.mobius.waila.api.config.IPluginConfig;
import mcp.mobius.waila.api.ui.IElementHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import snownee.jade.Jade;

public class AdvancedBreweryProvider implements IComponentProvider, IServerDataProvider<BlockEntity> {

    public static final AdvancedBreweryProvider INSTANCE = new AdvancedBreweryProvider();
    private static final ItemStack BLAZE_POWDER = new ItemStack(Items.BLAZE_POWDER);
    private static final ItemStack CLOCK = new ItemStack(Items.CLOCK);

    @Override
    public void appendTooltip(ITooltip tooltip, BlockAccessor accessor, IPluginConfig config) {
        if (!config.get(UtilJade.ADVANCED_BREWERY)) {
            return;
        }

        CompoundTag tag = accessor.getServerData().getCompound("AdvancedBrewery");
        int fuel = tag.getInt("fuel");
        int time = tag.getInt("time");

        IElementHelper helper = tooltip.getElementHelper();
        tooltip.add(Jade.smallItem(helper, BLAZE_POWDER));
        tooltip.append(helper.text(new TextComponent(Integer.toString(fuel))));

        if (time > 0 && time != TileAdvancedBrewery.MAX_BREW_TIME) {
            tooltip.append(helper.spacer(5, 0));
            tooltip.append(helper.item(CLOCK, 0.75f));
            tooltip.append(helper.text(new TranslatableComponent("jade.seconds", time / 20)).translate(Jade.SMALL_ITEM_OFFSET));
        }
    }

    @Override
    public void appendServerData(CompoundTag data, ServerPlayer player, Level level, BlockEntity blockEntity, boolean showDetails) {
        TileAdvancedBrewery brewery = (TileAdvancedBrewery) blockEntity;
        CompoundTag tag = new CompoundTag();
        tag.putInt("time", TileAdvancedBrewery.MAX_BREW_TIME - brewery.getBrewTime());
        tag.putInt("fuel", brewery.getFuel());
        data.put("AdvancedBrewery", tag);
    }
}
