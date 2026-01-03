package de.melanx.utilitix.recipe.brewery;

import com.mojang.serialization.MapCodec;
import de.melanx.utilitix.recipe.PotionInput;
import de.melanx.utilitix.recipe.PotionOutput;
import de.melanx.utilitix.registration.ModItemTags;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import javax.annotation.Nullable;

public class Clone extends EffectTransformer {

    private static final Clone INSTANCE = new Clone();
    public static final MapCodec<Clone> CODEC = MapCodec.unit(Clone.INSTANCE);
    public static final StreamCodec<RegistryFriendlyByteBuf, Clone> STREAM_CODEC = StreamCodec.unit(Clone.INSTANCE);

    @Override
    public boolean canTransform(PotionInput input) {
        return input.testEffectsMain(potionContents -> !this.getEffects(potionContents).isEmpty()) && input.getIn1().is(ModItemTags.POTIONS)
                && input.getEffects1() == null && input.getIn2().is(ModItemTags.POTIONS)
                && input.getEffects2() == null;
    }

    @Override
    public ItemStack output() {
        return new ItemStack(Items.POTION);
    }

    @Nullable
    @Override
    public PotionOutput transform(PotionInput input) {
        return PotionOutput.create(ItemStack.EMPTY, input.getMain().copy(), input.getMain().copy());
    }
}
