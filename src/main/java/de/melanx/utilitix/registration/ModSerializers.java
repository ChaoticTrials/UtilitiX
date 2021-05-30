package de.melanx.utilitix.registration;

import de.melanx.utilitix.content.track.carts.piston.PistonCartMode;
import de.melanx.utilitix.content.track.carts.stonecutter.StonecutterCartMode;
import io.github.noeppi_noeppi.libx.annotation.NoReg;
import io.github.noeppi_noeppi.libx.annotation.RegisterClass;
import io.github.noeppi_noeppi.libx.network.datasync.EnumDataSerializer;
import net.minecraft.network.datasync.IDataSerializer;
import net.minecraftforge.registries.DataSerializerEntry;

@RegisterClass
public class ModSerializers {

    @NoReg
    public static final IDataSerializer<PistonCartMode> pistonCartMode = new EnumDataSerializer<>(PistonCartMode.class);
    public static final IDataSerializer<StonecutterCartMode> stonecutterCartMode = new EnumDataSerializer<>(StonecutterCartMode.class);
    
    // TODO change in 1.17 (LibX will get changes to its registration system so this won't be necessary any longer)
    public static final DataSerializerEntry pistonCartEntry = new DataSerializerEntry(pistonCartMode);
    public static final DataSerializerEntry stonecutterCartEntry = new DataSerializerEntry(stonecutterCartMode);
}
