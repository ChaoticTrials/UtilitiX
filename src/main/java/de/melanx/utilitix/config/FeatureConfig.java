package de.melanx.utilitix.config;

import org.moddingx.libx.annotation.config.RegisterConfig;
import org.moddingx.libx.config.Config;
import org.moddingx.libx.config.Group;

@RegisterConfig(value = "features")
public class FeatureConfig {

    private static final String MAIN_MESSAGE = "If you disable this, the following content is gone:";

    @Group("Various items")
    public static class Items {

        @Config({
                FeatureConfig.MAIN_MESSAGE,
                "- Ancient Compass"
        })
        public static boolean ancientCompass = true;

        @Config({
                FeatureConfig.MAIN_MESSAGE,
                "- Diamond Shears"
        })
        public static boolean diamondShears = true;

        @Config({
                FeatureConfig.MAIN_MESSAGE,
                "- Hand Bell",
                "- Mob Bell"
        })
        public static boolean handBells = true;

        @Config({
                FeatureConfig.MAIN_MESSAGE,
                "- Mob Yoinker"
        })
        public static boolean mobYoinker = true;

        @Config({
                FeatureConfig.MAIN_MESSAGE,
                "- Tiny Coal",
                "- Tiny Char Coal"
        })
        public static boolean tinyCoal = true;
    }

    @Group("All blocks that you can interact with")
    public static class Machines {

        @Config({
                FeatureConfig.MAIN_MESSAGE,
                "- Advanced Brewery",
                "- Failed Potion"
        })
        public static boolean advancedBrewery = true;

        @Config({
                FeatureConfig.MAIN_MESSAGE,
                "- Crude Furnace"
        })
        public static boolean crudeFurnace = true;

        @Config({
                FeatureConfig.MAIN_MESSAGE,
                "- Experience Crystal"
        })
        public static boolean experienceCrystal = true;
    }

    @Group("Various content in different sub-categories")
    public static class Misc {

        @Group("All the changes that change in world vanilla behavior")
        public static class InWorldChanges {

            @Config("Items in world which have mending collect xp orbs to get repaired")
            public static boolean betterMending = true;

            @Config("Prevents waterlogging when holding the sneak key")
            public static boolean crouchNoWaterlog = true;

            @Config("Both doors open at the same time if connected")
            public static boolean doubleDoor = true;

            @Config({
                    FeatureConfig.MAIN_MESSAGE,
                    "- Gilding using a Gilding Crystal"
            })
            public static boolean gilding = true;

            @Config({
                    FeatureConfig.MAIN_MESSAGE,
                    "- Glue Ball"
            })
            public static boolean glue = true;

            @Config("When a block from #minecraft:saplings or #minecraft:crops despawns, it tries to plant it instead")
            public static boolean plantsOnDespawn = true;

            @Config({
                    FeatureConfig.MAIN_MESSAGE,
                    "- Extra time for Wandering Traders in World",
                    "- More Wandering Traders per World"
            })
            public static boolean wanderingTrader = true;
        }

        @Group("All the content related to redstone")
        public static class Redstone {

            @Config({
                    FeatureConfig.MAIN_MESSAGE,
                    "- Comparator Redirector Up",
                    "- Comparator Redirector Down"
            })
            public static boolean comparatorRedirector = true;

            @Config({
                    FeatureConfig.MAIN_MESSAGE,
                    "- Dimmable Redstone Lamp"
            })
            public static boolean dimmableLamps = true;

            @Config({
                    FeatureConfig.MAIN_MESSAGE,
                    "- Weak Redstone Torch"
            })
            public static boolean weakRedstoneTorch = true;

            @Config({
                    FeatureConfig.MAIN_MESSAGE,
                    "- Linked Repeater",
                    "- Redstone Crystal"
            })
            public static boolean wirelessRedstone = true;
        }

        @Config({
                FeatureConfig.MAIN_MESSAGE,
                "- Armed Stand"
        })
        public static boolean armedStand = true;

        @Config({
                FeatureConfig.MAIN_MESSAGE,
                "- Stone Wall"
        })
        public static boolean decoration = true;
    }

    @Group("Items, blocks, and entities used for transportation")
    public static class Transportation {

        @Config({
                FeatureConfig.MAIN_MESSAGE,
                "- Anvil Cart",
                "- Ender Cart",
                "- Piston Cart",
                "- Stonecutter Cart",
                "If you also disable \"moreRails\", this item is gone:",
                "- Minecart Tinkerer"
        })
        public static boolean moreMinecarts = true;

        @Config({
                FeatureConfig.MAIN_MESSAGE,
                "- Crossing Rail",
                "- Directional Rail",
                "- Filter Rail",
                "- Highspeed Rail",
                "- Piston Minecart Controller Rail",
                "- Directional Highspeed Rail",
                "- Reinforced Rail",
                "- Reinforced Crossing Rail",
                "- Reinforced Filter Rail",
                "- Reinforced Piston Minecart Controller Rail",
                "If you also disable \"moreMinecarts\", this item is gone:",
                "- Minecart Tinkerer"
        })
        public static boolean moreRails = true;

        @Config({
                FeatureConfig.MAIN_MESSAGE,
                "- All Shulker Boat variants"
        })
        public static boolean shulkerBoats = true;
    }
}
