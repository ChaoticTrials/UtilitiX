package de.melanx.utilitix.client;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.melanx.utilitix.registration.ModDataComponentTypes;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.properties.numeric.NeedleDirectionHelper;
import net.minecraft.client.renderer.item.properties.numeric.RangeSelectItemModelProperty;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.ItemOwner;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

import javax.annotation.Nonnull;

public class AncientCompassAngleProperty extends NeedleDirectionHelper implements RangeSelectItemModelProperty {

    public static final MapCodec<AncientCompassAngleProperty> MAP_CODEC = RecordCodecBuilder.mapCodec(
            i -> i.group(
                    Codec.BOOL.optionalFieldOf("wobble", true).forGetter(p -> p.wobble())
            ).apply(i, AncientCompassAngleProperty::new)
    );

    private final NeedleDirectionHelper.Wobbler wobbler;
    private final NeedleDirectionHelper.Wobbler noTargetWobbler;
    private final RandomSource random = RandomSource.create();

    public AncientCompassAngleProperty(boolean wobble) {
        super(wobble);
        this.wobbler = this.newWobbler(0.8F);
        this.noTargetWobbler = this.newWobbler(0.8F);
    }

    @Override
    protected float calculate(@Nonnull ItemStack stack, ClientLevel level, int seed, @Nonnull ItemOwner owner) {
        GlobalPos target = AncientCompassAngleProperty.target(stack);
        long gameTime = level.getGameTime();

        return !AncientCompassAngleProperty.isValidTarget(owner, target)
                ? this.getRandomlySpinningRotation(seed, gameTime)
                : this.getRotationTowardsTarget(owner, gameTime, target.pos());
    }

    @Nonnull
    @Override
    public MapCodec<AncientCompassAngleProperty> type() {
        return MAP_CODEC;
    }

    private float getRandomlySpinningRotation(int seed, long gameTime) {
        if (this.noTargetWobbler.shouldUpdate(gameTime)) {
            this.noTargetWobbler.update(gameTime, this.random.nextFloat());
        }

        float rotation = this.noTargetWobbler.rotation() + AncientCompassAngleProperty.hash(seed) / 2.1474836E9F;

        return Mth.positiveModulo(rotation, 1.0F);
    }

    private float getRotationTowardsTarget(ItemOwner owner, long gameTime, BlockPos targetPos) {
        float angleToTarget = (float) getAngleFromEntityToPos(owner, targetPos);
        float ownerYRotation = getWrappedVisualRotationY(owner);
        float rotation;
        if (owner.asLivingEntity() instanceof Player player && player.isLocalPlayer() && player.level().tickRateManager().runsNormally()) {
            if (this.wobbler.shouldUpdate(gameTime)) {
                this.wobbler.update(gameTime, 0.5F - (ownerYRotation - 0.25F));
            }

            rotation = angleToTarget + this.wobbler.rotation();
        } else {
            rotation = 0.5F - (ownerYRotation - 0.25F - angleToTarget);
        }

        return Mth.positiveModulo(rotation, 1.0F);
    }

    @Nullable
    private static GlobalPos target(ItemStack stack) {
        return stack.get(ModDataComponentTypes.ancientCityPos);
    }

    private static boolean isValidTarget(ItemOwner owner, @Nullable GlobalPos target) {
        return target != null
                && target.dimension() == owner.level().dimension()
                && !(target.pos().distToCenterSqr(owner.position()) < 1.0E-5F);
    }

    private static double getAngleFromEntityToPos(ItemOwner owner, BlockPos position) {
        Vec3 target = Vec3.atCenterOf(position);
        Vec3 ownerPosition = owner.position();
        return Math.atan2(target.z() - ownerPosition.z(), target.x() - ownerPosition.x()) / (float) (Math.PI * 2);
    }

    private static float getWrappedVisualRotationY(ItemOwner owner) {
        return Mth.positiveModulo(owner.getVisualRotationYInDegrees() / 360.0F, 1.0F);
    }

    private static int hash(int input) {
        return input * 1327217883;
    }
}
