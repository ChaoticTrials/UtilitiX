package de.melanx.utilitix.recipe.brewery;

import de.melanx.utilitix.recipe.PotionInput;
import de.melanx.utilitix.recipe.PotionOutput;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public abstract class EffectTransformer {

    protected EffectTransformer() {}

    public abstract boolean canTransform(PotionInput input);

    public abstract ItemStack output();

    @Nullable
    public abstract PotionOutput transform(PotionInput input);

    public List<MobEffectInstance> getEffects(PotionContents contents) {
        List<MobEffectInstance> effects = new ArrayList<>();
        contents.getAllEffects().forEach(effects::add);

        return effects;
    }

    /**
     * Create a potion-like stack with effects.
     * <p>
     * Important: {@link PotionInput} treats potions with effects as valid inputs only if they are based on AWKWARD.
     * So we always set the base potion to AWKWARD and store the effects in {@link DataComponents#POTION_CONTENTS}.
     */
    public static ItemStack create(Item item, @Nullable List<MobEffectInstance> effects) {
        ItemStack stack = new ItemStack(item);

        PotionContents contents = new PotionContents(Potions.AWKWARD);
        if (effects != null && !effects.isEmpty()) {
            for (MobEffectInstance effect : effects) {
                // Copy to avoid sharing instances
                contents = contents.withEffectAdded(new MobEffectInstance(effect));
            }
        }

        stack.set(DataComponents.POTION_CONTENTS, contents);
        return stack;
    }
}
