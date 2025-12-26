package de.melanx.utilitix.content.redstone.wireless;

import de.melanx.utilitix.config.FeatureConfig;
import de.melanx.utilitix.registration.ModItems;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.GlobalPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DiodeBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.ticks.TickPriority;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.event.EventHooks;
import org.moddingx.libx.base.tile.BlockBE;
import org.moddingx.libx.mod.ModX;
import org.moddingx.libx.registration.SetupContext;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.EnumSet;
import java.util.UUID;

public class LinkedRepeaterBlock extends BlockBE<LinkedRepeaterBlockEntity> {

    public static final VoxelShape SHAPE = Block.box(0, 0, 0, 16, 2, 16);

    public LinkedRepeaterBlock(ModX mod, Properties properties) {
        this(mod, properties, new Item.Properties());
    }

    public LinkedRepeaterBlock(ModX mod, Properties properties, Item.Properties itemProperties) {
        super(mod, LinkedRepeaterBlockEntity.class, properties, itemProperties);
        this.registerDefaultState(this.defaultBlockState()
                .setValue(BlockStateProperties.HORIZONTAL_FACING, Direction.NORTH)
                .setValue(BlockStateProperties.EYE, false)
                .setValue(BlockStateProperties.POWER, 0)
        );
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void setupClient(SetupContext ctx) {
        ctx.enqueue(() -> BlockEntityRenderers.register(this.getBlockEntityType(), context -> new LinkedRepeaterRenderer()));
    }

    @Override
    protected void createBlockStateDefinition(@Nonnull StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(BlockStateProperties.HORIZONTAL_FACING, BlockStateProperties.EYE, BlockStateProperties.POWER);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState()
                .setValue(BlockStateProperties.HORIZONTAL_FACING, context.getHorizontalDirection().getOpposite())
                .setValue(BlockStateProperties.EYE, false)
                .setValue(BlockStateProperties.POWER, 0);
    }

    @Nonnull
    @Override
    protected ItemInteractionResult useItemOn(@Nonnull ItemStack stack, @Nonnull BlockState state, Level level, @Nonnull BlockPos pos, @Nonnull Player player, @Nonnull InteractionHand hand, @Nonnull BlockHitResult hitResult) {
        if (level.isClientSide) {
            return ItemInteractionResult.SUCCESS;
        }

        LinkedRepeaterBlockEntity tile = this.getBlockEntity(level, pos);
        ItemStack link = tile.getLink();
        if (!link.isEmpty()) {
            ItemEntity entity = new ItemEntity(level, pos.getX() + 0.5D, pos.getY() + 0.1D, pos.getZ() + 0.5D, link.copy());
            level.addFreshEntity(entity);
            tile.setLink(ItemStack.EMPTY);

            return ItemInteractionResult.CONSUME;
        }

        ItemStack held = player.getItemInHand(hand);
        if (!held.isEmpty() && held.getItem() == ModItems.linkedCrystal && LinkedCrystalItem.getId(held) != null) {
            tile.setLink(held.split(1));
            player.setItemInHand(hand, held);

            return ItemInteractionResult.CONSUME;
        }

        return ItemInteractionResult.FAIL;
    }

    @Override
    public void onRemove(@Nonnull BlockState state, @Nonnull Level level, @Nonnull BlockPos pos, @Nonnull BlockState newState, boolean isMoving) {
        if (state.hasBlockEntity() && (!state.is(newState.getBlock()) || !newState.hasBlockEntity())) {
            LinkedRepeaterBlockEntity tile = this.getBlockEntity(level, pos);
            WirelessRedstoneSavedData.get(level).remove(level, tile.getLinkId(), GlobalPos.of(level.dimension(), pos));

            ItemStack stack = tile.getLink();
            if (!stack.isEmpty()) {
                ItemEntity entity = new ItemEntity(level, pos.getX() + 0.5D, pos.getY() + 0.1D, pos.getZ() + 0.5D, stack.copy());
                level.addFreshEntity(entity);
            }
        }

        super.onRemove(state, level, pos, newState, isMoving);
        this.notifyNeighbors(level, pos, state);
    }

    @Override
    protected boolean shouldDropInventory(Level level, BlockPos pos, BlockState state) {
        return false;
    }

    @Nonnull
    @Override
    public VoxelShape getShape(@Nonnull BlockState state, @Nonnull BlockGetter level, @Nonnull BlockPos pos, @Nonnull CollisionContext context) {
        return SHAPE;
    }

    @Override
    public boolean canSurvive(@Nonnull BlockState state, @Nonnull LevelReader level, BlockPos pos) {
        return Block.canSupportRigidBlock(level, pos.below());
    }

    @Override
    public int getDirectSignal(BlockState blockState, @Nonnull BlockGetter blockAccess, @Nonnull BlockPos pos, @Nonnull Direction side) {
        return blockState.getSignal(blockAccess, pos, side);
    }

    @Override
    public int getSignal(BlockState blockState, @Nonnull BlockGetter blockAccess, @Nonnull BlockPos pos, @Nonnull Direction side) {
        return blockState.getValue(BlockStateProperties.HORIZONTAL_FACING) == side ? blockState.getValue(BlockStateProperties.POWER) : 0;
    }

    @Override
    public void neighborChanged(BlockState state, @Nonnull Level level, @Nonnull BlockPos pos, @Nonnull Block block, @Nonnull BlockPos fromPos, boolean isMoving) {
        if (state.canSurvive(level, pos)) {
            this.updateState(level, pos, state);
            return;
        }

        level.removeBlock(pos, false);
        for (Direction direction : Direction.values()) {
            level.updateNeighborsAt(pos.relative(direction), this);
        }
    }

    @Override
    public boolean isSignalSource(@Nonnull BlockState state) {
        return true;
    }

    @Override
    public boolean canConnectRedstone(@Nonnull BlockState state, @Nonnull BlockGetter level, @Nonnull BlockPos pos, @Nullable Direction direction) {
        return this.isSignalSource(state) && direction != null && direction.getAxis() == state.getValue(BlockStateProperties.HORIZONTAL_FACING).getAxis();
    }

    @Override
    public void setPlacedBy(@Nonnull Level level, @Nonnull BlockPos pos, @Nonnull BlockState state, LivingEntity placer, @Nonnull ItemStack stack) {
        if (LinkedRepeaterBlock.inputStrength(level, state, pos) != state.getValue(BlockStateProperties.POWER)) {
            level.scheduleTick(pos, this, 1);
        }
    }

    @Override
    public void onPlace(@Nonnull BlockState state, @Nonnull Level level, @Nonnull BlockPos pos, @Nonnull BlockState oldState, boolean isMoving) {
        this.notifyNeighbors(level, pos, state);
    }

    private void updateState(Level level, BlockPos pos, BlockState state) {
        if (level.getBlockTicks().willTickThisTick(pos, this)) {
            return;
        }

        TickPriority priority = TickPriority.HIGH;

        Block targetBlock = level.getBlockState(pos.relative(state.getValue(BlockStateProperties.HORIZONTAL_FACING))).getBlock();
        if (targetBlock instanceof DiodeBlock || targetBlock instanceof LinkedRepeaterBlock) {
            priority = TickPriority.EXTREMELY_HIGH;
        }

        level.scheduleTick(pos, this, 1, priority);
    }

    @Override
    public void tick(@Nonnull BlockState state, @Nonnull ServerLevel level, @Nonnull BlockPos pos, @Nonnull RandomSource rand) {
        int input = LinkedRepeaterBlock.inputStrength(level, state, pos);

        UUID uid = this.getBlockEntity(level, pos).getLinkId();
        if (uid != null) {
            WirelessRedstoneSavedData storage = WirelessRedstoneSavedData.get(level);
            storage.update(level, uid, GlobalPos.of(level.dimension(), pos), input);
            input = storage.getStrength(uid);
        }

        if (input != state.getValue(BlockStateProperties.POWER)) {
            level.setBlock(pos, state.setValue(BlockStateProperties.POWER, input), Block.UPDATE_CLIENTS);
        }
    }

    private void notifyNeighbors(Level level, BlockPos pos, BlockState state) {
        Direction face = state.getValue(BlockStateProperties.HORIZONTAL_FACING);
        BlockPos target = pos.relative(face.getOpposite());
        if (EventHooks.onNeighborNotify(level, pos, level.getBlockState(pos), EnumSet.of(face.getOpposite()), false).isCanceled()) {
            return;
        }

        level.neighborChanged(target, this, pos);
        level.updateNeighborsAtExceptFromFacing(target, this, face);
    }

    @Override
    public boolean isEnabled(@Nonnull FeatureFlagSet enabledFeatures) {
        return FeatureConfig.Misc.Redstone.wirelessRedstone;
    }

    public static int inputStrength(Level level, BlockState state, BlockPos pos) {
        Direction face = state.getValue(BlockStateProperties.HORIZONTAL_FACING);
        BlockPos targetPos = pos.relative(face);

        int signalStrength = level.getSignal(targetPos, face);
        if (signalStrength >= 15) {
            return 15;
        }

        BlockState targetState = level.getBlockState(targetPos);

        return Math.max(signalStrength, targetState.is(Blocks.REDSTONE_WIRE) ? targetState.getValue(BlockStateProperties.POWER) : 0);
    }
}
