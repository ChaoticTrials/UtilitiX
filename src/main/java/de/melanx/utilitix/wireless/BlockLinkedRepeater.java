package de.melanx.utilitix.wireless;

import de.melanx.utilitix.registration.ModItems;
import io.github.noeppi_noeppi.libx.mod.ModX;
import io.github.noeppi_noeppi.libx.mod.registration.BlockTE;
import net.minecraft.block.*;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.TickPriority;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fml.client.registry.ClientRegistry;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Random;
import java.util.UUID;
import java.util.function.Consumer;

public class BlockLinkedRepeater extends BlockTE<TileLinkedRepeater> {

    public static final VoxelShape SHAPE = Block.makeCuboidShape(0, 0, 0, 16, 2, 16);

    public BlockLinkedRepeater(ModX mod, Properties properties) {
        this(mod, properties, new Item.Properties());
    }

    public BlockLinkedRepeater(ModX mod, Properties properties, Item.Properties itemProperties) {
        super(mod, TileLinkedRepeater.class, properties, itemProperties);
        this.setDefaultState(this.getStateContainer().getBaseState()
                .with(BlockStateProperties.HORIZONTAL_FACING, Direction.NORTH)
                .with(BlockStateProperties.EYE, false)
                .with(BlockStateProperties.POWER_0_15, 0)
        );
    }

    @Override
    public void registerClient(ResourceLocation id, Consumer<Runnable> defer) {
        ClientRegistry.bindTileEntityRenderer(this.getTileType(), TesrLinkedRepeater::new);
    }

    @Override
    protected void fillStateContainer(@Nonnull StateContainer.Builder<Block, BlockState> builder) {
        super.fillStateContainer(builder);
        builder.add(BlockStateProperties.HORIZONTAL_FACING, BlockStateProperties.EYE, BlockStateProperties.POWER_0_15);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context) {
        return this.getDefaultState()
                .with(BlockStateProperties.HORIZONTAL_FACING, context.getPlacementHorizontalFacing().getOpposite())
                .with(BlockStateProperties.EYE, false)
                .with(BlockStateProperties.POWER_0_15, 0);
    }

    @Nonnull
    @Override
    @SuppressWarnings("deprecation")
    public ActionResultType onBlockActivated(@Nonnull BlockState state, @Nonnull World world, @Nonnull BlockPos pos, @Nonnull PlayerEntity player, @Nonnull Hand hand, @Nonnull BlockRayTraceResult hit) {
        if (!world.isRemote) {
            TileLinkedRepeater tile = this.getTile(world, pos);
            ItemStack link = tile.getLink();
            if (!link.isEmpty()) {
                ItemEntity entity = new ItemEntity(world, pos.getX() + 0.5D, pos.getY() + 0.1D, pos.getZ() + 0.5D, link.copy());
                world.addEntity(entity);
                tile.setLink(ItemStack.EMPTY);
            } else {
                ItemStack held = player.getHeldItem(hand);
                if (!held.isEmpty() && held.getItem() == ModItems.linkedCrystal && ItemLinkedCrystal.getId(held) != null) {
                    tile.setLink(held.split(1));
                    player.setHeldItem(hand, held);
                } else {
                    return ActionResultType.FAIL;
                }
            }
        }
        return ActionResultType.successOrConsume(world.isRemote);
    }

    @Override
    public void onReplaced(@Nonnull BlockState state, @Nonnull World world, @Nonnull BlockPos pos, @Nonnull BlockState newState, boolean isMoving) {
        if (state.hasTileEntity() && (!state.matchesBlock(newState.getBlock()) || !newState.hasTileEntity())) {
            ItemStack stack = this.getTile(world, pos).getLink();
            if (!stack.isEmpty()) {
                ItemEntity entity = new ItemEntity(world, pos.getX() + 0.5D, pos.getY() + 0.1D, pos.getZ() + 0.5D, stack.copy());
                world.addEntity(entity);
            }
        }
        super.onReplaced(state, world, pos, newState, isMoving);
        this.notifyNeighbors(world, pos, state);
    }

    @Override
    protected boolean shouldDropInventory(World world, BlockPos pos, BlockState state) {
        return false;
    }

    @Nonnull
    @Override
    @SuppressWarnings("deprecation")
    public VoxelShape getShape(@Nonnull BlockState state, @Nonnull IBlockReader world, @Nonnull BlockPos pos, @Nonnull ISelectionContext context) {
        return SHAPE;
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean isValidPosition(@Nonnull BlockState state, @Nonnull IWorldReader world, BlockPos pos) {
        return hasSolidSideOnTop(world, pos.down());
    }

    @Override
    @SuppressWarnings("deprecation")
    public int getStrongPower(BlockState state, @Nonnull IBlockReader world, @Nonnull BlockPos pos, @Nonnull Direction side) {
        return state.getWeakPower(world, pos, side);
    }

    @Override
    @SuppressWarnings("deprecation")
    public int getWeakPower(BlockState state, @Nonnull IBlockReader world, @Nonnull BlockPos pos, @Nonnull Direction side) {
        return state.get(BlockStateProperties.HORIZONTAL_FACING) == side ? state.get(BlockStateProperties.POWER_0_15) : 0;
    }

    @Override
    @SuppressWarnings("deprecation")
    public void neighborChanged(BlockState state, @Nonnull World world, @Nonnull BlockPos pos, @Nonnull Block block, @Nonnull BlockPos fromPos, boolean isMoving) {
        if (state.isValidPosition(world, pos)) {
            this.updateState(world, pos, state);
        } else {
            world.removeBlock(pos, false);
            for (Direction direction : Direction.values()) {
                world.notifyNeighborsOfStateChange(pos.offset(direction), this);
            }
        }
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean canProvidePower(@Nonnull BlockState state) {
        return true;
    }

    @Override
    public boolean canConnectRedstone(BlockState state, IBlockReader world, BlockPos pos, @Nullable Direction side) {
        return this.canProvidePower(state) && side != null && side.getAxis() == state.get(BlockStateProperties.HORIZONTAL_FACING).getAxis();
    }

    @Override
    public void onBlockPlacedBy(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull BlockState state, LivingEntity living, @Nonnull ItemStack stack) {
        if (inputStrength(world, state, pos) != state.get(BlockStateProperties.POWER_0_15)) {
            world.getPendingBlockTicks().scheduleTick(pos, this, 1);
        }
    }

    @Override
    @SuppressWarnings("deprecation")
    public void onBlockAdded(@Nonnull BlockState state, @Nonnull World world, @Nonnull BlockPos pos, @Nonnull BlockState oldState, boolean isMoving) {
        this.notifyNeighbors(world, pos, state);
    }

    private void updateState(World world, BlockPos pos, BlockState state) {
        if (!world.getPendingBlockTicks().isTickPending(pos, this)) {
            TickPriority priority = TickPriority.HIGH;
            Block targetBlock = world.getBlockState(pos.offset(state.get(BlockStateProperties.HORIZONTAL_FACING))).getBlock();
            if (targetBlock instanceof RedstoneDiodeBlock || targetBlock instanceof BlockLinkedRepeater) {
                priority = TickPriority.EXTREMELY_HIGH;
            }
            world.getPendingBlockTicks().scheduleTick(pos, this, 1, priority);
        }
    }

    @Override
    @SuppressWarnings("deprecation")
    public void tick(@Nonnull BlockState state, @Nonnull ServerWorld world, @Nonnull BlockPos pos, @Nonnull Random rand) {
        UUID uid = this.getTile(world, pos).getLinkId();
        int input = inputStrength(world, state, pos);
        if (uid != null) {
            WirelessStorage storage = WirelessStorage.get(world);
            storage.update(world, uid, new WorldAndPos(world.getDimensionKey(), pos), input);
            input = storage.getStrength(uid);
        }
        if (input != state.get(BlockStateProperties.POWER_0_15)) {
            world.setBlockState(pos, state.with(BlockStateProperties.POWER_0_15, input), 2);
        }
    }

    private void notifyNeighbors(World world, BlockPos pos, BlockState state) {
        Direction face = state.get(BlockStateProperties.HORIZONTAL_FACING);
        BlockPos target = pos.offset(face.getOpposite());
        if (net.minecraftforge.event.ForgeEventFactory.onNeighborNotify(world, pos, world.getBlockState(pos), java.util.EnumSet.of(face.getOpposite()), false).isCanceled())
            return;
        world.neighborChanged(target, this, pos);
        world.notifyNeighborsOfStateExcept(target, this, face);
    }

    public static int inputStrength(World world, BlockState state, BlockPos pos) {
        Direction face = state.get(BlockStateProperties.HORIZONTAL_FACING);
        BlockPos targetPos = pos.offset(face);
        int i = world.getRedstonePower(targetPos, face);
        if (i >= 15) {
            return i;
        } else {
            BlockState targetState = world.getBlockState(targetPos);
            return Math.max(i, targetState.matchesBlock(Blocks.REDSTONE_WIRE) ? targetState.get(BlockStateProperties.POWER_0_15) : 0);
        }
    }
}
