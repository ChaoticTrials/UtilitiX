package de.melanx.utilitix.content.track.carts;

import de.melanx.utilitix.config.FeatureConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.dispenser.BlockSource;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.core.dispenser.DispenseItemBehavior;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseRailBlock;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.RailShape;
import net.minecraft.world.phys.Vec3;
import org.moddingx.libx.base.ItemBase;
import org.moddingx.libx.mod.ModX;
import org.moddingx.libx.registration.Registerable;
import org.moddingx.libx.registration.SetupContext;

import javax.annotation.Nonnull;

public class BaseCartItem extends ItemBase implements Registerable {

    public final EntityType<? extends BaseCart> minecartType;

    public BaseCartItem(ModX mod, EntityType<? extends BaseCart> minecartType, Properties properties) {
        super(mod, properties.overrideDescription(minecartType.getDescriptionId()));
        this.minecartType = minecartType;
    }

    @Override
    public void setupCommon(SetupContext ctx) {
        ctx.enqueue(() -> DispenserBlock.registerBehavior(this, this.dispenseBehaviour));
    }

    @Nonnull
    @Override
    public InteractionResult useOn(@Nonnull UseOnContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        BlockState state = level.getBlockState(pos);
        if (!state.is(BlockTags.RAILS)) {
            return InteractionResult.FAIL;
        }

        ItemStack stack = context.getItemInHand();
        if (!level.isClientSide()) {
            BaseCart cart = this.minecartType.create(level, EntitySpawnReason.SPAWN_ITEM_USE);
            if (cart == null) {
                return InteractionResult.FAIL;
            }

            RailShape rail = state.getBlock() instanceof BaseRailBlock ? ((BaseRailBlock) state.getBlock()).getRailDirection(state, level, pos, null) : RailShape.NORTH_SOUTH;
            cart.setInitialPos(pos.getX() + 0.5, pos.getY() + (rail.isSlope() ? 0.5625 : 0.0625), pos.getZ() + 0.5);
            cart.setDeltaMovement(Vec3.ZERO);

            if (stack.has(DataComponents.CUSTOM_NAME)) {
                cart.setCustomName(stack.getHoverName());
            }

            level.addFreshEntity(cart);
        }

        stack.shrink(1);

        return ((level.isClientSide() ? InteractionResult.SUCCESS : InteractionResult.SUCCESS_SERVER));
    }

    @Override
    public boolean isEnabled(@Nonnull FeatureFlagSet enabledFeatures) {
        return FeatureConfig.Transportation.moreMinecarts;
    }

    public final DispenseItemBehavior dispenseBehaviour = new DefaultDispenseItemBehavior() {

        private final DefaultDispenseItemBehavior defaultDispense = new DefaultDispenseItemBehavior();

        @Nonnull
        @Override
        public ItemStack execute(@Nonnull BlockSource source, @Nonnull ItemStack stack) {
            Direction dir = source.state().getValue(DispenserBlock.FACING);
            Level world = source.level();
            double x = source.pos().getX() + (dir.getStepX() * 1.125);
            double y = (double) source.pos().getY() + dir.getStepY();
            double z = source.pos().getZ() + (dir.getStepZ() * 1.125);
            BlockPos target = source.pos().relative(dir);
            BlockState targetState = world.getBlockState(target);
            RailShape rail = targetState.getBlock() instanceof BaseRailBlock ? ((BaseRailBlock) targetState.getBlock()).getRailDirection(targetState, world, target, null) : RailShape.NORTH_SOUTH;

            double yOffset;
            if (targetState.is(BlockTags.RAILS)) {
                yOffset = rail.isSlope() ? 0.6 : 0.1;
            } else {
                if (!targetState.isAir() || !world.getBlockState(target.below()).is(BlockTags.RAILS)) {
                    return this.defaultDispense.dispense(source, stack);
                }

                BlockState railState = world.getBlockState(target.below());
                RailShape railDown = railState.getBlock() instanceof BaseRailBlock ? ((BaseRailBlock) railState.getBlock()).getRailDirection(railState, world, target.below(), null) : RailShape.NORTH_SOUTH;
                yOffset = dir != Direction.DOWN && railDown.isSlope() ? -0.4 : -0.9;
            }

            BaseCart cart = BaseCartItem.this.minecartType.create(world, EntitySpawnReason.DISPENSER);
            if (cart == null) {
                return this.defaultDispense.dispense(source, stack);
            }

            cart.setInitialPos(x, y + yOffset, z);
            cart.setDeltaMovement(Vec3.ZERO);

            if (stack.has(DataComponents.CUSTOM_NAME)) {
                cart.setCustomName(stack.getHoverName());
            }

            world.addFreshEntity(cart);
            stack.shrink(1);

            return stack;
        }

        @Override
        protected void playSound(BlockSource source) {
            source.level().levelEvent(1000, source.pos(), 0);
        }
    };
}
