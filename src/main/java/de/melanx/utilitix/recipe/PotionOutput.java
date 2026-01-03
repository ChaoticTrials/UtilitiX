package de.melanx.utilitix.recipe;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.item.ItemStack;

public class PotionOutput {

    public static final MapCodec<PotionOutput> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            ItemStack.CODEC.fieldOf("main").forGetter(PotionOutput::getMain),
            ItemStack.CODEC.fieldOf("out1").forGetter(PotionOutput::getOut1),
            ItemStack.CODEC.fieldOf("out2").forGetter(PotionOutput::getOut2)
    ).apply(instance, PotionOutput::new));

    private final ItemStack main;
    private final ItemStack out1;
    private final ItemStack out2;

    protected PotionOutput(ItemStack main, ItemStack out1, ItemStack out2) {
        this.main = main;
        this.out1 = out1;
        this.out2 = out2;
    }

    public ItemStack getMain() {
        return this.main;
    }

    public ItemStack getOut1() {
        return this.out1;
    }

    public ItemStack getOut2() {
        return this.out2;
    }
    
    public static PotionOutput simple(ItemStack potion) {
        return new PotionOutput(potion, ItemStack.EMPTY, ItemStack.EMPTY);
    }
    
    public static PotionOutput create(ItemStack potion, ItemStack out1, ItemStack out2) {
        return new PotionOutput(potion, out1, out2);
    }
}
