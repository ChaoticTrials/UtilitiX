package de.melanx.utilitix.compat.jade;

import de.melanx.utilitix.content.experiencecrystal.TileExperienceCrystal;
import de.melanx.utilitix.util.XPUtils;
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

public class ExperienceCrystalProvider implements IComponentProvider, IServerDataProvider<BlockEntity> {

    public static final ExperienceCrystalProvider INSTANCE = new ExperienceCrystalProvider();
    private static final ItemStack XP_BOTTLE = new ItemStack(Items.EXPERIENCE_BOTTLE);

    @Override
    public void appendTooltip(ITooltip tooltip, BlockAccessor accessor, IPluginConfig config) {
        if (!config.get(UtilJade.EXPERIENCE_CRYSTAL)) {
            return;
        }

        int xp = accessor.getServerData().getInt("Xp");
        IElementHelper helper = tooltip.getElementHelper();
        tooltip.add(Jade.smallItem(helper, XP_BOTTLE));
        if (accessor.getServerData().getBoolean("ShowDetails")) {
            tooltip.append(helper.text(new TranslatableComponent("jade.utilitix.experience_crystal.xp")).translate(Jade.SMALL_ITEM_OFFSET));
            tooltip.append(helper.text(new TextComponent(String.valueOf(xp))).translate(Jade.SMALL_ITEM_OFFSET));
        } else {
            tooltip.append(helper.text(new TranslatableComponent("jade.utilitix.experience_crystal.level")).translate(Jade.SMALL_ITEM_OFFSET));
            tooltip.append(helper.text(new TextComponent(XPUtils.getLevelExp(xp).getLeft().toString())).translate(Jade.SMALL_ITEM_OFFSET));
        }
    }

    @Override
    public void appendServerData(CompoundTag data, ServerPlayer player, Level level, BlockEntity blockEntity, boolean showDetails) {
        TileExperienceCrystal crystal = (TileExperienceCrystal) blockEntity;
        data.putInt("Xp", crystal.getXp());
        data.putBoolean("ShowDetails", showDetails);
    }
}
