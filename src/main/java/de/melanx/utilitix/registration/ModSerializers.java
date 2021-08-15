package de.melanx.utilitix.registration;

import de.melanx.utilitix.content.track.carts.piston.PistonCartMode;
import de.melanx.utilitix.content.track.carts.stonecutter.StonecutterCartMode;
import io.github.noeppi_noeppi.libx.annotation.registration.RegisterClass;
import io.github.noeppi_noeppi.libx.network.EnumDataSerializer;
import net.minecraft.network.syncher.EntityDataSerializer;

@RegisterClass
public class ModSerializers {

    public static final EntityDataSerializer<PistonCartMode> pistonCartMode = new EnumDataSerializer<>(PistonCartMode.class);
    public static final EntityDataSerializer<StonecutterCartMode> stonecutterCartMode = new EnumDataSerializer<>(StonecutterCartMode.class);
}
