package de.melanx.utilitix.registration;

import de.melanx.utilitix.UtilitiX;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.registries.ForgeRegistries;
import org.moddingx.libx.annotation.registration.RegisterClass;

@RegisterClass(registry = "CREATIVE_MODE_TAB")
public class ModCreativeTab {

    public static final CreativeModeTab utilitixTab = CreativeModeTab.builder()
            .title(Component.literal("UtilitiX"))
            .icon(() -> new ItemStack(ModItems.mobBell))
            .build();

    public static void onCreateTabs(BuildCreativeModeTabContentsEvent event) {
        for (Item item : ForgeRegistries.ITEMS.getValues()) {
            //noinspection DataFlowIssue
            if (UtilitiX.getInstance().modid.equals(ForgeRegistries.ITEMS.getKey(item).getNamespace())) {
                if (event.getTabKey() == ModCreativeTab.getTabForItem(item)) {
                    event.accept(item);
                }
            }
        }
    }

    private static ResourceKey<CreativeModeTab> getTabForItem(Item item) {
        if (item == ModItems.quiver) {
            return null;
        }

        if (item == ModBlocks.stoneWall.asItem()) {
            return CreativeModeTabs.BUILDING_BLOCKS;
        }

        return ResourceKey.create(Registries.CREATIVE_MODE_TAB, UtilitiX.getInstance().resource("utilitix_tab"));
    }
}
