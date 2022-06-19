package de.melanx.utilitix.content;

import de.melanx.utilitix.UtilitiXConfig;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.item.ArmorStandItem;
import org.moddingx.libx.mod.ModX;

import javax.annotation.Nonnull;
import java.util.function.Supplier;

public class ArmedStand extends ArmorStandItem {

    public ArmedStand(ModX mod, Properties properties) {
        super(((Supplier<Properties>)() -> {
            if (mod.tab != null) {
                properties.tab(mod.tab);
            }
            return properties;
        }).get());
    }


    @Override
    public void randomizePose(@Nonnull ArmorStand armorStand, @Nonnull RandomSource rand) {
        super.randomizePose(armorStand, rand);
        armorStand.setShowArms(true);
        armorStand.getPersistentData().putBoolean("UtilitiXArmorStand", true);
        armorStand.getPersistentData().putInt("UtilitiXPoseIdx", 0);
        if (!UtilitiXConfig.armorStandPoses.isEmpty()) {
            UtilitiXConfig.armorStandPoses.get(0).apply(armorStand);
        }
    }
}
