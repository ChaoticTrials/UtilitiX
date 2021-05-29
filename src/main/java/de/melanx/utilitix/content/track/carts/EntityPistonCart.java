package de.melanx.utilitix.content.track.carts;

import com.google.common.collect.ImmutableList;
import de.melanx.utilitix.content.track.carts.piston.PistonCartContainer;
import de.melanx.utilitix.content.track.carts.piston.PistonCartMode;
import de.melanx.utilitix.content.track.rails.BlockPistonControllerRail;
import de.melanx.utilitix.registration.ModItemTags;
import de.melanx.utilitix.registration.ModSerializers;
import io.github.noeppi_noeppi.libx.inventory.BaseItemStackHandler;
import io.netty.buffer.Unpooled;
import net.minecraft.block.AbstractRailBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.state.properties.RailShape;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fml.network.NetworkHooks;
import net.minecraftforge.items.IItemHandlerModifiable;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.NoSuchElementException;

public class EntityPistonCart extends EntityCart {

    private static final DataParameter<PistonCartMode> MODE = EntityDataManager.createKey(EntityCart.class, ModSerializers.pistonCartMode);

    private PistonCartMode mode = PistonCartMode.IDLE;
    private final BaseItemStackHandler railIn;
    private final BaseItemStackHandler torchIn;
    private final BaseItemStackHandler railOut;

    public EntityPistonCart(EntityType<?> type, World worldIn) {
        super(type, worldIn);
        this.railIn = new BaseItemStackHandler(12);
        this.railIn.setSlotValidator((slot, stack) -> ItemTags.RAILS.contains(stack.getItem()));
        this.railOut = new BaseItemStackHandler(12);
        this.railOut.setSlotValidator((slot, stack) -> ItemTags.RAILS.contains(stack.getItem()));
        this.torchIn = new BaseItemStackHandler(1);
        this.torchIn.setSlotValidator((slot, stack) -> ModItemTags.RAIL_POWER_SOURCES.contains(stack.getItem()));
    }

    @Override
    protected void registerData() {
        super.registerData();
        this.dataManager.register(MODE, PistonCartMode.IDLE);
    }

    @Override
    public void notifyDataManagerChange(@Nonnull DataParameter<?> key) {
        super.notifyDataManagerChange(key);
        if (MODE.equals(key)) {
            this.mode = this.dataManager.get(MODE);
        }
    }

    @Nonnull
    @Override
    public BlockState getDefaultDisplayTile() {
        if (this.mode == PistonCartMode.PLACE) {
            return Blocks.PISTON.getDefaultState().with(BlockStateProperties.FACING, Direction.UP);
        } else if (this.mode == PistonCartMode.REPLACE) {
            return Blocks.STICKY_PISTON.getDefaultState().with(BlockStateProperties.FACING, Direction.UP);
        } else {
            return Blocks.PISTON.getDefaultState().with(BlockStateProperties.FACING, Direction.UP).with(BlockStateProperties.EXTENDED, true);
        }
    }

    @Override
    public void killMinecart(@Nonnull DamageSource source) {
        super.killMinecart(source);
        this.entityDropItem(Items.PISTON);
        for (int i = 0; i < this.railIn.getSlots(); i++) {
            this.entityDropItem(this.railIn.getStackInSlot(i));
        }
        for (int i = 0; i < this.torchIn.getSlots(); i++) {
            this.entityDropItem(this.torchIn.getStackInSlot(i));
        }
        for (int i = 0; i < this.railOut.getSlots(); i++) {
            this.entityDropItem(this.railOut.getStackInSlot(i));
        }
    }

    @Nonnull
    @Override
    public ActionResultType processInitialInteract(@Nonnull PlayerEntity player, @Nonnull Hand hand) {
        ActionResultType ret = super.processInitialInteract(player, hand);
        if (ret.isSuccessOrConsume()) return ret;
        if (player instanceof ServerPlayerEntity) {
            INamedContainerProvider containerProvider = new INamedContainerProvider() {

                @Override
                public ITextComponent getDisplayName() {
                    return EntityPistonCart.this.getDisplayName();
                }

                @Override
                public Container createMenu(int windowId, @Nonnull PlayerInventory playerInventory, @Nonnull PlayerEntity player) {
                    PacketBuffer buffer = new PacketBuffer(Unpooled.buffer());
                    buffer.writeInt(EntityPistonCart.this.getEntityId());
                    return PistonCartContainer.TYPE.create(windowId, playerInventory, buffer);
                }
            };
            NetworkHooks.openGui((ServerPlayerEntity) player, containerProvider, buffer -> buffer.writeInt(EntityPistonCart.this.getEntityId()));
        }
        return ActionResultType.successOrConsume(player.world.isRemote);
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.world.isRemote) {
            if (this.mode == PistonCartMode.PLACE && this.shouldDoRailFunctions()) {
                BlockPos pos = new BlockPos(MathHelper.floor(this.getPosX()), MathHelper.floor(this.getPosY()), MathHelper.floor(this.getPosZ()));
                if (!this.world.getBlockState(pos).isIn(BlockTags.RAILS) && !this.world.getBlockState(pos.down()).isIn(BlockTags.RAILS)) {
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
                BlockPos pos = new BlockPos(MathHelper.floor(this.getPosX()), MathHelper.floor(this.getPosY()), MathHelper.floor(this.getPosZ()));
                if (!this.world.getBlockState(pos).isIn(BlockTags.RAILS) && this.world.getBlockState(pos.down()).isIn(BlockTags.RAILS)) {
                    pos = pos.down();
                }
                if (this.world.getBlockState(pos).isIn(BlockTags.RAILS)) {
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
            ItemEntity ie = new ItemEntity(this.world, this.getPosX(), this.getPosY(), this.getPosZ(), remainder);
            this.world.addEntity(ie);
        }
    }

    @Nullable
    private List<ItemStack> placeRail(ItemStack railStack, BlockPos pos, boolean replace) {
        BlockState oldState = this.world.getBlockState(pos);
        if (replace) {
            RailShape shape = RailShape.NORTH_SOUTH;
            switch (Direction.fromAngle(this.rotationYaw + 90)) {
                case WEST:
                case EAST:
                    shape = RailShape.EAST_WEST;
            }
            if (oldState.getBlock() instanceof AbstractRailBlock) {
                shape = ((AbstractRailBlock) oldState.getBlock()).getRailDirection(oldState, this.world, pos, this);
            }
            List<ItemStack> drops = null;
            if (this.world instanceof ServerWorld) {
                drops = Block.getDrops(oldState, (ServerWorld) this.world, pos, this.world.getTileEntity(pos));
            }
            if (this.doPlaceRail(railStack, shape, pos, oldState.getBlock())) {
                return drops == null ? ImmutableList.of() : drops;
            } else {
                return null;
            }
        } else {
            if (oldState.isAir(this.world, pos) || oldState.getMaterial().isReplaceable()) {
                RailShape shape = RailShape.NORTH_SOUTH;
                switch (Direction.fromAngle(this.rotationYaw + 90)) {
                    case WEST:
                    case EAST:
                        shape = RailShape.EAST_WEST;
                }
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
            if (railBlock instanceof AbstractRailBlock) {
                //noinspection deprecation
                if (((AbstractRailBlock) railBlock).getShapeProperty().getAllowedValues().contains(shape)) {
                    //noinspection deprecation
                    BlockState railState = railBlock.getDefaultState().with(((AbstractRailBlock) railBlock).getShapeProperty(), shape);
                    this.world.setBlockState(pos, railState, 11);
                    this.tryPower(pos);
                    return true;
                }
            }
        }
        return false;
    }

    private void tryPower(BlockPos railPos) {
        // We need to query it again as it might have changed its properties
        BlockState railState = this.world.getBlockState(railPos);
        if (railState.hasProperty(BlockStateProperties.POWERED) && !railState.get(BlockStateProperties.POWERED)) {
            // Only place torch if block above can redirect power to the rail
            if (this.world.getBlockState(railPos.down()).isSolid()) {
                BlockPos pos = railPos.down(2);
                Pair<ItemStack, Integer> result = this.findRail(this.torchIn);
                ItemStack torchStack = result.getLeft();
                int torchSlot = result.getRight();
                if (!torchStack.isEmpty() && torchSlot >= 0 && torchStack.getItem() instanceof BlockItem) {
                    BlockState oldState = this.world.getBlockState(pos);
                    BlockState state = ((BlockItem) torchStack.getItem()).getBlock().getDefaultState();
                    if (state.isValidPosition(this.world, pos)) {
                        List<ItemStack> drops = null;
                        if (this.world instanceof ServerWorld) {
                            drops = Block.getDrops(oldState, (ServerWorld) this.world, pos, this.world.getTileEntity(pos));
                        }
                        this.world.setBlockState(pos, state, 11);
                        this.torchIn.extractItem(torchSlot, 1, false);
                        if (drops != null) {
                            for (ItemStack drop : drops) {
                                ItemEntity ie = new ItemEntity(this.world, this.getPosX(), this.getPosY(), this.getPosZ(), drop.copy());
                                this.world.addEntity(ie);
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
        this.dataManager.set(MODE, mode);
    }

    @Override
    protected void readAdditional(@Nonnull CompoundNBT nbt) {
        super.readAdditional(nbt);
        this.railIn.deserializeNBT(nbt.getCompound("RailInput"));
        this.torchIn.deserializeNBT(nbt.getCompound("TorchIn"));
        this.railOut.deserializeNBT(nbt.getCompound("RailOut"));
        String modeName = nbt.getString("Mode");
        try {
            this.mode = PistonCartMode.valueOf(modeName);
        } catch (IllegalArgumentException | NoSuchElementException e) {
            this.mode = PistonCartMode.IDLE;
        }
    }

    @Override
    protected void writeAdditional(@Nonnull CompoundNBT nbt) {
        super.writeAdditional(nbt);
        nbt.put("RailInput", this.railIn.serializeNBT());
        nbt.put("TorchIn", this.torchIn.serializeNBT());
        nbt.put("RailOut", this.railOut.serializeNBT());
        nbt.putString("Mode", this.mode.name());
    }
}
