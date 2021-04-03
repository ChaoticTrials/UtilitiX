package de.melanx.utilitix.item.bells;

import io.github.noeppi_noeppi.libx.mod.ModX;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tags.EntityTypeTags;

public class HandBell extends BellBase {

    public HandBell(ModX mod, Item.Properties properties) {
        super(mod, properties.setISTER(() -> RenderHandBell::new));
    }

    @Override
    boolean entityFilter(LivingEntity entity, ItemStack stack) {
        return entity.isAlive() && entity.getType().isContained(EntityTypeTags.RAIDERS);
    }

    @Override
    boolean notifyNearbyEntities() {
        return true;
    }
}
