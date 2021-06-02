package de.melanx.utilitix.config;

import com.google.common.collect.ImmutableList;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import de.melanx.utilitix.util.ArmorStandRotation;
import io.github.noeppi_noeppi.libx.config.ValueMapper;
import net.minecraft.network.PacketBuffer;

import java.util.List;

public class ArmorStandRotationListMapper implements ValueMapper<List<ArmorStandRotation>, JsonArray> {

    @Override
    public Class<List<ArmorStandRotation>> type() {
        //noinspection unchecked
        return (Class<List<ArmorStandRotation>>) (Class<?>) List.class;
    }

    @Override
    public Class<JsonArray> element() {
        return JsonArray.class;
    }

    @Override
    public List<ArmorStandRotation> fromJSON(JsonArray json, Class<?> elementType) {
        ImmutableList.Builder<ArmorStandRotation> builder = ImmutableList.builder();
        for (JsonElement e : json) {
            builder.add(ArmorStandRotation.deserialize(e.getAsJsonObject()));
        }
        return builder.build();
    }

    @Override
    public JsonArray toJSON(List<ArmorStandRotation> value, Class<?> elementType) {
        JsonArray array = new JsonArray();
        for (ArmorStandRotation rot : value) {
            array.add(rot.serialize());
        }
        return array;
    }

    @Override
    public List<ArmorStandRotation> read(PacketBuffer buffer, Class<?> elementType) {
        int size = buffer.readVarInt();
        ImmutableList.Builder<ArmorStandRotation> builder = ImmutableList.builder();
        for (int i = 0; i < size; i++) {
            builder.add(ArmorStandRotation.read(buffer));
        }
        return builder.build();
    }

    @Override
    public void write(List<ArmorStandRotation> value, PacketBuffer buffer, Class<?> elementType) {
        buffer.writeVarInt(value.size());
        for (ArmorStandRotation rot : value) {
            rot.write(buffer);
        }
    }
}
