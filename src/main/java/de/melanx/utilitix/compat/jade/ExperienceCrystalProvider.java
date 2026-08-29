package de.melanx.utilitix.compat.jade;

import de.melanx.utilitix.UtilitiX;
import de.melanx.utilitix.util.XPUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;
import snownee.jade.api.ui.JadeUI;

import javax.annotation.Nonnull;

public class ExperienceCrystalProvider implements IBlockComponentProvider {

    public static final Identifier UID = UtilitiX.getInstance().id("experience_crystal");
    public static final ExperienceCrystalProvider INSTANCE = new ExperienceCrystalProvider();

    private static ItemStack xpBottle;

    private static ItemStack xpBottle() {
        if (xpBottle == null) {
            xpBottle = new ItemStack(Items.EXPERIENCE_BOTTLE);
        }

        return xpBottle;
    }

    @Nonnull
    @Override
    public Identifier getUid() {
        return UID;
    }

    @Override
    public void appendTooltip(@Nonnull ITooltip tooltip, @Nonnull BlockAccessor accessor, IPluginConfig config) {
        if (!config.get(UtilJade.EXPERIENCE_CRYSTAL)) {
            return;
        }

        int xp = accessor.getServerData().getIntOr("Xp", 0);
        tooltip.add(JadeUI.smallItem(xpBottle()));
        if (accessor.getServerData().getBooleanOr("ShowDetails", false)) {
            tooltip.append(JadeUI.text(Component.translatable("jade.utilitix.experience_crystal.xp")).offset(0, -1));
            tooltip.append(JadeUI.text(Component.literal(String.valueOf(xp))).offset(0, -1));
        } else {
            tooltip.append(JadeUI.text(Component.translatable("jade.utilitix.experience_crystal.level")).offset(0, -1));
            tooltip.append(JadeUI.text(Component.literal(XPUtils.getLevelExp(xp).getLeft().toString())).offset(0, -1));
        }
    }
}
