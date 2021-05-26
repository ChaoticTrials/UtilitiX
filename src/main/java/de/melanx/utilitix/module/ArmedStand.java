package de.melanx.utilitix.module;

import de.melanx.utilitix.UtilitiXConfig;
import io.github.noeppi_noeppi.libx.mod.ModX;
import net.minecraft.entity.item.ArmorStandEntity;
import net.minecraft.item.ArmorStandItem;

import javax.annotation.Nonnull;
import java.util.Random;
import java.util.function.Supplier;

public class ArmedStand extends ArmorStandItem {

    public ArmedStand(ModX mod, Properties properties) {
        super(((Supplier<Properties>)() -> {
            if (mod.tab != null) {
                properties.group(mod.tab);
            }
            return properties;
        }).get());
    }

    @Override
    public void applyRandomRotations(@Nonnull ArmorStandEntity entity, @Nonnull Random rand) {
        super.applyRandomRotations(entity, rand);
        entity.setShowArms(true);
        entity.getPersistentData().putBoolean("UtilitiXArmorStand", true);
        entity.getPersistentData().putInt("UtilitiXPoseIdx", 0);
        if (!UtilitiXConfig.armorStandPoses.isEmpty()) {
            UtilitiXConfig.armorStandPoses.get(0).apply(entity);
        }
    }
}
