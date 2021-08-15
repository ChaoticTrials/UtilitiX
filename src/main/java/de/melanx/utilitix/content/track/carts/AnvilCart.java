package de.melanx.utilitix.content.track.carts;

import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nonnull;

public class AnvilCart extends Cart {

    public AnvilCart(EntityType<?> type, Level level) {
        super(type, level);
    }

    @Nonnull
    @Override
    public BlockState getDefaultDisplayBlockState() {
        return Blocks.ANVIL.defaultBlockState();
    }

    @Override
    public void destroy(@Nonnull DamageSource source) {
        super.destroy(source);
        this.spawnAtLocation(Items.ANVIL);
    }

    @Override
    public void tick() {
        AABB collisionBox = this.getCollisionHandler() != null ? this.getCollisionHandler().getMinecartCollisionBox(this) : this.getBoundingBox().inflate(0.2, 0, 0.2);
        if (this.canBeRidden() && getHorizontalDistanceSqr(this.getDeltaMovement()) > 0.1 * 0.1) {
            for (Entity entity : this.level.getEntities(this, collisionBox, EntitySelector.pushableBy(this))) {
                if (!(entity instanceof AbstractMinecart)) {
                    if (entity instanceof Player || entity instanceof IronGolem || this.isVehicle() || entity.isPassenger()) {
                        this.boostEntity(entity);
                    } else {
                        if (!entity.startRiding(this)) {
                            this.boostEntity(entity);
                        }
                    }
                }
            }
        } else {
            for (Entity entity : this.level.getEntities(this, collisionBox)) {
                if (!this.hasPassenger(entity) && entity.isPushable() && !(entity instanceof AbstractMinecart)) {
                    this.boostEntity(entity);
                }
            }
        }
        super.tick();
    }

    @Override
    public boolean canCollideWith(@Nonnull Entity entity) {
        return super.canCollideWith(entity) && entity instanceof AbstractMinecart;
    }

    private void boostEntity(Entity entity) {
        double boost = Mth.clamp(Math.sqrt(getHorizontalDistanceSqr(this.getDeltaMovement())), 0.05, 0.5);
        Direction minecartDir = this.getMotionDirection();
        Vec3 targetVec = new Vec3(
                (entity.getX() - this.getX()) * (minecartDir.getAxis() == Direction.Axis.Z ? 2.5 : 0.8),
                0,
                (entity.getZ() - this.getZ()) * (minecartDir.getAxis() == Direction.Axis.X ? 2.5 : 0.8)
        ).normalize().scale(1.5 * boost);
        entity.hurt(DamageSource.ANVIL, 0.25f);
        entity.push(targetVec.x, targetVec.y, targetVec.z);
    }
}
