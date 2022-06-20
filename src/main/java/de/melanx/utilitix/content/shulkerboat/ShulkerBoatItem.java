package de.melanx.utilitix.content.shulkerboat;

import net.minecraft.stats.Stats;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.item.BoatItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nonnull;
import java.util.List;

public class ShulkerBoatItem extends BoatItem {

    private final Boat.Type boatType;

    public ShulkerBoatItem(Boat.Type boatType, Properties properties) {
        super(true, boatType, properties);
        this.boatType = boatType;
    }

    @Nonnull
    @Override
    public InteractionResultHolder<ItemStack> use(@Nonnull Level level, @Nonnull Player player, @Nonnull InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        BlockHitResult hitResult = Item.getPlayerPOVHitResult(level, player, ClipContext.Fluid.ANY);
        if (hitResult.getType() == HitResult.Type.MISS) {
            return InteractionResultHolder.pass(stack);
        }

        Vec3 view = player.getViewVector(1);
        List<Entity> entities = level.getEntities(player, player.getBoundingBox().expandTowards(view.scale(5)).inflate(1), EntitySelector.NO_SPECTATORS.and(Entity::isPickable));
        if (!entities.isEmpty()) {
            Vec3 eyePos = player.getEyePosition();

            for (Entity entity : entities) {
                AABB box = entity.getBoundingBox().inflate(entity.getPickRadius());
                if (box.contains(eyePos)) {
                    return InteractionResultHolder.pass(stack);
                }
            }
        }

        if (hitResult.getType() == HitResult.Type.BLOCK) {
            ShulkerBoat boat = new ShulkerBoat(level, hitResult.getLocation());
            boat.setType(this.boatType);
            boat.setYRot(player.getYRot());
            if (!level.noCollision(boat, boat.getBoundingBox())) {
                return InteractionResultHolder.fail(stack);
            }

            if (!level.isClientSide) {
                if (stack.hasCustomHoverName()) {
                    boat.setCustomName(stack.getHoverName());
                }
                ContainerHelper.loadAllItems(stack.getOrCreateTag().getCompound("Items"), boat.getItemStacks());
                level.addFreshEntity(boat);
                level.gameEvent(player, GameEvent.ENTITY_PLACE, hitResult.getLocation());
                if (!player.getAbilities().instabuild) {
                    stack.shrink(1);
                }
            }

            player.awardStat(Stats.ITEM_USED.get(this));
            return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
        }

        return InteractionResultHolder.pass(stack);
    }

    @Override
    public boolean canFitInsideContainerItems() {
        return false;
    }
}
