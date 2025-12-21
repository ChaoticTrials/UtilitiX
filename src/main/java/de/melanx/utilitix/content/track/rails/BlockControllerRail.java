package de.melanx.utilitix.content.track.rails;

import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.util.Function3;
import de.melanx.utilitix.content.track.tinkerer.MinecartTinkererMenu;
import de.melanx.utilitix.registration.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
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
import net.minecraft.world.phys.BlockHitResult;
import org.moddingx.libx.mod.ModX;
import org.moddingx.libx.registration.RegistrationContext;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

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
    public void registerAdditional(RegistrationContext ctx, EntryCollector builder) {
        super.registerAdditional(ctx, builder);
        builder.register(Registries.BLOCK_ENTITY_TYPE, this.beType);
    }

    @Nonnull
    @Override
    protected ItemInteractionResult useItemOn(@Nonnull ItemStack stack, @Nonnull BlockState state, @Nonnull Level level, @Nonnull BlockPos pos, Player player, @Nonnull InteractionHand hand, @Nonnull BlockHitResult hitResult) {
        ItemStack held = player.getItemInHand(hand);
        if (!held.isEmpty() && held.getItem() == ModItems.minecartTinkerer && player.isShiftKeyDown()) {
            if (!level.isClientSide && player instanceof ServerPlayer serverPlayer) {
                MinecartTinkererMenu.open(serverPlayer, pos);
            }

            return ItemInteractionResult.sidedSuccess(level.isClientSide);
        }

        return super.useItemOn(stack, state, level, pos, player, hand, hitResult);
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
    public float getRailMaxSpeed(@Nonnull BlockState state, @Nonnull Level level, @Nonnull BlockPos pos, @Nonnull AbstractMinecart cart) {
        return this.reinforced ? 0.7f : 0.4f;
    }
}
