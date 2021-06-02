package de.melanx.utilitix.recipe;

import com.google.common.collect.ImmutableList;
import de.melanx.utilitix.registration.ModItemTags;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.PotionUtils;
import net.minecraft.potion.Potions;
import net.minecraft.util.LazyValue;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Predicate;

public class PotionInput {

    private final ItemStack main;
    private final ItemStack in1;
    private final ItemStack in2;
    private final LazyValue<List<EffectInstance>> effectsMain;
    private final LazyValue<List<EffectInstance>> effects1;
    private final LazyValue<List<EffectInstance>> effects2;

    public PotionInput(ItemStack main, ItemStack in1, ItemStack in2) {
        this.main = main;
        this.in1 = in1;
        this.in2 = in2;
        this.effectsMain = new LazyValue<>(() -> this.getEffects(main));
        this.effects1 = new LazyValue<>(() -> this.getEffects(in1));
        this.effects2 = new LazyValue<>(() -> this.getEffects(in2));
    }

    public ItemStack getMain() {
        return this.main;
    }

    public ItemStack getIn1() {
        return this.in1;
    }

    public ItemStack getIn2() {
        return this.in2;
    }

    @Nullable
    public List<EffectInstance> getEffectsMain() {
        return this.effectsMain.getValue();
    }

    @Nullable
    public List<EffectInstance> getEffects1() {
        return this.effects1.getValue();
    }

    @Nullable
    public List<EffectInstance> getEffects2() {
        return this.effects2.getValue();
    }
    
    public boolean testEffectsMain(Predicate<List<EffectInstance>> test) {
        return this.getEffectsMain() != null && test.test(this.getEffectsMain());
    }

    public boolean testEffects1(Predicate<List<EffectInstance>> test) {
        return this.getEffects1() != null && test.test(this.getEffects1());
    }

    public boolean testEffects2(Predicate<List<EffectInstance>> test) {
        return this.getEffects2() != null && test.test(this.getEffects2());
    }

    @Nullable
    private List<EffectInstance> getEffects(ItemStack stack) {
        if (stack.isEmpty()) {
            return null;
        } else if (ModItemTags.POTIONS.contains(stack.getItem())) {
            List<EffectInstance> list = PotionUtils.getEffectsFromStack(stack);
            if (list.isEmpty() && PotionUtils.getPotionFromItem(stack) != Potions.AWKWARD) {
                return null;
            }
            return list;
        } else {
            return ImmutableList.of();
        }
    }

}
