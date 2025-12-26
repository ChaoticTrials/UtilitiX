package de.melanx.utilitix.util;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.EntityType;

public record MobData(String entityType, CompoundTag entityData) {

    public static final Codec<MobData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.fieldOf("entity_type").forGetter(MobData::entityType),
            CompoundTag.CODEC.fieldOf("entity_data").forGetter(MobData::entityData)
    ).apply(instance, MobData::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, MobData> STREAM_CODEC = StreamCodec.of(
            (buffer, data) -> {
                buffer.writeUtf(data.entityType);
                buffer.writeNbt(data.entityData);
            }, buffer -> new MobData(buffer.readUtf(), buffer.readNbt())
    );

    public EntityType<?> getEntityType() {
        return EntityType.byString(this.entityType).orElse(null);
    }
}
