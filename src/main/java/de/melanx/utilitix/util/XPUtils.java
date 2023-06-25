package de.melanx.utilitix.util;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.material.Fluid;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Set;

public class XPUtils {

    public static final Set<TagKey<Fluid>> XP_FLUID_TAGS = Set.of(
            TagKey.create(Registries.FLUID, new ResourceLocation("forge", "experience")),
            TagKey.create(Registries.FLUID, new ResourceLocation("forge", "xpjuice"))
    );

    public static int getXpBarCap(int level) {
        if (level >= 30) {
            return 112 + ((level - 30) * 9);
        } else if (level >= 15) {
            return 37 + ((level - 15) * 5);
        } else if (level < 0) {
            return 0;
        } else {
            return 7 + (level * 2);
        }
    }

    public static Pair<Integer, Float> getLevelExp(int xpPoints) {
        int level = 0;
        float exp = xpPoints / (float) getXpBarCap(level);
        while (exp >= 1.0F) {
            exp = (exp - 1) * getXpBarCap(level);
            level += 1;
            exp = exp / getXpBarCap(level);
        }
        return Pair.of(level, exp);
    }

    public static int getExpPoints(int level, float exp) {
        int points = 0;
        for (int i = 0; i < level; i++) {
            points += getXpBarCap(i);
        }
        points += Math.round(getXpBarCap(level) * exp);
        return points;
    }
}
