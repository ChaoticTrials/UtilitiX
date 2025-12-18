package de.melanx.utilitix.registration;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import org.moddingx.libx.annotation.registration.RegisterClass;
import org.moddingx.libx.creativetab.CreativeTabX;
import org.moddingx.libx.mod.ModX;

@RegisterClass(registry = "CREATIVE_MODE_TAB")
public class ModCreativeTab extends CreativeTabX {

    public ModCreativeTab(ModX mod) {
        super(mod);
    }

    @Override
    protected void addItems(TabContext ctx) {
        this.addModItems(ctx);
    }

    @Override
    protected void buildTab(CreativeModeTab.Builder builder) {
        builder.title(Component.literal("UtilitiX"));
        builder.icon(() -> new ItemStack(ModItems.mobBell));
    }
}
