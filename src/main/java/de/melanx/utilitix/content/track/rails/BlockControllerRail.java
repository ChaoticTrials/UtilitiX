package de.melanx.utilitix.content.track.rails;

import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.util.Function3;
import de.melanx.utilitix.registration.ModItems;
import io.github.noeppi_noeppi.libx.menu.GenericMenu;
import io.github.noeppi_noeppi.libx.mod.ModX;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.state.properties.RailShape;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Set;

public abstract class BlockControllerRail<T extends TileControllerRail> extends BlockRail implements EntityBlock {

    private final BlockEntityType<T> beType;
    public final boolean reinforced;

    public BlockControllerRail(ModX mod, Function3<BlockEntityType<T>, BlockPos, BlockState, T> ctor, boolean reinforced, BlockBehaviour.Properties properties) {
        this(mod, ctor, reinforced, properties, new Item.Properties());
    }

    public BlockControllerRail(ModX mod, Function3<BlockEntityType<T>, BlockPos, BlockState, T> ctor, boolean reinforced, BlockBehaviour.Properties properties, Item.Properties itemProperties) {
        super(mod, false, properties, itemProperties);
        //noinspection ConstantConditions
        this.beType = new BlockEntityType<>((pos, state) -> ctor.apply(this.getTileType(), pos, state), ImmutableSet.of(this), null);
        this.reinforced = reinforced;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(@Nonnull BlockPos pos, @Nonnull BlockState state) {
        return this.beType.create(pos, state);
    }

    @Override
    public Set<Object> getAdditionalRegisters(ResourceLocation id) {
        return ImmutableSet.builder().addAll(super.getAdditionalRegisters(id)).add(this.beType).build();
    }

    @Nonnull
    @Override
    public abstract Property<RailShape> getShapeProperty();

    @Nonnull
    @Override
    @SuppressWarnings("deprecation")
    public InteractionResult use(@Nonnull BlockState state, @Nonnull Level level, @Nonnull BlockPos pos, @Nonnull Player player, @Nonnull InteractionHand hand, @Nonnull BlockHitResult hit) {
        ItemStack held = player.getItemInHand(hand);
        if (!held.isEmpty() && held.getItem() == ModItems.minecartTinkerer && player.isShiftKeyDown()) {
            if (!level.isClientSide && player instanceof ServerPlayer) {
                TileControllerRail tile = this.getTile(level, pos);
                IItemHandlerModifiable handler = new ItemStackHandler(1) {

                    @Override
                    public int getSlotLimit(int slot) {
                        return 1;
                    }

                    @Override
                    protected void onContentsChanged(int slot) {
                        if (slot == 0) {
                            tile.setFilterStack(this.getStackInSlot(0));
                        }
                    }
                };
                handler.setStackInSlot(0, tile.getFilterStack().copy());
                GenericMenu.open((ServerPlayer) player, handler, new TranslatableComponent("screen.utilitix.minecart_tinkerer"), null);
            }
            return InteractionResult.sidedSuccess(level.isClientSide);
        }
        return super.use(state, level, pos, player, hand, hit);
    }

    @Override
    public void onRemove(BlockState state, @Nonnull Level level, @Nonnull BlockPos pos, @Nonnull BlockState newState, boolean isMoving) {
        if (state.hasBlockEntity() && (!state.is(newState.getBlock()) || !newState.hasBlockEntity())) {
            ItemStack stack = this.getTile(level, pos).getFilterStack();
            if (!stack.isEmpty()) {
                ItemEntity entity = new ItemEntity(level, pos.getX() + 0.5D, pos.getY() + 0.1D, pos.getZ() + 0.5D, stack.copy());
                level.addFreshEntity(entity);
            }
        }
        super.onRemove(state, level, pos, newState, isMoving);
    }

    public T getTile(BlockGetter level, BlockPos pos) {
        BlockEntity be = level.getBlockEntity(pos);
        if (be != null) {
            //noinspection unchecked
            return (T) be;
        } else {
            throw new IllegalStateException("Expected a controller rail tile entity at " + pos + ".");
        }
    }

    public BlockEntityType<T> getTileType() {
        return this.beType;
    }

    @Override
    public float getRailMaxSpeed(BlockState state, Level level, BlockPos pos, AbstractMinecart cart) {
        return this.reinforced ? 0.7f : 0.4f;
    }
}
