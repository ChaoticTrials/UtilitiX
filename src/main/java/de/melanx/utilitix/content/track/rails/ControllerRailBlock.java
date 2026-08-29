package de.melanx.utilitix.content.track.rails;

import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.util.Function3;
import de.melanx.utilitix.content.track.tinkerer.MinecartTinkererMenu;
import de.melanx.utilitix.registration.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
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

public abstract class ControllerRailBlock<T extends ControllerRailBlockEntity> extends RailBlock implements EntityBlock, MaxSpeedRail {

    private final BlockEntityType<T> beType;
    public final boolean reinforced;

    public ControllerRailBlock(ModX mod, Function3<BlockEntityType<T>, BlockPos, BlockState, T> ctor, boolean reinforced, BlockBehaviour.Properties properties) {
        this(mod, ctor, reinforced, properties, new Item.Properties());
    }

    public ControllerRailBlock(ModX mod, Function3<BlockEntityType<T>, BlockPos, BlockState, T> ctor, boolean reinforced, BlockBehaviour.Properties properties, Item.Properties itemProperties) {
        super(mod, false, properties, itemProperties);
        this.beType = new BlockEntityType<>((pos, state) -> ctor.apply(this.getBlockEntityType(), pos, state), ImmutableSet.of(this));
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
    protected InteractionResult useItemOn(@Nonnull ItemStack stack, @Nonnull BlockState state, @Nonnull Level level, @Nonnull BlockPos pos, @Nonnull Player player, @Nonnull InteractionHand hand, @Nonnull BlockHitResult hitResult) {
        ItemStack held = player.getItemInHand(hand);
        if (held.isEmpty() || held.getItem() != ModItems.minecartTinkerer || !player.isShiftKeyDown()) {
            return super.useItemOn(stack, state, level, pos, player, hand, hitResult);
        }
        if (!level.isClientSide() && player instanceof ServerPlayer serverPlayer) {
            MinecartTinkererMenu.open(serverPlayer, pos);
        }

        return InteractionResult.SUCCESS;
    }

    @Override
    public double getMaxRailSpeed() {
        return this.reinforced ? 0.7 : 0.4;
    }

    public T getBlockEntity(BlockGetter level, BlockPos pos) {
        BlockEntity be = level.getBlockEntity(pos);
        if (be != null) {
            //noinspection unchecked
            return (T) be;
        }

        throw new IllegalStateException("Expected a controller rail block entity at " + pos + ".");
    }

    public BlockEntityType<T> getBlockEntityType() {
        return this.beType;
    }
}
