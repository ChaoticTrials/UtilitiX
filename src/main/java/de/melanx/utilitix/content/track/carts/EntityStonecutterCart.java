package de.melanx.utilitix.content.track.carts;

import de.melanx.utilitix.UtilitiXConfig;
import de.melanx.utilitix.content.track.carts.stonecutter.StonecutterCartMode;
import de.melanx.utilitix.registration.ModSerializers;
import io.github.noeppi_noeppi.libx.util.NBTX;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.util.Constants;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.NoSuchElementException;

public class EntityStonecutterCart extends EntityCart {

    // This is the max progress. Progress is incremented each tick by clamp(5 - hardness, 1, 5)
    // Exception: Hardness 0 is insta break
    public static final int MAX_PROGRESS = 50;

    private static final DataParameter<StonecutterCartMode> MODE = EntityDataManager.createKey(EntityStonecutterCart.class, ModSerializers.stonecutterCartMode);
    private static final DataParameter<Boolean> IN_REVERSE = EntityDataManager.createKey(EntityStonecutterCart.class, DataSerializers.BOOLEAN);

    private StonecutterCartMode mode = StonecutterCartMode.TOP;
    @Nullable
    private BlockPos breakingBlock = null;
    @Nullable
    private BlockPos lastSuccess = null;
    private int breakProgress = 0;
    @Nullable
    private Vector3d storedMotion = null;
    private boolean cartHasMoved = false;

    public EntityStonecutterCart(EntityType<?> type, World worldIn) {
        super(type, worldIn);
    }

    @Override
    protected void registerData() {
        super.registerData();
        this.dataManager.register(MODE, StonecutterCartMode.TOP);
        this.dataManager.register(IN_REVERSE, false);
    }

    @Override
    public void notifyDataManagerChange(@Nonnull DataParameter<?> key) {
        super.notifyDataManagerChange(key);
        if (MODE.equals(key)) {
            this.mode = this.dataManager.get(MODE);
        } else if (IN_REVERSE.equals(key)) {
            this.isInReverse = this.dataManager.get(IN_REVERSE);
        }
    }

    @Nonnull
    @Override
    public BlockState getDefaultDisplayTile() {
        return Blocks.STONECUTTER.getDefaultState();
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.world.isRemote) {
            BlockPos pos = new BlockPos(MathHelper.floor(this.getPosX()), MathHelper.floor(this.getPosY()), MathHelper.floor(this.getPosZ()));
            if (!this.world.getBlockState(pos).isIn(BlockTags.RAILS) && this.world.getBlockState(pos.down()).isIn(BlockTags.RAILS)) {
                pos = pos.down();
            }
            Direction minecartDir = this.getAdjustedHorizontalFacing();
            Direction leftDir = minecartDir.rotateYCCW();
            int ox = (minecartDir.getXOffset() * this.getMode().offsetTrack) + (leftDir.getXOffset() * this.getMode().offsetLeft);
            int oz = (minecartDir.getZOffset() * this.getMode().offsetTrack) + (leftDir.getZOffset() * this.getMode().offsetLeft);
            pos = pos.add(ox, this.getMode().offsetHor, oz);

            if (!pos.equals(this.breakingBlock)) {
                if (this.breakingBlock != null) {
                    this.world.sendBlockBreakProgress(this.getEntityId(), this.breakingBlock, -1);
                }
                this.breakingBlock = pos;
                this.breakProgress = 0;
            }

            boolean shouldResetMotion = true;
            if (!this.breakingBlock.equals(this.lastSuccess) && this.cartHasMoved) {
                BlockState state = this.world.getBlockState(this.breakingBlock);
                if (!state.isAir(this.world, this.breakingBlock) && !state.getMaterial().isReplaceable() && !BlockTags.RAILS.contains(state.getBlock())) {
                    float hardness = state.getBlockHardness(this.world, this.breakingBlock);
                    if (hardness >= 0 && hardness <= UtilitiXConfig.Track.stonecutterMaxHardness) {
                        this.breakProgress += MathHelper.clamp(5 - hardness, 1, 5);
                        if (this.breakProgress >= MAX_PROGRESS || hardness == 0) {
                            List<ItemStack> drops = null;
                            if (this.world instanceof ServerWorld) {
                                drops = Block.getDrops(state, (ServerWorld) this.world, this.breakingBlock, this.world.getTileEntity(pos));
                            }
                            this.world.setBlockState(this.breakingBlock, Blocks.AIR.getDefaultState(), 11);
                            if (drops != null) {
                                for (ItemStack drop : drops) {
                                    ItemEntity ie = new ItemEntity(this.world, this.getPosX(), this.getPosY(), this.getPosZ(), drop.copy());
                                    ie.setMotion(Vector3d.ZERO);
                                    this.world.addEntity(ie);
                                }
                            }
                            this.world.sendBlockBreakProgress(this.getEntityId(), this.breakingBlock, -1);
                            this.lastSuccess = this.breakingBlock;
                            this.breakingBlock = null;
                            this.breakProgress = 0;

                        } else {
                            int stage = MathHelper.clamp(Math.round((this.breakProgress / (float) MAX_PROGRESS) * 10), 0, 9);
                            this.world.sendBlockBreakProgress(this.getEntityId(), this.breakingBlock, stage);
                            // capture motion if not yet done and remove current motion
                            // important we can only capture the motion once before resetting it
                            // as the calculations for the motion rely on the current motion which we
                            // set to ZERO
                            if (this.storedMotion == null) {
                                this.storedMotion = this.getMotion();
                            }
                            this.setMotion(Vector3d.ZERO);
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
                    this.setMotion(this.storedMotion);
                    this.storedMotion = null;
                }
            }
            
            if (horizontalMag(this.getMotion()) >= 0.1 * 0.1) {
                this.cartHasMoved = true;
            }
        }
        
        if (!this.world.isRemote && this.isInReverse != this.dataManager.get(IN_REVERSE)) {
            this.dataManager.set(IN_REVERSE, this.isInReverse);
        }
    }

    public StonecutterCartMode getMode() {
        return this.mode;
    }

    public void setMode(StonecutterCartMode mode) {
        this.mode = mode;
        this.dataManager.set(MODE, mode);
    }

    @Nonnull
    @Override
    public ActionResultType processInitialInteract(@Nonnull PlayerEntity player, @Nonnull Hand hand) {
        ActionResultType ret = super.processInitialInteract(player, hand);
        if (ret.isSuccessOrConsume()) return ret;
        if (!this.world.isRemote) {
            int modeIdx = this.getMode().ordinal();
            StonecutterCartMode[] modes = StonecutterCartMode.values();
            this.setMode(modes[(modeIdx + 1) % modes.length]);
        }
        return ActionResultType.successOrConsume(player.world.isRemote);
    }

    @Override
    protected void readAdditional(@Nonnull CompoundNBT nbt) {
        super.readAdditional(nbt);
        String modeName = nbt.getString("Mode");
        try {
            this.mode = StonecutterCartMode.valueOf(modeName);
        } catch (IllegalArgumentException | NoSuchElementException e) {
            this.mode = StonecutterCartMode.TOP;
        }
        if (this.mode != this.dataManager.get(MODE)) {
            this.dataManager.set(MODE, this.mode);
        }
        this.breakingBlock = NBTX.getPos(nbt, "BreakPos");
        this.lastSuccess = NBTX.getPos(nbt, "LastSuccessfulBreak");
        this.breakProgress = nbt.getInt("BreakProgress");
        if (nbt.contains("StoredMotion", Constants.NBT.TAG_COMPOUND)) {
            CompoundNBT motionNbt = nbt.getCompound("StoredMotion");
            this.storedMotion = new Vector3d(motionNbt.getDouble("X"), motionNbt.getDouble("Y"), motionNbt.getDouble("Z"));
        } else {
            this.storedMotion = null;
        }
        this.cartHasMoved = nbt.getBoolean("CartHasMoved");
    }

    @Override
    protected void writeAdditional(@Nonnull CompoundNBT nbt) {
        super.writeAdditional(nbt);
        nbt.putString("Mode", this.mode.name());
        if (this.breakingBlock == null) {
            nbt.remove("BreakPos");
        } else {
            NBTX.putPos(nbt, "BreakPos", this.breakingBlock);
        }
        if (this.lastSuccess == null) {
            nbt.remove("LastSuccessfulBreak");
        } else {
            NBTX.putPos(nbt, "LastSuccessfulBreak", this.lastSuccess);
        }
        nbt.putInt("BreakProgress", this.breakProgress);
        if (this.storedMotion == null) {
            nbt.remove("StoredMotion");
        } else {
            CompoundNBT motionNBT = new CompoundNBT();
            motionNBT.putDouble("X", this.storedMotion.x);
            motionNBT.putDouble("Y", this.storedMotion.y);
            motionNBT.putDouble("Z", this.storedMotion.z);
            nbt.put("StoredMotion", motionNBT);
        }
        nbt.putBoolean("CartHasMoved", this.cartHasMoved);
    }
}
