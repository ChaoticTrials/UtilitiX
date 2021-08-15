package de.melanx.utilitix.config;

import com.google.gson.JsonObject;
import de.melanx.utilitix.util.ArmorStandRotation;
import io.github.noeppi_noeppi.libx.config.ValueMapper;
import net.minecraft.network.FriendlyByteBuf;

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
    public ArmorStandRotation fromJson(JsonObject json) {
        return ArmorStandRotation.deserialize(json);
    }

    @Override
    public JsonObject toJson(ArmorStandRotation value) {
        return value.serialize();
    }

    @Override
    public ArmorStandRotation fromNetwork(FriendlyByteBuf buffer) {
        return ArmorStandRotation.read(buffer);
    }

    @Override
    public void toNetwork(ArmorStandRotation value, FriendlyByteBuf buffer) {
        value.write(buffer);
    }
}
