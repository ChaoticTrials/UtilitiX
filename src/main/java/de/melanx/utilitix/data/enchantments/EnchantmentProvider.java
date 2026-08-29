package de.melanx.utilitix.data.enchantments;

import de.melanx.utilitix.UtilitiX;
import de.melanx.utilitix.registration.ModItems;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.item.enchantment.Enchantment;
import org.moddingx.libx.datagen.DatagenContext;
import org.moddingx.libx.datagen.provider.EnchantmentProviderBase;

public class EnchantmentProvider extends EnchantmentProviderBase {

    public static final ResourceKey<Enchantment> BELL_RANGE = ResourceKey.create(Registries.ENCHANTMENT, UtilitiX.getInstance().id("bell_range"));

    public final Holder<Enchantment> bellRange = this.enchantment(Component.translatable("enchantment.utilitix.bell_range"))
            .supportedItems(ModItems.handBell, ModItems.mobBell)
            .slot(EquipmentSlotGroup.HAND)
            .minCost(15, 9)
            .maxCost(20, 9)
            .maxLevel(3)
            .build();

    public EnchantmentProvider(DatagenContext ctx) {
        super(ctx);
    }
}
