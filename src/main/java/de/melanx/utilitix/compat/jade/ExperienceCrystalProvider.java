package de.melanx.utilitix.compat.jade;

import de.melanx.utilitix.UtilitiX;
import de.melanx.utilitix.content.experiencecrystal.TileExperienceCrystal;
import de.melanx.utilitix.util.XPUtils;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import snownee.jade.Jade;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.IServerDataProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;
import snownee.jade.api.ui.IElementHelper;

public class ExperienceCrystalProvider implements IBlockComponentProvider, IServerDataProvider<BlockEntity> {

    public static final ResourceLocation UID = UtilitiX.getInstance().resource("experience_crystal");
    public static final ExperienceCrystalProvider INSTANCE = new ExperienceCrystalProvider();
    private static final ItemStack XP_BOTTLE = new ItemStack(Items.EXPERIENCE_BOTTLE);

    @Override
    public ResourceLocation getUid() {
        return UID;
    }

    @Override
    public void appendTooltip(ITooltip tooltip, BlockAccessor accessor, IPluginConfig config) {
        if (!config.get(UtilJade.EXPERIENCE_CRYSTAL)) {
            return;
        }

        int xp = accessor.getServerData().getInt("Xp");
        IElementHelper helper = tooltip.getElementHelper();
        tooltip.add(Jade.smallItem(helper, XP_BOTTLE));
        if (accessor.getServerData().getBoolean("ShowDetails")) {
            tooltip.append(helper.text(Component.translatable("jade.utilitix.experience_crystal.xp")).translate(Jade.SMALL_ITEM_OFFSET));
            tooltip.append(helper.text(Component.literal(String.valueOf(xp))).translate(Jade.SMALL_ITEM_OFFSET));
        } else {
            tooltip.append(helper.text(Component.translatable("jade.utilitix.experience_crystal.level")).translate(Jade.SMALL_ITEM_OFFSET));
            tooltip.append(helper.text(Component.literal(XPUtils.getLevelExp(xp).getLeft().toString())).translate(Jade.SMALL_ITEM_OFFSET));
        }
    }

    @Override
    public void appendServerData(CompoundTag data, ServerPlayer player, Level level, BlockEntity blockEntity, boolean showDetails) {
        TileExperienceCrystal crystal = (TileExperienceCrystal) blockEntity;
        data.putInt("Xp", crystal.getXp());
        data.putBoolean("ShowDetails", showDetails);
    }
}
