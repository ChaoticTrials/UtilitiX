package de.melanx.utilitix.content.track.carts;

import io.github.noeppi_noeppi.libx.mod.ModX;
import io.github.noeppi_noeppi.libx.mod.registration.ItemBase;
import io.github.noeppi_noeppi.libx.mod.registration.Registerable;
import net.minecraft.block.AbstractRailBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.DispenserBlock;
import net.minecraft.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.dispenser.IDispenseItemBehavior;
import net.minecraft.entity.EntityType;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.state.properties.RailShape;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import java.util.function.Consumer;

public class ItemCart extends ItemBase implements Registerable {

    public final EntityType<? extends EntityCart> minecartType;

    public ItemCart(ModX mod, EntityType<? extends EntityCart> minecartType, Properties properties) {
        super(mod, properties);
        this.minecartType = minecartType;
    }

    @Override
    public void registerCommon(ResourceLocation id, Consumer<Runnable> defer) {
        defer.accept(() -> DispenserBlock.registerDispenseBehavior(this, this.dispenseBehaviour));
    }
    
    @Nonnull
    @Override
    protected String getDefaultTranslationKey() {
        return this.minecartType.getTranslationKey();
    }

    @Nonnull
    @Override
    public ActionResultType onItemUse(@Nonnull ItemUseContext context) {
        World world = context.getWorld();
        BlockPos pos = context.getPos();
        BlockState state = world.getBlockState(pos);
        if (state.isIn(BlockTags.RAILS)) {
            ItemStack stack = context.getItem();
            if (!world.isRemote) {
                EntityCart cart = this.minecartType.create(world);
                if (cart == null) {
                    return ActionResultType.FAIL;
                }
                RailShape rail = state.getBlock() instanceof AbstractRailBlock ? ((AbstractRailBlock) state.getBlock()).getRailDirection(state, world, pos, null) : RailShape.NORTH_SOUTH;
                cart.setPosition(pos.getX() + 0.5, pos.getY() + (rail.isAscending() ? 0.5625 : 0.0625), pos.getZ() + 0.5);
                cart.setMotion(Vector3d.ZERO);
                cart.prevPosX = pos.getX() + 0.5;
                cart.prevPosY = pos.getY() + (rail.isAscending() ? 0.5625 : 0.0625);
                cart.prevPosZ = pos.getZ() + 0.5;
                if (stack.hasDisplayName()) {
                    cart.setCustomName(stack.getDisplayName());
                }
                world.addEntity(cart);
            }
            stack.shrink(1);
            return ActionResultType.successOrConsume(world.isRemote);
        } else {
            return ActionResultType.FAIL;
        }
    }
    
    public final IDispenseItemBehavior dispenseBehaviour = new DefaultDispenseItemBehavior() {
        
        private final DefaultDispenseItemBehavior defaultDispense = new DefaultDispenseItemBehavior();
        
        @Nonnull
        @Override
        public ItemStack dispenseStack(@Nonnull IBlockSource block, @Nonnull ItemStack stack) {
            Direction dir = block.getBlockState().get(DispenserBlock.FACING);
            World world = block.getWorld();
            double x = block.getX() + (dir.getXOffset() * 1.125);
            double y = Math.floor(block.getY()) + dir.getYOffset();
            double z = block.getZ() + (dir.getZOffset() * 1.125);
            BlockPos target = block.getBlockPos().offset(dir);
            BlockState targetState = world.getBlockState(target);
            RailShape rail = targetState.getBlock() instanceof AbstractRailBlock ? ((AbstractRailBlock) targetState.getBlock()).getRailDirection(targetState, world, target, null) : RailShape.NORTH_SOUTH;
            double yOffset;
            if (targetState.isIn(BlockTags.RAILS)) {
                yOffset = rail.isAscending() ? 0.6 : 0.1;
            } else {
                //noinspection deprecation
                if (!targetState.isAir() || !world.getBlockState(target.down()).isIn(BlockTags.RAILS)) {
                    return this.defaultDispense.dispense(block, stack);
                }

                BlockState railState = world.getBlockState(target.down());
                RailShape railDown = railState.getBlock() instanceof AbstractRailBlock ? ((AbstractRailBlock)railState.getBlock()).getRailDirection(railState, world, target.down(), null) : RailShape.NORTH_SOUTH;
                yOffset = dir != Direction.DOWN && railDown.isAscending() ? -0.4 : -0.9;
            }

            EntityCart cart = ItemCart.this.minecartType.create(world);
            if (cart == null) {
                return this.defaultDispense.dispense(block, stack);
            }
            cart.setPosition(x, y + yOffset, z);
            cart.setMotion(Vector3d.ZERO);
            cart.prevPosX = x;
            cart.prevPosY = y + yOffset;
            cart.prevPosZ = z;
            if (stack.hasDisplayName()) {
                cart.setCustomName(stack.getDisplayName());
            }
            world.addEntity(cart);
            stack.shrink(1);
            return stack;
        }
        
        @Override
        protected void playDispenseSound(IBlockSource source) {
            source.getWorld().playEvent(1000, source.getBlockPos(), 0);
        }
    };
}
