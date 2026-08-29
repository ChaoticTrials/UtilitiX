package de.melanx.utilitix.compat.jade;

import de.melanx.utilitix.content.brewery.AdvancedBreweryBlockEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;
import snownee.jade.api.theme.IThemeHelper;
import snownee.jade.api.ui.JadeUI;

import javax.annotation.Nonnull;

public class AdvancedBreweryProvider implements IBlockComponentProvider {

    public static final AdvancedBreweryProvider INSTANCE = new AdvancedBreweryProvider();

    private static ItemStack blazePowder;
    private static ItemStack clock;

    private static ItemStack blazePowder() {
        if (blazePowder == null) {
            blazePowder = new ItemStack(Items.BLAZE_POWDER);
        }

        return blazePowder;
    }

    private static ItemStack clock() {
        if (clock == null) {
            clock = new ItemStack(Items.CLOCK);
        }

        return clock;
    }

    @Nonnull
    @Override
    public Identifier getUid() {
        return UtilJade.ADVANCED_BREWERY;
    }

    @Override
    public void appendTooltip(@Nonnull ITooltip tooltip, @Nonnull BlockAccessor accessor, IPluginConfig config) {
        if (!config.get(UtilJade.ADVANCED_BREWERY)) {
            return;
        }

        CompoundTag tag = accessor.getServerData().getCompoundOrEmpty("AdvancedBrewery");
        int fuel = tag.getIntOr("fuel", 0);
        int time = tag.getIntOr("time", 0);

        tooltip.add(JadeUI.smallItem(blazePowder()));
        tooltip.append(JadeUI.text(Component.translatable(Integer.toString(fuel))));

        if (time > 0 && time != AdvancedBreweryBlockEntity.MAX_BREW_TIME) {
            tooltip.append(JadeUI.spacer(5, 0));
            tooltip.append(JadeUI.smallItem(clock()).narration(""));
            tooltip.append(IThemeHelper.get().seconds(time, accessor.tickRate()));
        }
    }
}
