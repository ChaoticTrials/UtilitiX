package de.melanx.utilitix.content.track.carts;

import io.github.noeppi_noeppi.libx.base.ItemBase;
import io.github.noeppi_noeppi.libx.mod.ModX;
import io.github.noeppi_noeppi.libx.mod.registration.Registerable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockSource;
import net.minecraft.core.Direction;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.core.dispenser.DispenseItemBehavior;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseRailBlock;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.RailShape;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nonnull;
import java.util.function.Consumer;

public class ItemCart extends ItemBase implements Registerable {

    public final EntityType<? extends Cart> minecartType;

    public ItemCart(ModX mod, EntityType<? extends Cart> minecartType, Properties properties) {
        super(mod, properties);
        this.minecartType = minecartType;
    }

    @Override
    public void registerCommon(ResourceLocation id, Consumer<Runnable> defer) {
        defer.accept(() -> DispenserBlock.registerBehavior(this, this.dispenseBehaviour));
    }

    @Nonnull
    @Override
    protected String getOrCreateDescriptionId() {
        return this.minecartType.getDescriptionId();
    }

    @Nonnull
    @Override
    public InteractionResult useOn(@Nonnull UseOnContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        BlockState state = level.getBlockState(pos);
        if (state.is(BlockTags.RAILS)) {
            ItemStack stack = context.getItemInHand();
            if (!level.isClientSide) {
                Cart cart = this.minecartType.create(level);
                if (cart == null) {
                    return InteractionResult.FAIL;
                }
                RailShape rail = state.getBlock() instanceof BaseRailBlock ? ((BaseRailBlock) state.getBlock()).getRailDirection(state, level, pos, null) : RailShape.NORTH_SOUTH;
                cart.setPos(pos.getX() + 0.5, pos.getY() + (rail.isAscending() ? 0.5625 : 0.0625), pos.getZ() + 0.5);
                cart.setDeltaMovement(Vec3.ZERO);
                cart.xo = pos.getX() + 0.5;
                cart.yo = pos.getY() + (rail.isAscending() ? 0.5625 : 0.0625);
                cart.zo = pos.getZ() + 0.5;
                if (stack.hasCustomHoverName()) {
                    cart.setCustomName(stack.getHoverName());
                }
                level.addFreshEntity(cart);
            }
            stack.shrink(1);
            return InteractionResult.sidedSuccess(level.isClientSide);
        } else {
            return InteractionResult.FAIL;
        }
    }

    public final DispenseItemBehavior dispenseBehaviour = new DefaultDispenseItemBehavior() {

        private final DefaultDispenseItemBehavior defaultDispense = new DefaultDispenseItemBehavior();

        @Nonnull
        @Override
        public ItemStack execute(@Nonnull BlockSource source, @Nonnull ItemStack stack) {
            Direction dir = source.getBlockState().getValue(DispenserBlock.FACING);
            Level world = source.getLevel();
            double x = source.x() + (dir.getStepX() * 1.125);
            double y = Math.floor(source.y()) + dir.getStepY();
            double z = source.z() + (dir.getStepZ() * 1.125);
            BlockPos target = source.getPos().relative(dir);
            BlockState targetState = world.getBlockState(target);
            RailShape rail = targetState.getBlock() instanceof BaseRailBlock ? ((BaseRailBlock) targetState.getBlock()).getRailDirection(targetState, world, target, null) : RailShape.NORTH_SOUTH;
            double yOffset;
            if (targetState.is(BlockTags.RAILS)) {
                yOffset = rail.isAscending() ? 0.6 : 0.1;
            } else {
                if (!targetState.isAir() || !world.getBlockState(target.below()).is(BlockTags.RAILS)) {
                    return this.defaultDispense.dispense(source, stack);
                }

                BlockState railState = world.getBlockState(target.below());
                RailShape railDown = railState.getBlock() instanceof BaseRailBlock ? ((BaseRailBlock) railState.getBlock()).getRailDirection(railState, world, target.below(), null) : RailShape.NORTH_SOUTH;
                yOffset = dir != Direction.DOWN && railDown.isAscending() ? -0.4 : -0.9;
            }

            Cart cart = ItemCart.this.minecartType.create(world);
            if (cart == null) {
                return this.defaultDispense.dispense(source, stack);
            }
            cart.setPos(x, y + yOffset, z);
            cart.setDeltaMovement(Vec3.ZERO);
            cart.xo = x;
            cart.yo = y + yOffset;
            cart.zo = z;
            if (stack.hasCustomHoverName()) {
                cart.setCustomName(stack.getHoverName());
            }
            world.addFreshEntity(cart);
            stack.shrink(1);
            return stack;
        }

        @Override
        protected void playSound(BlockSource source) {
            source.getLevel().levelEvent(1000, source.getPos(), 0);
        }
    };
}
