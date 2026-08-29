package de.melanx.utilitix.content.track.carts;

import de.melanx.utilitix.config.CommonConfig;
import de.melanx.utilitix.content.track.carts.stonecutter.StonecutterCartMode;
import de.melanx.utilitix.registration.ModSerializers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.NoSuchElementException;

public class StonecutterCart extends BaseCart {

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
    protected void defineSynchedData(@Nonnull SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(MODE, StonecutterCartMode.TOP);
        builder.define(IN_REVERSE, false);
    }

    @Override
    public void onSyncedDataUpdated(@Nonnull EntityDataAccessor<?> key) {
        super.onSyncedDataUpdated(key);
        if (MODE.equals(key)) {
            this.mode = this.entityData.get(MODE);
        } else if (IN_REVERSE.equals(key)) {
            this.setFlipped(this.entityData.get(IN_REVERSE));
        }
    }

    @Nonnull
    @Override
    public BlockState getDefaultDisplayBlockState() {
        return Blocks.STONECUTTER.defaultBlockState();
    }

    @Override
    public void onRemovedFromLevel() {
        super.onRemovedFromLevel();

        if (this.breakingBlock != null) {
            this.level().destroyBlockProgress(this.getId(), this.breakingBlock, -1);
        }
    }

    @Override
    public void tick() {
        super.tick();

        if (this.level().isClientSide()) {
            return;
        }

        BlockPos pos = new BlockPos(Mth.floor(this.getX()), Mth.floor(this.getY()), Mth.floor(this.getZ()));
        if (!this.level().getBlockState(pos).is(BlockTags.RAILS) && this.level().getBlockState(pos.below()).is(BlockTags.RAILS)) {
            pos = pos.below();
        }

        Direction minecartDir = this.getMotionDirection();
        Direction leftDir = minecartDir.getCounterClockWise();
        int ox = (minecartDir.getStepX() * this.getMode().offsetTrack) + (leftDir.getStepX() * this.getMode().offsetLeft);
        int oz = (minecartDir.getStepZ() * this.getMode().offsetTrack) + (leftDir.getStepZ() * this.getMode().offsetLeft);
        pos = pos.offset(ox, this.getMode().offsetHor, oz);

        if (!pos.equals(this.breakingBlock)) {
            if (this.breakingBlock != null) {
                this.level().destroyBlockProgress(this.getId(), this.breakingBlock, -1);
            }

            this.breakingBlock = pos;
            this.breakProgress = 0;
        }

        boolean shouldResetMotion = true;
        if (!this.breakingBlock.equals(this.lastSuccess) && this.cartHasMoved) {
            BlockState state = this.level().getBlockState(this.breakingBlock);
            if (state.isAir() || state.canBeReplaced() || state.is(BlockTags.RAILS)) {
                this.breakProgress = 0;
                this.finalTick(shouldResetMotion);
                return;
            }

            float hardness = state.getDestroySpeed(this.level(), this.breakingBlock);
            if (hardness < 0 || hardness > CommonConfig.Track.stonecutterMaxHardness) {
                this.breakProgress = 0;
                this.finalTick(shouldResetMotion);
                return;
            }

            this.breakProgress += (int) Mth.clamp(5 - hardness, 1, 5);
            if (this.breakProgress >= MAX_PROGRESS || hardness == 0) {
                List<ItemStack> drops = null;
                if (this.level() instanceof ServerLevel) {
                    drops = Block.getDrops(state, (ServerLevel) this.level(), this.breakingBlock, this.level().getBlockEntity(pos));
                }
                this.level().setBlock(this.breakingBlock, Blocks.AIR.defaultBlockState(), 11);
                if (drops != null) {
                    for (ItemStack drop : drops) {
                        ItemEntity ie = new ItemEntity(this.level(), this.getX(), this.getY(), this.getZ(), drop.copy());
                        ie.setDeltaMovement(Vec3.ZERO);
                        this.level().addFreshEntity(ie);
                    }
                }
                this.level().destroyBlockProgress(this.getId(), this.breakingBlock, -1);
                this.lastSuccess = this.breakingBlock;
                this.breakingBlock = null;
                this.breakProgress = 0;
            } else {
                int stage = Mth.clamp(Math.round((this.breakProgress / (float) MAX_PROGRESS) * 10), 0, 9);
                this.level().destroyBlockProgress(this.getId(), this.breakingBlock, stage);
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
        }

        this.finalTick(shouldResetMotion);
    }

    private void finalTick(boolean shouldResetMotion) {
        if (shouldResetMotion) {
            if (this.storedMotion != null) {
                this.setDeltaMovement(this.storedMotion);
                this.storedMotion = null;
            }
        }

        if (BaseCart.getHorizontalDistanceSqr(this.getDeltaMovement()) >= 0.1 * 0.1) {
            this.cartHasMoved = true;
        }

        if (this.isFlipped() != this.entityData.get(IN_REVERSE)) {
            this.entityData.set(IN_REVERSE, this.isFlipped());
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
    public InteractionResult interact(@Nonnull Player player, @Nonnull InteractionHand hand, @Nonnull Vec3 location) {
        InteractionResult ret = super.interact(player, hand, location);
        if (ret.consumesAction()) {
            return ret;
        }

        if (!this.level().isClientSide()) {
            int modeIdx = this.getMode().ordinal();
            StonecutterCartMode[] modes = StonecutterCartMode.values();
            this.setMode(modes[(modeIdx + 1) % modes.length]);
        }

        return player.level().isClientSide() ? InteractionResult.SUCCESS : InteractionResult.SUCCESS_SERVER;
    }

    @Override
    protected void readAdditionalSaveData(@Nonnull ValueInput input) {
        super.readAdditionalSaveData(input);

        String modeName = input.getStringOr("Mode", "");
        try {
            this.mode = StonecutterCartMode.valueOf(modeName);
        } catch (IllegalArgumentException | NoSuchElementException e) {
            this.mode = StonecutterCartMode.TOP;
        }

        if (this.mode != this.entityData.get(MODE)) {
            this.entityData.set(MODE, this.mode);
        }

        this.breakingBlock = input.read("BreakPos", BlockPos.CODEC).orElse(null);
        this.lastSuccess = input.read("LastSuccessfulBreak", BlockPos.CODEC).orElse(null);
        this.breakProgress = input.getIntOr("BreakProgress", 0);
        this.storedMotion = input.read("StoredMotion", Vec3.CODEC).orElse(null);
        this.cartHasMoved = input.getBooleanOr("CartHasMoved", false);
    }

    @Override
    protected void addAdditionalSaveData(@Nonnull ValueOutput output) {
        super.addAdditionalSaveData(output);

        output.putString("Mode", this.mode.name());

        if (this.breakingBlock == null) {
            output.discard("BreakPos");
        } else {
            output.store("BreakPos", BlockPos.CODEC, this.breakingBlock);
        }

        if (this.lastSuccess == null) {
            output.discard("LastSuccessfulBreak");
        } else {
            output.store("LastSuccessfulBreak", BlockPos.CODEC, this.lastSuccess);
        }

        output.putInt("BreakProgress", this.breakProgress);
        if (this.storedMotion == null) {
            output.discard("StoredMotion");
        } else {
            output.store("StoredMotion", Vec3.CODEC, this.storedMotion);
        }

        output.putBoolean("CartHasMoved", this.cartHasMoved);
    }
}
