package de.melanx.utilitix.registration;

import de.melanx.utilitix.content.track.carts.*;
import org.moddingx.libx.annotation.registration.RegisterClass;

@RegisterClass(registry = "")
public class ModRegisterables {

    public static final BaseCart.CartType<EnderCart> enderCart = BaseCart.type("ender_cart", EnderCart::new);
    public static final BaseCart.CartType<PistonCart> pistonCart = BaseCart.type("piston_cart", PistonCart::new);
    public static final BaseCart.CartType<StonecutterCart> stonecutterCart = BaseCart.type("stonecutter_cart", StonecutterCart::new);
    public static final BaseCart.CartType<AnvilCart> anvilCart = BaseCart.type("anvil_cart", AnvilCart::new);
}
