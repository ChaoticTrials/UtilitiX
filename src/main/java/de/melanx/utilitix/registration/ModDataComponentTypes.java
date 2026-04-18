package de.melanx.utilitix.registration;

import com.mojang.serialization.Codec;
import de.melanx.utilitix.util.MobData;
import net.minecraft.core.BlockPos;
import net.minecraft.core.UUIDUtil;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.projectile.AbstractArrow;
import org.moddingx.libx.annotation.registration.RegisterClass;
import org.moddingx.libx.impl.codec.EnumCodec;

import java.util.UUID;
import java.util.function.UnaryOperator;

@RegisterClass(registry = "DATA_COMPONENT_TYPE")
public class ModDataComponentTypes {

    public static final DataComponentType<Boolean> gilded = ModDataComponentTypes.builder(builder -> builder.persistent(Codec.BOOL));
    public static final DataComponentType<Boolean> filled = ModDataComponentTypes.builder(builder -> builder.persistent(Codec.BOOL));
    public static final DataComponentType<AbstractArrow.Pickup> pickupType = ModDataComponentTypes.builder(builder -> builder.persistent(EnumCodec.get(AbstractArrow.Pickup.class)));
    public static final DataComponentType<MobData> mobData = ModDataComponentTypes.builder(builder -> builder.persistent(MobData.CODEC).networkSynchronized(MobData.STREAM_CODEC).cacheEncoding());
    public static final DataComponentType<UUID> redstoneId = ModDataComponentTypes.builder(builder -> builder.persistent(UUIDUtil.CODEC).networkSynchronized(UUIDUtil.STREAM_CODEC).cacheEncoding());
    public static final DataComponentType<BlockPos> ancientCityPos = ModDataComponentTypes.builder(builder -> builder.persistent(BlockPos.CODEC).networkSynchronized(BlockPos.STREAM_CODEC).cacheEncoding());
    public static final DataComponentType<ResourceLocation> ancientCityLevel = ModDataComponentTypes.builder(builder -> builder.persistent(ResourceLocation.CODEC).networkSynchronized(ResourceLocation.STREAM_CODEC).cacheEncoding());
    public static final DataComponentType<Integer> inventorySize = ModDataComponentTypes.builder(builder -> builder.persistent(Codec.INT));

    private static <T> DataComponentType<T> builder(UnaryOperator<DataComponentType.Builder<T>> builder) {
        return builder.apply(new DataComponentType.Builder<>()).build();
    }
}
