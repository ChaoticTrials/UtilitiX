package de.melanx.utilitix.registration;

import de.melanx.utilitix.UtilitiX;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.moddingx.libx.annotation.registration.RegisterClass;
import org.moddingx.libx.creativetab.CreativeTabX;
import org.moddingx.libx.mod.ModX;

@RegisterClass(registry = "CREATIVE_MODE_TAB")
public class ModCreativeTab extends CreativeTabX {

    public ModCreativeTab(ModX mod) {
        super(mod);
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

    @Override
    protected void addItems(TabContext ctx) {
        this.addModItems(ctx); // todo use getTabForItem
    }

    @Override
    protected void buildTab(CreativeModeTab.Builder builder) {
        builder.title(Component.literal("UtilitiX"));
        builder.icon(() -> new ItemStack(ModItems.mobBell));
    }
}
