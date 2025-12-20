package de.melanx.utilitix.recipe.brewery;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.melanx.utilitix.recipe.PotionInput;
import de.melanx.utilitix.recipe.PotionOutput;
import de.melanx.utilitix.registration.ModItemTags;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.moddingx.libx.util.Misc;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class Apply extends EffectTransformer {

    public static final Codec<Holder<MobEffect>> MOB_EFFECT_HOLDER_CODEC =
            ResourceLocation.CODEC.comapFlatMap(
                    id -> BuiltInRegistries.MOB_EFFECT.getHolder(id)
                            .<DataResult<Holder<MobEffect>>>map(DataResult::success)
                            .orElseGet(() -> DataResult.error(() -> "Unknown mob effect: " + id)),
                    holder -> {
                        ResourceLocation key = BuiltInRegistries.MOB_EFFECT.getKey(holder.value());
                        return key == null ? Misc.MISSINGNO : key;
                    }
            );

    public static final Codec<MobEffectInstance> EFFECT_CODEC = RecordCodecBuilder.create(instance -> instance.group(
            MOB_EFFECT_HOLDER_CODEC.fieldOf("effect").forGetter(MobEffectInstance::getEffect),
            Codec.intRange(1, 255).optionalFieldOf("amplifier", 1).forGetter(e -> e.getAmplifier() + 1),
            Codec.intRange(1, Integer.MAX_VALUE).fieldOf("duration").forGetter(MobEffectInstance::getDuration),
            Codec.BOOL.optionalFieldOf("ambient", false).forGetter(MobEffectInstance::isAmbient),
            Codec.BOOL.optionalFieldOf("particles", true).forGetter(MobEffectInstance::isVisible)
    ).apply(instance, (effect, ampPlusOne, duration, ambient, particles) ->
            new MobEffectInstance(effect, duration, Math.max(0, ampPlusOne - 1), ambient, particles)
    ));

    public static final MapCodec<Apply> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            ComponentSerialization.CODEC.optionalFieldOf("name").forGetter(Apply::getName),
            EFFECT_CODEC.listOf().fieldOf("effects").forGetter(Apply::getEffects)
    ).apply(instance, (nameOpt, effects) -> new Apply(nameOpt.orElse(null), effects)));

    public static final StreamCodec<RegistryFriendlyByteBuf, Apply> STREAM_CODEC = StreamCodec.of(
            Apply::toNetwork, Apply::fromNetwork
    );

    @Nullable
    private final Component name;
    private final List<MobEffectInstance> effects;

    public Apply(MobEffectInstance... effects) {
        this(null, effects);
    }

    public Apply(List<MobEffectInstance> effects) {
        this(null, effects);
    }

    public Apply(@Nullable Component name, MobEffectInstance... effects) {
        this(name, ImmutableList.copyOf(effects));
    }

    public Apply(@Nullable Component name, List<MobEffectInstance> effects) {
        this.name = name;
        this.effects = ImmutableList.copyOf(effects);
    }

    @Override
    public boolean canTransform(PotionInput input) {
        return input.getMain().is(ModItemTags.POTIONS)
                && input.testEffectsMain(contents -> contents.customEffects().isEmpty())
                && input.getIn1().isEmpty() && input.getIn2().isEmpty();
    }

    @Override
    public ItemStack output() {
        ItemStack stack = EffectTransformer.create(Items.POTION, this.effects);
        if (this.name != null) {
            stack.set(DataComponents.CUSTOM_NAME, this.name.copy());
        }

        return stack;
    }

    @Nullable
    @Override
    public PotionOutput transform(PotionInput input) {
        ItemStack stack = EffectTransformer.create(input.getMain().getItem(), this.effects);
        if (this.name != null) {
            stack.set(DataComponents.CUSTOM_NAME, this.name.copy());
        }

        return PotionOutput.simple(stack);
    }

    @Nullable
    public Optional<Component> getName() {
        return Optional.ofNullable(this.name);
    }

    public List<MobEffectInstance> getEffects() {
        return this.effects;
    }

    private static Apply fromNetwork(RegistryFriendlyByteBuf buffer) {
        Component name = null;
        if (buffer.readBoolean()) {
            name = ComponentSerialization.TRUSTED_CONTEXT_FREE_STREAM_CODEC.decode(buffer);
        }
        int size = buffer.readVarInt();
        ImmutableList.Builder<MobEffectInstance> effects = ImmutableList.builder();
        for (int i = 0; i < size; i++) {
            CompoundTag nbt = buffer.readNbt();
            if (nbt == null) {
                throw new IllegalStateException("Missing MobEffectInstance NBT in Apply transformer");
            }
            effects.add(Objects.requireNonNull(MobEffectInstance.load(nbt)));
        }
        return new Apply(name, effects.build());
    }

    private static void toNetwork(RegistryFriendlyByteBuf buffer, Apply transformer) {
        buffer.writeBoolean(transformer.name != null);
        if (transformer.name != null) {
            ComponentSerialization.TRUSTED_CONTEXT_FREE_STREAM_CODEC.encode(buffer, transformer.name);
        }
        buffer.writeVarInt(transformer.effects.size());
        for (MobEffectInstance effect : transformer.effects) {
            buffer.writeNbt(effect.save());
        }
    }
}
