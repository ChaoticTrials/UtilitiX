package de.melanx.utilitix.content.track.carts;

import com.google.common.collect.ImmutableList;
import de.melanx.utilitix.content.track.carts.piston.PistonCartMenu;
import de.melanx.utilitix.content.track.carts.piston.PistonCartMode;
import de.melanx.utilitix.content.track.rails.PistonControllerRailBlock;
import de.melanx.utilitix.registration.ModItemTags;
import de.melanx.utilitix.registration.ModSerializers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.DirectionalPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseRailBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.RailShape;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import org.apache.commons.lang3.tuple.Pair;
import org.moddingx.libx.inventory.BaseItemStackHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.IntStream;

public class PistonCart extends BaseCart {

    private static final EntityDataAccessor<PistonCartMode> MODE = SynchedEntityData.defineId(PistonCart.class, ModSerializers.pistonCartMode);

    private PistonCartMode mode = PistonCartMode.IDLE;
    private final BaseItemStackHandler railIn;
    private final BaseItemStackHandler torchIn;
    private final BaseItemStackHandler railOut;

    public PistonCart(EntityType<?> type, Level level) {
        super(type, level);
        this.railIn = BaseItemStackHandler.builder(12)
                .validator(stack -> stack.is(ItemTags.RAILS), IntStream.range(0, 12).toArray())
                .build();
        this.railOut = BaseItemStackHandler.builder(12)
                .validator(stack -> stack.is(ItemTags.RAILS), IntStream.range(0, 12).toArray())
                .build();
        this.torchIn = BaseItemStackHandler.builder(1)
                .validator(stack -> stack.is(ModItemTags.RAIL_POWER_SOURCES), 0)
                .build();
    }

    @Override
    protected void defineSynchedData(@Nonnull SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(MODE, PistonCartMode.IDLE);
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
        }

        if (this.mode == PistonCartMode.REPLACE) {
            return Blocks.STICKY_PISTON.defaultBlockState().setValue(BlockStateProperties.FACING, Direction.UP);
        }

        return Blocks.PISTON.defaultBlockState().setValue(BlockStateProperties.FACING, Direction.UP).setValue(BlockStateProperties.EXTENDED, true);
    }

    @Override
    public void destroy(@Nonnull DamageSource source) {
        super.destroy(source);

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
        if (ret.consumesAction()) {
            return ret;
        }

        if (!this.level().isClientSide && player instanceof ServerPlayer serverPlayer) {
            PistonCartMenu.TYPE.open(serverPlayer, this.getDisplayName(), this.getId());
        }

        return InteractionResult.sidedSuccess(this.level().isClientSide);
    }

    @Override
    public void tick() {
        super.tick();
        if (this.level().isClientSide) {
            return;
        }

        if (this.mode == PistonCartMode.PLACE && this.shouldDoRailFunctions()) {
            BlockPos pos = new BlockPos(Mth.floor(this.getX()), Mth.floor(this.getY()), Mth.floor(this.getZ()));
            if (!this.level().getBlockState(pos).is(BlockTags.RAILS) && !this.level().getBlockState(pos.below()).is(BlockTags.RAILS)) {
                // Simulate extraction of a rail and see what we should place next.
                Pair<ItemStack, Integer> result = this.findRail(this.railIn);
                ItemStack railStack = result.getLeft();
                int railSlot = result.getRight();

                if (railStack.isEmpty() || railSlot < 0) {
                    return;
                }

                List<ItemStack> placeResult = this.placeRail(railStack, pos, false);
                if (placeResult != null) {
                    this.railIn.extractItem(railSlot, 1, false);
                    for (ItemStack drop : placeResult) {
                        this.depositOrDrop(drop.copy());
                    }
                }
            }

            return;
        }

        if (this.mode == PistonCartMode.REPLACE && this.shouldDoRailFunctions()) {
            BlockPos pos = new BlockPos(Mth.floor(this.getX()), Mth.floor(this.getY()), Mth.floor(this.getZ()));
            if (!this.level().getBlockState(pos).is(BlockTags.RAILS) && this.level().getBlockState(pos.below()).is(BlockTags.RAILS)) {
                pos = pos.below();
            }

            if (!this.level().getBlockState(pos).is(BlockTags.RAILS)) {
                return;
            }

            // Simulate extraction of a rail and see what we should place next.
            Pair<ItemStack, Integer> result = this.findRail(this.railIn);
            ItemStack railStack = result.getLeft();
            int railSlot = result.getRight();

            if (railStack.isEmpty() || railSlot < 0) {
                return;
            }

            List<ItemStack> placeResult = this.placeRail(railStack, pos, true);
            if (placeResult != null) {
                this.railIn.extractItem(railSlot, 1, false);
                for (ItemStack drop : placeResult) {
                    this.depositOrDrop(drop.copy());
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
            ItemEntity ie = new ItemEntity(this.level(), this.getX(), this.getY(), this.getZ(), remainder);
            this.level().addFreshEntity(ie);
        }
    }

    @Nullable
    private List<ItemStack> placeRail(ItemStack railStack, BlockPos pos, boolean replace) {
        BlockState oldState = this.level().getBlockState(pos);
        if (replace) {
            RailShape shape = switch(this.getMotionDirection()) {
                case WEST, EAST -> RailShape.EAST_WEST;
                default -> RailShape.NORTH_SOUTH;
            };

            if (oldState.getBlock() instanceof BaseRailBlock) {
                shape = ((BaseRailBlock) oldState.getBlock()).getRailDirection(oldState, this.level(), pos, this);
            }

            List<ItemStack> drops = null;
            if (this.level() instanceof ServerLevel) {
                drops = Block.getDrops(oldState, (ServerLevel) this.level(), pos, this.level().getBlockEntity(pos));
            }

            if (this.doPlaceRail(railStack, shape, pos, oldState.getBlock())) {
                return drops == null ? ImmutableList.of() : drops;
            }

            return null;
        }

        if (oldState.isAir() || oldState.canBeReplaced()) {
            RailShape shape = switch(this.getMotionDirection()) {
                case WEST, EAST -> RailShape.EAST_WEST;
                default -> RailShape.NORTH_SOUTH;
            };

            return this.doPlaceRail(railStack, shape, pos, null) ? ImmutableList.of() : null;
        }

        return null;
    }

    // Place without checks
    private boolean doPlaceRail(ItemStack railStack, RailShape shape, BlockPos pos, @Nullable Block oldBlock) {
        if (!(railStack.getItem() instanceof BlockItem blockItem)) {
            return false;
        }

        Block railBlock = blockItem.getBlock();
        if (railBlock instanceof PistonControllerRailBlock) {
            // Don't replace controller rails for this minecart.
            return false;
        }

        if (oldBlock != null && railBlock == oldBlock) {
            this.tryPower(pos);
            return false;
        }

        if (!(railBlock instanceof BaseRailBlock baseRailBlock)) {
            return false;
        }

        //noinspection deprecation
        if (!baseRailBlock.getShapeProperty().getPossibleValues().contains(shape)) {
            return false;
        }

        BlockState railState = railBlock.getStateForPlacement(new DirectionalPlaceContext(this.level(), pos, this.getMotionDirection(), railStack.copy(), Direction.UP));
        //noinspection deprecation
        railState = railState.setValue(baseRailBlock.getShapeProperty(), shape);

        if (!railState.canSurvive(this.level(), pos)) {
            return false;
        }

        this.level().setBlock(pos, railState, Block.UPDATE_ALL_IMMEDIATE);
        this.tryPower(pos);

        return true;
    }

    private void tryPower(BlockPos railPos) {
        // We need to query it again as it might have changed its properties
        BlockState railState = this.level().getBlockState(railPos);
        if (!railState.hasProperty(BlockStateProperties.POWERED) || railState.getValue(BlockStateProperties.POWERED)) {
            return;
        }

        // Only place torch if the block above can redirect power to the rail
        if (!this.level().getBlockState(railPos.below()).canOcclude()) {
            return;
        }

        BlockPos pos = railPos.below(2);
        Pair<ItemStack, Integer> result = this.findRail(this.torchIn);
        ItemStack torchStack = result.getLeft();
        int torchSlot = result.getRight();

        if (torchStack.isEmpty() || torchSlot < 0 || !(torchStack.getItem() instanceof BlockItem blockItem)) {
            return;
        }

        BlockState oldState = this.level().getBlockState(pos);
        BlockState state = blockItem.getBlock().defaultBlockState();
        if (!state.canSurvive(this.level(), pos)) {
            return;
        }

        List<ItemStack> drops = null;
        if (this.level() instanceof ServerLevel) {
            drops = Block.getDrops(oldState, (ServerLevel) this.level(), pos, this.level().getBlockEntity(pos));
        }

        this.level().setBlock(pos, state, Block.UPDATE_ALL_IMMEDIATE);
        this.torchIn.extractItem(torchSlot, 1, false);

        if (drops == null) {
            return;
        }

        for (ItemStack drop : drops) {
            ItemEntity ie = new ItemEntity(this.level(), this.getX(), this.getY(), this.getZ(), drop.copy());
            this.level().addFreshEntity(ie);
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

        RegistryAccess registryAccess = this.registryAccess();
        this.railIn.deserializeNBT(registryAccess, compound.getCompound("RailInput"));
        this.torchIn.deserializeNBT(registryAccess, compound.getCompound("TorchIn"));
        this.railOut.deserializeNBT(registryAccess, compound.getCompound("RailOut"));

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

        RegistryAccess registryAccess = this.registryAccess();
        compound.put("RailInput", this.railIn.serializeNBT(registryAccess));
        compound.put("TorchIn", this.torchIn.serializeNBT(registryAccess));
        compound.put("RailOut", this.railOut.serializeNBT(registryAccess));
        compound.putString("Mode", this.mode.name());
    }
}
