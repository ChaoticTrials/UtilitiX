package de.melanx.utilitix.content.shulkerboat;

import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.boat.ChestBoat;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ShulkerBoxMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.gamerules.GameRules;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.Supplier;

public class ShulkerBoat extends ChestBoat {

    private final boolean raft;

    public ShulkerBoat(EntityType<? extends ChestBoat> entityType, Level level, Supplier<Item> dropItem, boolean raft) {
        super(entityType, level, dropItem);
        this.raft = raft;
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int id, @Nonnull Inventory inventory, @Nonnull Player player) {
        if (this.getLootTable().isPresent() && player.isSpectator()) {
            return null;
        }

        this.unpackLootTable(player);
        return new ShulkerBoxMenu(id, inventory, this);
    }

    @Override
    public boolean hurtServer(@Nonnull ServerLevel level, @Nonnull DamageSource source, float damage) {
        this.setHurtDir(-this.getHurtDir());
        this.setHurtTime(10);
        this.setDamage(this.getDamage() + damage * 10);
        this.markHurt();
        this.gameEvent(GameEvent.ENTITY_DAMAGE, source.getEntity());
        boolean creative = source.getEntity() instanceof Player player && player.getAbilities().instabuild;
        if (creative || this.getDamage() > 40) {
            if ((!creative || this.hasItems()) && (level.getServer().getGameRules().get(GameRules.ENTITY_DROPS) || this.hasItems())) {
                this.destroy(level, this.getDropItem());
            }

            this.discard();
        }

        return true;
    }

    private boolean hasItems() {
        for (ItemStack item : this.getItemStacks()) {
            if (!item.isEmpty()) {
                return true;
            }
        }

        return false;
    }

    @Override
    public void destroy(@Nonnull ServerLevel level, @Nonnull Item dropItem) {
        ItemStack drop = dropItem.getDefaultInstance();
        ItemContainerContents containerContents = ItemContainerContents.fromItems(this.getItemStacks());

        if (containerContents != ItemContainerContents.EMPTY) {
            drop.set(DataComponents.CONTAINER, containerContents);
        }

        if (this.hasCustomName()) {
            drop.set(DataComponents.CUSTOM_NAME, this.getCustomName());
        }

        this.spawnAtLocation(level, drop);
    }

    @Override
    public void remove(@Nonnull RemovalReason reason) {
        if (!this.level().isClientSide() && reason.shouldDestroy()) {
            if (this.isLeashed()) {
                this.dropLeash();
            }
        }

        this.setRemoved(reason);
    }

    @Override
    protected double rideHeight(@Nonnull EntityDimensions dimensions) {
        return this.raft ? dimensions.height() * 0.8888889F : super.rideHeight(dimensions);
    }
}
