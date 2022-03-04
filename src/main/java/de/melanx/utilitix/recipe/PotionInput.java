package de.melanx.utilitix.recipe;

import com.google.common.collect.ImmutableList;
import de.melanx.utilitix.registration.ModItemTags;
import io.github.noeppi_noeppi.libx.util.LazyValue;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Predicate;

public class PotionInput {

    private final ItemStack main;
    private final ItemStack in1;
    private final ItemStack in2;
    private final LazyValue<List<MobEffectInstance>> effectsMain;
    private final LazyValue<List<MobEffectInstance>> effects1;
    private final LazyValue<List<MobEffectInstance>> effects2;

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
    public List<MobEffectInstance> getEffectsMain() {
        return this.effectsMain.get();
    }

    @Nullable
    public List<MobEffectInstance> getEffects1() {
        return this.effects1.get();
    }

    @Nullable
    public List<MobEffectInstance> getEffects2() {
        return this.effects2.get();
    }

    public boolean testEffectsMain(Predicate<List<MobEffectInstance>> test) {
        return this.getEffectsMain() != null && test.test(this.getEffectsMain());
    }

    public boolean testEffects1(Predicate<List<MobEffectInstance>> test) {
        return this.getEffects1() != null && test.test(this.getEffects1());
    }

    public boolean testEffects2(Predicate<List<MobEffectInstance>> test) {
        return this.getEffects2() != null && test.test(this.getEffects2());
    }

    @Nullable
    private List<MobEffectInstance> getEffects(ItemStack stack) {
        if (stack.isEmpty()) {
            return null;
        } else if (stack.is(ModItemTags.POTIONS)) {
            List<MobEffectInstance> list = PotionUtils.getMobEffects(stack);
            if (list.isEmpty() && PotionUtils.getPotion(stack) != Potions.AWKWARD) {
                return null;
            }
            return list;
        } else {
            return ImmutableList.of();
        }
    }

}
