package de.melanx.utilitix.content.track.carts;

import com.google.common.collect.ImmutableList;
import de.melanx.utilitix.content.track.carts.piston.PistonCartContainerMenu;
import de.melanx.utilitix.content.track.carts.piston.PistonCartMode;
import de.melanx.utilitix.content.track.rails.BlockPistonControllerRail;
import de.melanx.utilitix.registration.ModItemTags;
import de.melanx.utilitix.registration.ModSerializers;
import io.github.noeppi_noeppi.libx.inventory.BaseItemStackHandler;
import io.netty.buffer.Unpooled;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.DirectionalPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseRailBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.RailShape;
import net.minecraftforge.fmllegacy.network.NetworkHooks;
import net.minecraftforge.items.IItemHandlerModifiable;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.NoSuchElementException;

public class PistonCart extends Cart {

    private static final EntityDataAccessor<PistonCartMode> MODE = SynchedEntityData.defineId(PistonCart.class, ModSerializers.pistonCartMode);

    private PistonCartMode mode = PistonCartMode.IDLE;
    private final BaseItemStackHandler railIn;
    private final BaseItemStackHandler torchIn;
    private final BaseItemStackHandler railOut;

    public PistonCart(EntityType<?> type, Level level) {
        super(type, level);
        this.railIn = BaseItemStackHandler.builder(12)
                .validator(stack -> ItemTags.RAILS.contains(stack.getItem()))
                .build();
        this.railOut = BaseItemStackHandler.builder(12)
                .validator(stack -> ItemTags.RAILS.contains(stack.getItem()))
                .build();
        this.torchIn = BaseItemStackHandler.builder(12)
                .validator(stack -> ModItemTags.RAIL_POWER_SOURCES.contains(stack.getItem()))
                .build();
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(MODE, PistonCartMode.IDLE);
    }

    @Override
    public void onSyncedDataUpdated(@Nonnull EntityDataAccessor<?> key) {
        super.onSyncedDataUpdated(key);
        if (MODE.equals(key)) {
            this.mode = this.entityData.get(MODE);
        }
    }

    @Nonnull
    @Override
    public BlockState getDefaultDisplayBlockState() {
        if (this.mode == PistonCartMode.PLACE) {
            return Blocks.PISTON.defaultBlockState().setValue(BlockStateProperties.FACING, Direction.UP);
        } else if (this.mode == PistonCartMode.REPLACE) {
            return Blocks.STICKY_PISTON.defaultBlockState().setValue(BlockStateProperties.FACING, Direction.UP);
        } else {
            return Blocks.PISTON.defaultBlockState().setValue(BlockStateProperties.FACING, Direction.UP).setValue(BlockStateProperties.EXTENDED, true);
        }
    }

    @Override
    public void destroy(@Nonnull DamageSource source) {
        super.destroy(source);
        this.spawnAtLocation(Items.PISTON);
        for (int i = 0; i < this.railIn.getSlots(); i++) {
            this.spawnAtLocation(this.railIn.getStackInSlot(i));
        }
        for (int i = 0; i < this.torchIn.getSlots(); i++) {
            this.spawnAtLocation(this.torchIn.getStackInSlot(i));
        }
        for (int i = 0; i < this.railOut.getSlots(); i++) {
            this.spawnAtLocation(this.railOut.getStackInSlot(i));
        }
    }

    @Nonnull
    @Override
    public InteractionResult interact(@Nonnull Player player, @Nonnull InteractionHand hand) {
        InteractionResult ret = super.interact(player, hand);
        if (ret.consumesAction()) return ret;
        if (player instanceof ServerPlayer) {
            MenuProvider containerProvider = new MenuProvider() {

                @Nonnull
                @Override
                public Component getDisplayName() {
                    return PistonCart.this.getDisplayName();
                }

                @Override
                public AbstractContainerMenu createMenu(int containerId, @Nonnull Inventory inventory, @Nonnull Player player) {
                    FriendlyByteBuf buffer = new FriendlyByteBuf(Unpooled.buffer());
                    buffer.writeInt(PistonCart.this.getId());
                    return PistonCartContainerMenu.TYPE.create(containerId, inventory, buffer);
                }
            };
            NetworkHooks.openGui((ServerPlayer) player, containerProvider, buffer -> buffer.writeInt(PistonCart.this.getId()));
        }
        return InteractionResult.sidedSuccess(player.level.isClientSide);
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.level.isClientSide) {
            if (this.mode == PistonCartMode.PLACE && this.shouldDoRailFunctions()) {
                BlockPos pos = new BlockPos(Mth.floor(this.getX()), Mth.floor(this.getY()), Mth.floor(this.getZ()));
                if (!this.level.getBlockState(pos).is(BlockTags.RAILS) && !this.level.getBlockState(pos.below()).is(BlockTags.RAILS)) {
                    // Simulate extraction of a rail and see what we should place next.
                    Pair<ItemStack, Integer> result = this.findRail(this.railIn);
                    ItemStack railStack = result.getLeft();
                    int railSlot = result.getRight();
                    if (!railStack.isEmpty() && railSlot >= 0) {
                        List<ItemStack> placeResult = this.placeRail(railStack, pos, false);
                        if (placeResult != null) {
                            this.railIn.extractItem(railSlot, 1, false);
                            for (ItemStack drop : placeResult) {
                                this.depositOrDrop(drop.copy());
                            }
                        }
                    }
                }
            } else if (this.mode == PistonCartMode.REPLACE && this.shouldDoRailFunctions()) {
                BlockPos pos = new BlockPos(Mth.floor(this.getX()), Mth.floor(this.getY()), Mth.floor(this.getZ()));
                if (!this.level.getBlockState(pos).is(BlockTags.RAILS) && this.level.getBlockState(pos.below()).is(BlockTags.RAILS)) {
                    pos = pos.below();
                }
                if (this.level.getBlockState(pos).is(BlockTags.RAILS)) {
                    // Simulate extraction of a rail and see what we should place next.
                    Pair<ItemStack, Integer> result = this.findRail(this.railIn);
                    ItemStack railStack = result.getLeft();
                    int railSlot = result.getRight();
                    if (!railStack.isEmpty() && railSlot >= 0) {
                        List<ItemStack> placeResult = this.placeRail(railStack, pos, true);
                        if (placeResult != null) {
                            this.railIn.extractItem(railSlot, 1, false);
                            for (ItemStack drop : placeResult) {
                                this.depositOrDrop(drop.copy());
                            }
                        }
                    }
                }
            }
        }
    }

    private Pair<ItemStack, Integer> findRail(IItemHandlerModifiable inventory) {
        for (int slot = inventory.getSlots() - 1; slot >= 0; slot--) {
            ItemStack extracted = inventory.extractItem(slot, 1, true);
            if (!extracted.isEmpty()) {
                return Pair.of(extracted.copy(), slot);
            }
        }
        return Pair.of(ItemStack.EMPTY, -1);
    }

    private void depositOrDrop(ItemStack rail) {
        ItemStack remainder = rail;
        for (int slot = 0; slot < this.railOut.getSlots(); slot++) {
            remainder = this.railOut.insertItem(slot, remainder, false);
            if (remainder.isEmpty()) {
                return;
            }
        }
        if (!remainder.isEmpty()) {
            ItemEntity ie = new ItemEntity(this.level, this.getX(), this.getY(), this.getZ(), remainder);
            this.level.addFreshEntity(ie);
        }
    }

    @Nullable
    private List<ItemStack> placeRail(ItemStack railStack, BlockPos pos, boolean replace) {
        BlockState oldState = this.level.getBlockState(pos);
        if (replace) {
            RailShape shape = switch (this.getMotionDirection()) {
                case WEST, EAST -> RailShape.EAST_WEST;
                default -> RailShape.NORTH_SOUTH;
            };
            if (oldState.getBlock() instanceof BaseRailBlock) {
                shape = ((BaseRailBlock) oldState.getBlock()).getRailDirection(oldState, this.level, pos, this);
            }
            List<ItemStack> drops = null;
            if (this.level instanceof ServerLevel) {
                drops = Block.getDrops(oldState, (ServerLevel) this.level, pos, this.level.getBlockEntity(pos));
            }
            if (this.doPlaceRail(railStack, shape, pos, oldState.getBlock())) {
                return drops == null ? ImmutableList.of() : drops;
            } else {
                return null;
            }
        } else {
            if (oldState.isAir() || oldState.getMaterial().isReplaceable()) {
                RailShape shape = switch (this.getMotionDirection()) {
                    case WEST, EAST -> RailShape.EAST_WEST;
                    default -> RailShape.NORTH_SOUTH;
                };
                return this.doPlaceRail(railStack, shape, pos, null) ? ImmutableList.of() : null;
            } else {
                return null;
            }
        }
    }

    // Place without checks
    private boolean doPlaceRail(ItemStack railStack, RailShape shape, BlockPos pos, @Nullable Block oldBlock) {
        if (railStack.getItem() instanceof BlockItem) {
            Block railBlock = ((BlockItem) railStack.getItem()).getBlock();
            if (railBlock instanceof BlockPistonControllerRail) {
                // Don't replace controller rails for this minecart.
                return false;
            }
            if (oldBlock != null && railBlock == oldBlock) {
                this.tryPower(pos);
                return false;
            }
            if (railBlock instanceof BaseRailBlock) {
                //noinspection deprecation
                if (((BaseRailBlock) railBlock).getShapeProperty().getPossibleValues().contains(shape)) {
                    BlockState railState = railBlock.getStateForPlacement(new DirectionalPlaceContext(this.level, pos, this.getMotionDirection(), railStack.copy(), Direction.UP));
                    if (railState == null) {
                        railState = railBlock.defaultBlockState();
                    }
                    //noinspection deprecation
                    railState = railState.setValue(((BaseRailBlock) railBlock).getShapeProperty(), shape);
                    if (!railState.canSurvive(this.level, pos)) {
                        return false;
                    }
                    this.level.setBlock(pos, railState, 11);
                    this.tryPower(pos);
                    return true;
                }
            }
        }
        return false;
    }

    private void tryPower(BlockPos railPos) {
        // We need to query it again as it might have changed its properties
        BlockState railState = this.level.getBlockState(railPos);
        if (railState.hasProperty(BlockStateProperties.POWERED) && !railState.getValue(BlockStateProperties.POWERED)) {
            // Only place torch if block above can redirect power to the rail
            if (this.level.getBlockState(railPos.below()).canOcclude()) {
                BlockPos pos = railPos.below(2);
                Pair<ItemStack, Integer> result = this.findRail(this.torchIn);
                ItemStack torchStack = result.getLeft();
                int torchSlot = result.getRight();
                if (!torchStack.isEmpty() && torchSlot >= 0 && torchStack.getItem() instanceof BlockItem) {
                    BlockState oldState = this.level.getBlockState(pos);
                    BlockState state = ((BlockItem) torchStack.getItem()).getBlock().defaultBlockState();
                    if (state.canSurvive(this.level, pos)) {
                        List<ItemStack> drops = null;
                        if (this.level instanceof ServerLevel) {
                            drops = Block.getDrops(oldState, (ServerLevel) this.level, pos, this.level.getBlockEntity(pos));
                        }
                        this.level.setBlock(pos, state, 11);
                        this.torchIn.extractItem(torchSlot, 1, false);
                        if (drops != null) {
                            for (ItemStack drop : drops) {
                                ItemEntity ie = new ItemEntity(this.level, this.getX(), this.getY(), this.getZ(), drop.copy());
                                this.level.addFreshEntity(ie);
                            }
                        }
                    }
                }
            }
        }
    }

    public IItemHandlerModifiable getRailInputInventory() {
        return this.railIn;
    }

    public IItemHandlerModifiable getRailOutputInventory() {
        return this.railOut;
    }

    public IItemHandlerModifiable getTorchInventory() {
        return this.torchIn;
    }

    public PistonCartMode getMode() {
        return this.mode;
    }

    public void setMode(PistonCartMode mode) {
        this.mode = mode;
        this.entityData.set(MODE, mode);
    }

    @Override
    protected void readAdditionalSaveData(@Nonnull CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        this.railIn.deserializeNBT(compound.getCompound("RailInput"));
        this.torchIn.deserializeNBT(compound.getCompound("TorchIn"));
        this.railOut.deserializeNBT(compound.getCompound("RailOut"));
        String modeName = compound.getString("Mode");
        try {
            this.mode = PistonCartMode.valueOf(modeName);
        } catch (IllegalArgumentException | NoSuchElementException e) {
            this.mode = PistonCartMode.IDLE;
        }
        if (this.mode != this.entityData.get(MODE)) {
            this.entityData.set(MODE, this.mode);
        }
    }

    @Override
    protected void addAdditionalSaveData(@Nonnull CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.put("RailInput", this.railIn.serializeNBT());
        compound.put("TorchIn", this.torchIn.serializeNBT());
        compound.put("RailOut", this.railOut.serializeNBT());
        compound.putString("Mode", this.mode.name());
    }
}
