package de.melanx.utilitix.config;

import com.google.gson.JsonObject;
import de.melanx.utilitix.util.ArmorStandRotation;
import io.github.noeppi_noeppi.libx.config.ValueMapper;
import net.minecraft.network.PacketBuffer;

public class ArmorStandRotationMapper implements ValueMapper<ArmorStandRotation, JsonObject> {

    @Override
    public Class<ArmorStandRotation> type() {
        return ArmorStandRotation.class;
    }

    @Override
    public Class<JsonObject> element() {
        return JsonObject.class;
    }

    @Override
    public ArmorStandRotation fromJSON(JsonObject json, Class<?> elementType) {
        return ArmorStandRotation.deserialize(json);
    }

    @Override
    public JsonObject toJSON(ArmorStandRotation value, Class<?> elementType) {
        return value.serialize();
    }

    @Override
    public ArmorStandRotation read(PacketBuffer buffer, Class<?> elementType) {
        return ArmorStandRotation.read(buffer);
    }

    @Override
    public void write(ArmorStandRotation value, PacketBuffer buffer, Class<?> elementType) {
        value.write(buffer);
    }
}
