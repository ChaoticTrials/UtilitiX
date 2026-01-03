package de.melanx.utilitix.registration;


import de.melanx.utilitix.content.quiver.QuiverMenu;
import de.melanx.utilitix.content.track.carts.piston.PistonCartMenu;
import de.melanx.utilitix.content.track.tinkerer.MinecartTinkererMenu;
import net.minecraft.world.inventory.MenuType;
import org.moddingx.libx.annotation.registration.RegisterClass;
import org.moddingx.libx.menu.type.AdvancedMenuType;

@RegisterClass(registry = "MENU")
public class ModMenus {

    public static final AdvancedMenuType<PistonCartMenu, Integer> pistonCart = PistonCartMenu.TYPE;
    public static final MenuType<MinecartTinkererMenu> minecartTinkerer = MinecartTinkererMenu.TYPE;
    public static final MenuType<QuiverMenu> quiver = QuiverMenu.TYPE;
}
