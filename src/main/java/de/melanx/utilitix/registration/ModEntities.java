package de.melanx.utilitix.registration;

import de.melanx.utilitix.content.track.carts.*;
import io.github.noeppi_noeppi.libx.annotation.RegisterClass;

@RegisterClass
public class ModEntities {

    public static final EntityCart.CartType<EntityEnderCart> enderCart = EntityCart.type("ender_cart", EntityEnderCart::new);
    public static final EntityCart.CartType<EntityPistonCart> pistonCart = EntityCart.type("piston_cart", EntityPistonCart::new);
    public static final EntityCart.CartType<EntityStonecutterCart> stonecutterCart = EntityCart.type("stonecutter_cart", EntityStonecutterCart::new);
    public static final EntityCart.CartType<EntityAnvilCart> anvilCart = EntityCart.type("anvil_cart", EntityAnvilCart::new);
}
