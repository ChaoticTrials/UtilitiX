package de.melanx.utilitix;

import com.google.common.collect.ImmutableList;
import io.github.noeppi_noeppi.libx.annotation.RegisterConfig;
import io.github.noeppi_noeppi.libx.config.Config;
import io.github.noeppi_noeppi.libx.config.Group;
import net.minecraft.util.ResourceLocation;

import java.util.List;

@RegisterConfig(value = "common")
public class UtilitiXConfig {

    @Group("Config values for the two bells, mob bell and hand bell")
    public static class HandBells {
        @Config(value = "Entity blacklist for mob bell", elementType = ResourceLocation.class)
        public static List<ResourceLocation> blacklist = ImmutableList.of();

        @Config("The time in ticks how long you have to ring the hand bell to let the mobs glow")
        public static int ringTime = 40;

        @Config("The time in ticks how long a mob should glow")
        public static int glowTime = 60;

        @Config("The radius in which entities will glow")
        public static int glowRadius = 36;

        @Config("The radius in which entities get notified that you rung")
        public static int notifyRadius = 24;
    }
}
