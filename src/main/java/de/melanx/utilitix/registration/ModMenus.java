package de.melanx.utilitix.registration;


import de.melanx.utilitix.content.track.carts.piston.PistonCartContainerMenu;
import de.melanx.utilitix.content.track.tinkerer.MinecartTinkererMenu;
import net.minecraft.world.inventory.MenuType;
import org.moddingx.libx.annotation.registration.RegisterClass;
import org.moddingx.libx.menu.type.AdvancedMenuType;

@RegisterClass(registry = "MENU")
public class ModMenus {

    public static final AdvancedMenuType<PistonCartContainerMenu, Integer> pistonCart = PistonCartContainerMenu.TYPE;
    public static final MenuType<MinecartTinkererMenu> minecartTinkerer = MinecartTinkererMenu.TYPE;
}
