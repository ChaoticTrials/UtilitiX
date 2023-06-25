package de.melanx.utilitix.content;

import de.melanx.utilitix.UtilitiXConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.item.ArmorStandItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nonnull;
import java.util.function.Consumer;

public class ArmedStand extends ArmorStandItem {

    public ArmedStand(Properties properties) {
        super(properties);
    }

    @Nonnull
    @Override
    public InteractionResult useOn(@Nonnull UseOnContext context) {
        Direction direction = context.getClickedFace();
        if (direction == Direction.DOWN) {
            return InteractionResult.FAIL;
        }

        Level level = context.getLevel();
        BlockPlaceContext blockPLaceContext = new BlockPlaceContext(context);
        BlockPos pos = blockPLaceContext.getClickedPos();
        ItemStack stack = context.getItemInHand();
        Vec3 centerPos = Vec3.atBottomCenterOf(pos);
        AABB box = EntityType.ARMOR_STAND.getDimensions().makeBoundingBox(centerPos.x(), centerPos.y(), centerPos.z());

        if (level.noCollision(null, box) && level.getEntities(null, box).isEmpty()) {
            if (level instanceof ServerLevel serverlevel) {
                Consumer<ArmorStand> consumer = EntityType.createDefaultStackConfig(serverlevel, stack, context.getPlayer());
                ArmorStand stand = EntityType.ARMOR_STAND.create(serverlevel, stack.getTag(), consumer, pos, MobSpawnType.SPAWN_EGG, true, true);
                if (stand == null) {
                    return InteractionResult.FAIL;
                }

                float yRot = (float) Mth.floor((Mth.wrapDegrees(context.getRotation() - 180.0F) + 22.5F) / 45.0F) * 45.0F;
                stand.moveTo(stand.getX(), stand.getY(), stand.getZ(), yRot, 0.0F);
                this.setArms(stand);
                serverlevel.addFreshEntityWithPassengers(stand);
                level.playSound(null, stand.getX(), stand.getY(), stand.getZ(), SoundEvents.ARMOR_STAND_PLACE, SoundSource.BLOCKS, 0.75F, 0.8F);
                stand.gameEvent(GameEvent.ENTITY_PLACE, context.getPlayer());
            }

            stack.shrink(1);
            return InteractionResult.sidedSuccess(level.isClientSide);
        }

        return InteractionResult.FAIL;
    }

    public void setArms(@Nonnull ArmorStand armorStand) {
        armorStand.setShowArms(true);
        armorStand.getPersistentData().putBoolean("UtilitiXArmorStand", true);
        armorStand.getPersistentData().putInt("UtilitiXPoseIdx", 0);
        if (!UtilitiXConfig.armorStandPoses.isEmpty()) {
            UtilitiXConfig.armorStandPoses.get(0).apply(armorStand);
        }
    }
}
