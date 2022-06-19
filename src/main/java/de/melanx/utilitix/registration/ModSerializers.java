package de.melanx.utilitix.registration;

import de.melanx.utilitix.content.track.carts.piston.PistonCartMode;
import de.melanx.utilitix.content.track.carts.stonecutter.StonecutterCartMode;
import net.minecraft.network.syncher.EntityDataSerializer;
import org.moddingx.libx.annotation.registration.RegisterClass;
import org.moddingx.libx.network.EnumDataSerializer;

@RegisterClass(registry = "DATA_SERIALIZERS")
public class ModSerializers {

    public static final EntityDataSerializer<PistonCartMode> pistonCartMode = new EnumDataSerializer<>(PistonCartMode.class);
    public static final EntityDataSerializer<StonecutterCartMode> stonecutterCartMode = new EnumDataSerializer<>(StonecutterCartMode.class);
}
