package de.melanx.utilitix.content.wireless;

import de.melanx.utilitix.registration.ModItems;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.GlobalPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
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
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.ForgeEventFactory;
import org.moddingx.libx.base.tile.BlockBE;
import org.moddingx.libx.mod.ModX;
import org.moddingx.libx.registration.SetupContext;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.UUID;

public class BlockLinkedRepeater extends BlockBE<TileLinkedRepeater> {

    public static final VoxelShape SHAPE = Block.box(0, 0, 0, 16, 2, 16);

    public BlockLinkedRepeater(ModX mod, Properties properties) {
        this(mod, properties, new Item.Properties());
    }

    public BlockLinkedRepeater(ModX mod, Properties properties, Item.Properties itemProperties) {
        super(mod, TileLinkedRepeater.class, properties, itemProperties);
        this.registerDefaultState(this.defaultBlockState()
                .setValue(BlockStateProperties.HORIZONTAL_FACING, Direction.NORTH)
                .setValue(BlockStateProperties.EYE, false)
                .setValue(BlockStateProperties.POWER, 0)
        );
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void registerClient(SetupContext ctx) {
        BlockEntityRenderers.register(this.getBlockEntityType(), context -> new BesrLinkedRepeater());
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
    @SuppressWarnings("deprecation")
    public InteractionResult use(@Nonnull BlockState state, @Nonnull Level level, @Nonnull BlockPos pos, @Nonnull Player player, @Nonnull InteractionHand hand, @Nonnull BlockHitResult hit) {
        if (!level.isClientSide) {
            TileLinkedRepeater tile = this.getBlockEntity(level, pos);
            ItemStack link = tile.getLink();
            if (!link.isEmpty()) {
                ItemEntity entity = new ItemEntity(level, pos.getX() + 0.5D, pos.getY() + 0.1D, pos.getZ() + 0.5D, link.copy());
                level.addFreshEntity(entity);
                tile.setLink(ItemStack.EMPTY);
            } else {
                ItemStack held = player.getItemInHand(hand);
                if (!held.isEmpty() && held.getItem() == ModItems.linkedCrystal && ItemLinkedCrystal.getId(held) != null) {
                    tile.setLink(held.split(1));
                    player.setItemInHand(hand, held);
                } else {
                    return InteractionResult.FAIL;
                }
            }
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    @Override
    public void onRemove(@Nonnull BlockState state, @Nonnull Level level, @Nonnull BlockPos pos, @Nonnull BlockState newState, boolean isMoving) {
        if (state.hasBlockEntity() && (!state.is(newState.getBlock()) || !newState.hasBlockEntity())) {
            TileLinkedRepeater tile = this.getBlockEntity(level, pos);
            WirelessStorage.get(level).remove(level, tile.getLinkId(), GlobalPos.of(level.dimension(), pos));
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
    @SuppressWarnings("deprecation")
    public VoxelShape getShape(@Nonnull BlockState state, @Nonnull BlockGetter level, @Nonnull BlockPos pos, @Nonnull CollisionContext context) {
        return SHAPE;
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean canSurvive(@Nonnull BlockState state, @Nonnull LevelReader level, BlockPos pos) {
        return canSupportRigidBlock(level, pos.below());
    }

    @Override
    @SuppressWarnings("deprecation")
    public int getDirectSignal(BlockState blockState, @Nonnull BlockGetter blockAccess, @Nonnull BlockPos pos, @Nonnull Direction side) {
        return blockState.getSignal(blockAccess, pos, side);
    }

    @Override
    @SuppressWarnings("deprecation")
    public int getSignal(BlockState blockState, @Nonnull BlockGetter blockAccess, @Nonnull BlockPos pos, @Nonnull Direction side) {
        return blockState.getValue(BlockStateProperties.HORIZONTAL_FACING) == side ? blockState.getValue(BlockStateProperties.POWER) : 0;
    }

    @Override
    @SuppressWarnings("deprecation")
    public void neighborChanged(BlockState state, @Nonnull Level level, @Nonnull BlockPos pos, @Nonnull Block block, @Nonnull BlockPos fromPos, boolean isMoving) {
        if (state.canSurvive(level, pos)) {
            this.updateState(level, pos, state);
        } else {
            level.removeBlock(pos, false);
            for (Direction direction : Direction.values()) {
                level.updateNeighborsAt(pos.relative(direction), this);
            }
        }
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean isSignalSource(@Nonnull BlockState state) {
        return true;
    }

    @Override
    public boolean canConnectRedstone(BlockState state, BlockGetter level, BlockPos pos, @Nullable Direction direction) {
        return this.isSignalSource(state) && direction != null && direction.getAxis() == state.getValue(BlockStateProperties.HORIZONTAL_FACING).getAxis();
    }

    @Override
    public void setPlacedBy(@Nonnull Level level, @Nonnull BlockPos pos, @Nonnull BlockState state, LivingEntity placer, @Nonnull ItemStack stack) {
        if (inputStrength(level, state, pos) != state.getValue(BlockStateProperties.POWER)) {
            level.scheduleTick(pos, this, 1);
        }
    }

    @Override
    @SuppressWarnings("deprecation")
    public void onPlace(@Nonnull BlockState state, @Nonnull Level level, @Nonnull BlockPos pos, @Nonnull BlockState oldState, boolean isMoving) {
        this.notifyNeighbors(level, pos, state);
    }

    private void updateState(Level level, BlockPos pos, BlockState state) {
        if (!level.getBlockTicks().willTickThisTick(pos, this)) {
            TickPriority priority = TickPriority.HIGH;
            Block targetBlock = level.getBlockState(pos.relative(state.getValue(BlockStateProperties.HORIZONTAL_FACING))).getBlock();
            if (targetBlock instanceof DiodeBlock || targetBlock instanceof BlockLinkedRepeater) {
                priority = TickPriority.EXTREMELY_HIGH;
            }
            level.scheduleTick(pos, this, 1, priority);
        }
    }

    @Override
    @SuppressWarnings("deprecation")
    public void tick(@Nonnull BlockState state, @Nonnull ServerLevel level, @Nonnull BlockPos pos, @Nonnull RandomSource rand) {
        UUID uid = this.getBlockEntity(level, pos).getLinkId();
        int input = inputStrength(level, state, pos);
        if (uid != null) {
            WirelessStorage storage = WirelessStorage.get(level);
            storage.update(level, uid, GlobalPos.of(level.dimension(), pos), input);
            input = storage.getStrength(uid);
        }
        if (input != state.getValue(BlockStateProperties.POWER)) {
            level.setBlock(pos, state.setValue(BlockStateProperties.POWER, input), 2);
        }
    }

    private void notifyNeighbors(Level level, BlockPos pos, BlockState state) {
        Direction face = state.getValue(BlockStateProperties.HORIZONTAL_FACING);
        BlockPos target = pos.relative(face.getOpposite());
        if (ForgeEventFactory.onNeighborNotify(level, pos, level.getBlockState(pos), java.util.EnumSet.of(face.getOpposite()), false).isCanceled())
            return;
        level.neighborChanged(target, this, pos);
        level.updateNeighborsAtExceptFromFacing(target, this, face);
    }

    public static int inputStrength(Level level, BlockState state, BlockPos pos) {
        Direction face = state.getValue(BlockStateProperties.HORIZONTAL_FACING);
        BlockPos targetPos = pos.relative(face);
        int i = level.getSignal(targetPos, face);
        if (i >= 15) {
            return 15;
        } else {
            BlockState targetState = level.getBlockState(targetPos);
            return Math.max(i, targetState.is(Blocks.REDSTONE_WIRE) ? targetState.getValue(BlockStateProperties.POWER) : 0);
        }
    }
}
