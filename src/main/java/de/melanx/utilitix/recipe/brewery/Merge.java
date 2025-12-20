package de.melanx.utilitix.recipe.brewery;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.melanx.utilitix.recipe.PotionInput;
import de.melanx.utilitix.recipe.PotionOutput;
import de.melanx.utilitix.registration.ModItems;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionContents;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Merge extends EffectTransformer {

    public static final MapCodec<Merge> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.FLOAT.fieldOf("fail_multiplier").forGetter(Merge::getFailMultiplier)
    ).apply(instance, Merge::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, Merge> STREAM_CODEC = StreamCodec.of(
            (buffer, value) -> buffer.writeFloat(value.getFailMultiplier()),
            buffer -> new Merge(buffer.readFloat())
    );

    private final float failMultiplier;

    public Merge(float failMultiplier) {
        this.failMultiplier = failMultiplier;
    }

    @Override
    public boolean canTransform(PotionInput input) {
        return input.getMain().getItem() == Items.GLASS_BOTTLE && input.getIn1().getItem() == input.getIn2().getItem()
                && input.testEffects1(list -> !list.customEffects().isEmpty()) && input.testEffects2(list -> !list.customEffects().isEmpty());
    }

    @Override
    public ItemStack output() {
        return new ItemStack(Items.POTION);
    }

    @Nullable
    @Override
    public PotionOutput transform(PotionInput input) {
        List<MobEffectInstance> merged = new ArrayList<>();
        if (input.getEffects1() != null) {
            for (MobEffectInstance effect : input.getEffects1().customEffects()) {
                this.addMergedEffectToList(effect.getEffect(), merged, input.getEffects1(), input.getEffects2());
            }
        }
        if (input.getEffects2() != null) {
            for (MobEffectInstance effect : input.getEffects2().customEffects()) {
                this.addMergedEffectToList(effect.getEffect(), merged, input.getEffects1(), input.getEffects2());
            }
        }
        float chance = Math.max(0, merged.size() + 1) * this.failMultiplier;
        if (new Random().nextInt(100) < chance) {
            return PotionOutput.simple(new ItemStack(ModItems.failedPotion));
        } else {
            ItemStack stack = EffectTransformer.create(input.getIn1().getItem(), merged);
            stack.set(DataComponents.CUSTOM_NAME, Component.translatable("item.utilitix.merged_potion").withStyle(ChatFormatting.GREEN));
            return PotionOutput.simple(stack);
        }
    }

    public float getFailMultiplier() {
        return failMultiplier;
    }

    private void addMergedEffectToList(Holder<MobEffect> potion, List<MobEffectInstance> mergeList, @Nullable PotionContents list1, @Nullable PotionContents list2) {
        for (MobEffectInstance effect : mergeList) {
            if (effect.getEffect() == potion)
                return;
        }

        MobEffectInstance effect1 = null;
        MobEffectInstance effect2 = null;
        if (list1 != null) {
            for (MobEffectInstance effect : list1.customEffects()) {
                if (effect.getEffect() == potion) {
                    effect1 = effect;
                    break;
                }
            }
        }
        if (list2 != null) {
            for (MobEffectInstance effect : list2.customEffects()) {
                if (effect.getEffect() == potion) {
                    effect2 = effect;
                    break;
                }
            }
        }

        //noinspection StatementWithEmptyBody
        if (effect1 == null && effect2 == null) {
            //
        } else if (effect1 == null) {
            mergeList.add(effect2);
        } else if (effect2 == null) {
            mergeList.add(effect1);
        } else {
            boolean useFirst;
            if (effect1.getAmplifier() > effect2.getAmplifier())
                useFirst = true;
            else if (effect2.getAmplifier() > effect1.getAmplifier())
                useFirst = false;
            else
                useFirst = effect1.getDuration() > effect2.getDuration();
            if (useFirst)
                mergeList.add(effect1);
            else
                mergeList.add(effect2);
        }
    }
}
