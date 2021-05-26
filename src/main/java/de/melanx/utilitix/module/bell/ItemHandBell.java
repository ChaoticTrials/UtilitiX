package de.melanx.utilitix.module.bell;

import io.github.noeppi_noeppi.libx.mod.ModX;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tags.EntityTypeTags;

public class ItemHandBell extends BellBase {

    public ItemHandBell(ModX mod, Item.Properties properties) {
        super(mod, properties.setISTER(() -> RenderBell::new));
    }

    @Override
    protected boolean entityFilter(LivingEntity entity, ItemStack stack) {
        return entity.isAlive() && entity.getType().isContained(EntityTypeTags.RAIDERS);
    }

    @Override
    protected boolean notifyNearbyEntities() {
        return true;
    }
}
