package de.melanx.utilitix.content.track.carts;

import de.melanx.utilitix.content.track.TrackUtil;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.item.minecart.AbstractMinecartEntity;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Direction;
import net.minecraft.util.EntityPredicates;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.common.IMinecartCollisionHandler;

import javax.annotation.Nonnull;

public class EntityAnvilCart extends EntityCart {
    
    public EntityAnvilCart(EntityType<?> type, World world) {
        super(type, world);
    }

    @Nonnull
    @Override
    public BlockState getDefaultDisplayTile() {
        return Blocks.ANVIL.getDefaultState();
    }

    @Override
    public void killMinecart(@Nonnull DamageSource source) {
        super.killMinecart(source);
        this.entityDropItem(Items.ANVIL);
    }

    @Override
    public void tick() {
        AxisAlignedBB collisionBox = this.getCollisionHandler() != null ? this.getCollisionHandler().getMinecartCollisionBox(this) : this.getBoundingBox().grow(0.2, 0, 0.2);
        if (this.canBeRidden() && horizontalMag(this.getMotion()) > 0.1 * 0.1) {
            for (Entity entity : this.world.getEntitiesInAABBexcluding(this, collisionBox, EntityPredicates.pushableBy(this))) {
                if (!(entity instanceof AbstractMinecartEntity)) {
                    if (entity instanceof PlayerEntity || entity instanceof IronGolemEntity || this.isBeingRidden() || entity.isPassenger()) {
                        this.boostEntity(entity);
                    } else {
                        if (!entity.startRiding(this)) {
                            this.boostEntity(entity);
                        }
                    }
                }
            }
        } else {
            for (Entity entity : this.world.getEntitiesWithinAABBExcludingEntity(this, collisionBox)) {
                if (!this.isPassenger(entity) && entity.canBePushed() && !(entity instanceof AbstractMinecartEntity)) {
                    this.boostEntity(entity);
                }
            }
        }
        super.tick();
    }

    @Override
    public boolean canCollide(@Nonnull Entity entity) {
        return super.canCollide(entity) && entity instanceof AbstractMinecartEntity;
    }

    private void boostEntity(Entity entity) {
        double boost = MathHelper.clamp(Math.sqrt(horizontalMag(this.getMotion())), 0.05, 0.5);
        Direction minecartDir = this.getAdjustedHorizontalFacing();
        Vector3d targetVec = new Vector3d(
                (entity.getPosX() - this.getPosX()) * (minecartDir.getAxis() == Direction.Axis.Z ? 2.5 : 0.8),
                0,
                (entity.getPosZ() - this.getPosZ()) * (minecartDir.getAxis() == Direction.Axis.X ? 2.5 : 0.8)
        ).normalize().scale(1.5 * boost);
        entity.attackEntityFrom(DamageSource.ANVIL, 0.25f);
        entity.addVelocity(targetVec.x, targetVec.y, targetVec.z);
    }
}
