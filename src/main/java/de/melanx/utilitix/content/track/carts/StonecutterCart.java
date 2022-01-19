package de.melanx.utilitix.content.track.carts;

import de.melanx.utilitix.UtilitiXConfig;
import de.melanx.utilitix.content.track.carts.stonecutter.StonecutterCartMode;
import de.melanx.utilitix.registration.ModSerializers;
import io.github.noeppi_noeppi.libx.util.NBTX;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.NoSuchElementException;

public class StonecutterCart extends Cart {

    // This is the max progress. Progress is incremented each tick by clamp(5 - hardness, 1, 5)
    // Exception: Hardness 0 is insta break
    public static final int MAX_PROGRESS = 50;

    private static final EntityDataAccessor<StonecutterCartMode> MODE = SynchedEntityData.defineId(StonecutterCart.class, ModSerializers.stonecutterCartMode);
    private static final EntityDataAccessor<Boolean> IN_REVERSE = SynchedEntityData.defineId(StonecutterCart.class, EntityDataSerializers.BOOLEAN);

    private StonecutterCartMode mode = StonecutterCartMode.TOP;
    @Nullable
    private BlockPos breakingBlock = null;
    @Nullable
    private BlockPos lastSuccess = null;
    private int breakProgress = 0;
    @Nullable
    private Vec3 storedMotion = null;
    private boolean cartHasMoved = false;

    public StonecutterCart(EntityType<?> type, Level level) {
        super(type, level);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(MODE, StonecutterCartMode.TOP);
        this.entityData.define(IN_REVERSE, false);
    }

    @Override
    public void onSyncedDataUpdated(@Nonnull EntityDataAccessor<?> key) {
        super.onSyncedDataUpdated(key);
        if (MODE.equals(key)) {
            this.mode = this.entityData.get(MODE);
        } else if (IN_REVERSE.equals(key)) {
            this.flipped = this.entityData.get(IN_REVERSE);
        }
    }

    @Nonnull
    @Override
    public BlockState getDefaultDisplayBlockState() {
        return Blocks.STONECUTTER.defaultBlockState();
    }

    @Override
    public void destroy(@Nonnull DamageSource source) {
        super.destroy(source);
        this.spawnAtLocation(Items.STONECUTTER);
    }

    @Override
    public void onRemovedFromWorld() {
        super.onRemovedFromWorld();
        if (this.breakingBlock != null) {
            this.level.destroyBlockProgress(this.getId(), this.breakingBlock, -1);
        }
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.level.isClientSide) {
            BlockPos pos = new BlockPos(Mth.floor(this.getX()), Mth.floor(this.getY()), Mth.floor(this.getZ()));
            if (!this.level.getBlockState(pos).is(BlockTags.RAILS) && this.level.getBlockState(pos.below()).is(BlockTags.RAILS)) {
                pos = pos.below();
            }
            Direction minecartDir = this.getMotionDirection();
            Direction leftDir = minecartDir.getCounterClockWise();
            int ox = (minecartDir.getStepX() * this.getMode().offsetTrack) + (leftDir.getStepX() * this.getMode().offsetLeft);
            int oz = (minecartDir.getStepZ() * this.getMode().offsetTrack) + (leftDir.getStepZ() * this.getMode().offsetLeft);
            pos = pos.offset(ox, this.getMode().offsetHor, oz);

            if (!pos.equals(this.breakingBlock)) {
                if (this.breakingBlock != null) {
                    this.level.destroyBlockProgress(this.getId(), this.breakingBlock, -1);
                }
                this.breakingBlock = pos;
                this.breakProgress = 0;
            }

            boolean shouldResetMotion = true;
            if (!this.breakingBlock.equals(this.lastSuccess) && this.cartHasMoved) {
                BlockState state = this.level.getBlockState(this.breakingBlock);
                if (!state.isAir() && !state.getMaterial().isReplaceable() && !BlockTags.RAILS.contains(state.getBlock())) {
                    float hardness = state.getDestroySpeed(this.level, this.breakingBlock);
                    if (hardness >= 0 && hardness <= UtilitiXConfig.Track.stonecutterMaxHardness) {
                        this.breakProgress += Mth.clamp(5 - hardness, 1, 5);
                        if (this.breakProgress >= MAX_PROGRESS || hardness == 0) {
                            List<ItemStack> drops = null;
                            if (this.level instanceof ServerLevel) {
                                drops = Block.getDrops(state, (ServerLevel) this.level, this.breakingBlock, this.level.getBlockEntity(pos));
                            }
                            this.level.setBlock(this.breakingBlock, Blocks.AIR.defaultBlockState(), 11);
                            if (drops != null) {
                                for (ItemStack drop : drops) {
                                    ItemEntity ie = new ItemEntity(this.level, this.getX(), this.getY(), this.getZ(), drop.copy());
                                    ie.setDeltaMovement(Vec3.ZERO);
                                    this.level.addFreshEntity(ie);
                                }
                            }
                            this.level.destroyBlockProgress(this.getId(), this.breakingBlock, -1);
                            this.lastSuccess = this.breakingBlock;
                            this.breakingBlock = null;
                            this.breakProgress = 0;

                        } else {
                            int stage = Mth.clamp(Math.round((this.breakProgress / (float) MAX_PROGRESS) * 10), 0, 9);
                            this.level.destroyBlockProgress(this.getId(), this.breakingBlock, stage);
                            // capture motion if not yet done and remove current motion
                            // important we can only capture the motion once before resetting it
                            // as the calculations for the motion rely on the current motion which we
                            // set to ZERO
                            if (this.storedMotion == null) {
                                this.storedMotion = this.getDeltaMovement();
                            }
                            this.setDeltaMovement(Vec3.ZERO);
                            shouldResetMotion = false;
                        }
                    } else {
                        this.breakProgress = 0;
                    }
                } else {
                    this.breakProgress = 0;
                }
            }

            if (shouldResetMotion) {
                if (this.storedMotion != null) {
                    this.setDeltaMovement(this.storedMotion);
                    this.storedMotion = null;
                }
            }

            if (getHorizontalDistanceSqr(this.getDeltaMovement()) >= 0.1 * 0.1) {
                this.cartHasMoved = true;
            }
        }

        if (!this.level.isClientSide && this.flipped != this.entityData.get(IN_REVERSE)) {
            this.entityData.set(IN_REVERSE, this.flipped);
        }
    }

    public StonecutterCartMode getMode() {
        return this.mode;
    }

    public void setMode(StonecutterCartMode mode) {
        this.mode = mode;
        this.entityData.set(MODE, mode);
    }

    @Nonnull
    @Override
    public InteractionResult interact(@Nonnull Player player, @Nonnull InteractionHand hand) {
        InteractionResult ret = super.interact(player, hand);
        if (ret.consumesAction()) return ret;
        if (!this.level.isClientSide) {
            int modeIdx = this.getMode().ordinal();
            StonecutterCartMode[] modes = StonecutterCartMode.values();
            this.setMode(modes[(modeIdx + 1) % modes.length]);
        }
        return InteractionResult.sidedSuccess(player.level.isClientSide);
    }

    @Override
    protected void readAdditionalSaveData(@Nonnull CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        String modeName = compound.getString("Mode");
        try {
            this.mode = StonecutterCartMode.valueOf(modeName);
        } catch (IllegalArgumentException | NoSuchElementException e) {
            this.mode = StonecutterCartMode.TOP;
        }
        if (this.mode != this.entityData.get(MODE)) {
            this.entityData.set(MODE, this.mode);
        }
        // TODO NbtUtils.readBlockPos(CompoundTag)
        this.breakingBlock = NBTX.getPos(compound, "BreakPos");
        this.lastSuccess = NBTX.getPos(compound, "LastSuccessfulBreak");
        this.breakProgress = compound.getInt("BreakProgress");
        if (compound.contains("StoredMotion", Tag.TAG_COMPOUND)) {
            CompoundTag motionNbt = compound.getCompound("StoredMotion");
            this.storedMotion = new Vec3(motionNbt.getDouble("X"), motionNbt.getDouble("Y"), motionNbt.getDouble("Z"));
        } else {
            this.storedMotion = null;
        }
        this.cartHasMoved = compound.getBoolean("CartHasMoved");
    }

    @Override
    protected void addAdditionalSaveData(@Nonnull CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putString("Mode", this.mode.name());
        if (this.breakingBlock == null) {
            compound.remove("BreakPos");
        } else {
            NBTX.putPos(compound, "BreakPos", this.breakingBlock);
        }
        if (this.lastSuccess == null) {
            compound.remove("LastSuccessfulBreak");
        } else {
            NBTX.putPos(compound, "LastSuccessfulBreak", this.lastSuccess);
        }
        compound.putInt("BreakProgress", this.breakProgress);
        if (this.storedMotion == null) {
            compound.remove("StoredMotion");
        } else {
            CompoundTag motionNBT = new CompoundTag();
            motionNBT.putDouble("X", this.storedMotion.x);
            motionNBT.putDouble("Y", this.storedMotion.y);
            motionNBT.putDouble("Z", this.storedMotion.z);
            compound.put("StoredMotion", motionNBT);
        }
        compound.putBoolean("CartHasMoved", this.cartHasMoved);
    }
}
