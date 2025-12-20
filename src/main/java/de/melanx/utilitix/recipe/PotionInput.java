package de.melanx.utilitix.recipe;

import de.melanx.utilitix.registration.ModItemTags;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import org.moddingx.libx.util.lazy.LazyValue;

import javax.annotation.Nullable;
import java.util.function.Predicate;

public class PotionInput {

    private final ItemStack main;
    private final ItemStack in1;
    private final ItemStack in2;
    private final LazyValue<PotionContents> effectsMain;
    private final LazyValue<PotionContents> effects1;
    private final LazyValue<PotionContents> effects2;

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
    public PotionContents getEffectsMain() {
        return this.effectsMain.get();
    }

    @Nullable
    public PotionContents getEffects1() {
        return this.effects1.get();
    }

    @Nullable
    public PotionContents getEffects2() {
        return this.effects2.get();
    }

    public boolean testEffectsMain(Predicate<PotionContents> test) {
        return this.getEffectsMain() != null && test.test(this.getEffectsMain());
    }

    public boolean testEffects1(Predicate<PotionContents> test) {
        return this.getEffects1() != null && test.test(this.getEffects1());
    }

    public boolean testEffects2(Predicate<PotionContents> test) {
        return this.getEffects2() != null && test.test(this.getEffects2());
    }

    @Nullable
    private PotionContents getEffects(ItemStack stack) {
        if (stack.isEmpty()) {
            return null;
        }

        if (stack.is(ModItemTags.POTIONS)) {
            PotionContents potionContents = stack.get(DataComponents.POTION_CONTENTS);

            if (potionContents != null && potionContents.hasEffects() && !potionContents.is(Potions.AWKWARD)) {
                return potionContents;
            }

            return null;
        }

        return PotionContents.EMPTY;
    }

}
