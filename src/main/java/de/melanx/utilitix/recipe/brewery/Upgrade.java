package de.melanx.utilitix.recipe.brewery;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.melanx.utilitix.recipe.PotionInput;
import de.melanx.utilitix.recipe.PotionOutput;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import javax.annotation.Nullable;
import java.util.List;

public class Upgrade extends EffectTransformer {

    public static final MapCodec<Upgrade> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.intRange(1, 255).fieldOf("max_level").forGetter(transformer -> transformer.maxLevel + 1)
    ).apply(instance, maxLevelOneBased -> new Upgrade(Math.max(0, maxLevelOneBased - 1))));

    public static final StreamCodec<RegistryFriendlyByteBuf, Upgrade> STREAM_CODEC = StreamCodec.of(
            (buffer, value) -> buffer.writeVarInt(value.maxLevel),
            buffer -> new Upgrade(buffer.readVarInt())
    );

    private final int maxLevel;

    public Upgrade(int maxLevel) {
        this.maxLevel = maxLevel;
    }

    @Override
    public boolean canTransform(PotionInput input) {
        return input.getIn1().isEmpty() && input.getIn2().isEmpty() &&
                input.testEffectsMain(potionContents -> {
                    List<MobEffectInstance> effects = this.getEffects(potionContents);
                    return effects.size() == 1 && effects.getFirst().getAmplifier() < this.maxLevel;
                });
    }

    @Override
    public ItemStack output() {
        return new ItemStack(Items.POTION);
    }

    @Nullable
    @Override
    public PotionOutput transform(PotionInput input) {
        if (input.getEffectsMain() == null) {
            return null;
        }

        List<MobEffectInstance> effects = this.getEffects(input.getEffectsMain());
        if (effects.isEmpty()) {
            return null;
        }

        MobEffectInstance old = effects.getFirst();
        ItemStack newStack = EffectTransformer.create(
                input.getMain().getItem(),
                ImmutableList.of(new MobEffectInstance(
                        old.getEffect(),
                        old.getDuration(),
                        Mth.clamp(old.getAmplifier() + 1, 0, this.maxLevel),
                        old.isAmbient(),
                        old.isVisible()
                ))
        );
        newStack.set(DataComponents.CUSTOM_NAME, input.getMain().getHoverName());

        return PotionOutput.simple(newStack);
    }
}
