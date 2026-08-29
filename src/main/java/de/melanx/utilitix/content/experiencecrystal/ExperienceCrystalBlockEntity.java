package de.melanx.utilitix.content.experiencecrystal;

import de.melanx.utilitix.config.CommonConfig;
import de.melanx.utilitix.util.BoundingBoxUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.transaction.SnapshotJournal;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;
import org.moddingx.libx.base.tile.BlockEntityBase;
import org.moddingx.libx.base.tile.TickingBlock;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class ExperienceCrystalBlockEntity extends BlockEntityBase implements TickingBlock, ResourceHandler<FluidResource> {

    private final SnapshotJournal<Integer> xpJournal = new SnapshotJournal<>() {

        @Override
        protected Integer createSnapshot() {
            return ExperienceCrystalBlockEntity.this.xp;
        }

        @Override
        protected void revertToSnapshot(Integer snapshot) {
            ExperienceCrystalBlockEntity.this.xp = snapshot;
        }
    };

    public static int MB_PER_XP = 20;
    private int xp;
    private Integer tankCount;
    private Fluid cachedXpFluid = null;

    public ExperienceCrystalBlockEntity(BlockEntityType<?> blockEntityTypeIn, BlockPos pos, BlockState state) {
        super(blockEntityTypeIn, pos, state);
    }

    @Override
    public void tick() {
        if (this.level != null) {
            this.moveExps(this.level, this.worldPosition);
        }
    }

    @Override
    protected void loadAdditional(@Nonnull ValueInput input) {
        super.loadAdditional(input);
        this.xp = input.getIntOr("Xp", 0);
    }

    @Override
    protected void saveAdditional(@Nonnull ValueOutput output) {
        super.saveAdditional(output);
        output.putInt("Xp", this.xp);
    }

    public int getXp() {
        return this.xp;
    }

    public int addXp(int xp) {
        int add = Math.min(Math.max(0, xp), CommonConfig.ExperienceCrystal.maxXp - this.xp);
        this.xp += add;
        this.setChanged();
        this.setDispatchable();

        return add;
    }

    public int subtractXp(int xp) {
        int remove = Math.max(0, Math.min(xp, this.xp));
        this.xp -= remove;
        this.setChanged();
        this.setDispatchable();

        return remove;
    }

    private void moveExps(Level level, BlockPos pos) {
        if (!CommonConfig.ExperienceCrystal.pullOrbs || this.xp >= CommonConfig.ExperienceCrystal.maxXp) return;
        List<ExperienceOrb> xps = level.getEntitiesOfClass(ExperienceOrb.class, BoundingBoxUtils.expand(new Vec3(pos.getX(), pos.getY(), pos.getZ()), 7));
        for (ExperienceOrb orb : xps) {
            Vec3 vector = new Vec3(pos.getX() - orb.getX() + 0.5, pos.getY() + (orb.getEyeHeight() / 2) - orb.getY(), pos.getZ() - orb.getZ() + 0.5);
            double scale = 1 - (vector.length() / 8);
            orb.setDeltaMovement(orb.getDeltaMovement().add(vector.normalize().scale(scale * scale * 0.1)));
        }
    }

    @Nullable
    public static ResourceHandler<FluidResource> getCapability(ExperienceCrystalBlockEntity blockEntity, Direction side) {
        if (blockEntity.xpFluid().isPresent()) {
            return blockEntity;
        }

        return null;
    }

    @Override
    public int size() {
        if (this.tankCount == null) {
            long totalMb = (long) CommonConfig.ExperienceCrystal.maxXp * MB_PER_XP;
            this.tankCount = (int) (totalMb / Integer.MAX_VALUE);
            if (totalMb % Integer.MAX_VALUE != 0) {
                this.tankCount++;
            }
        }

        return this.tankCount;
    }

    @Nonnull
    @Override
    public FluidResource getResource(int tank) {
        int xpForTank = this.getXpForTank(tank);

        return xpForTank <= 0 ? FluidResource.EMPTY : this.xpFluid()
                .map(FluidResource::of)
                .orElse(FluidResource.EMPTY);
    }

    @Override
    public long getAmountAsLong(int tank) {
        return this.getXpForTank(tank);
    }

    @Override
    public long getCapacityAsLong(int tank, @Nonnull FluidResource resource) {
        return this.getTankCapacity(tank);
    }

    private int getTankCapacity(int tank) {
        long totalMb = (long) CommonConfig.ExperienceCrystal.maxXp * MB_PER_XP;
        int maxCapacityPerTank = Integer.MAX_VALUE;

        if (tank < this.size() - 1) {
            return maxCapacityPerTank;
        }

        return (int) (totalMb - ((long) maxCapacityPerTank * tank));
    }

    public boolean isFluidValid(@Nonnull FluidResource resource) {
        return !resource.isEmpty() && resource.toStack(1).is(holder -> holder.is(Tags.Fluids.EXPERIENCE));
    }

    @Override
    public boolean isValid(int tank, @Nonnull FluidResource resource) {
        return this.isFluidValid(resource);
    }

    @Override
    public int insert(int tank, @Nonnull FluidResource resource, int amount, @Nonnull TransactionContext tx) {
        if (!this.isFluidValid(resource)) {
            return 0;
        }

        // we need to make sure we are only adding / subbing xp in increments of MB_PER_XP
        int xpToAdd = amount / MB_PER_XP;
        if (xpToAdd <= 0) {
            return 0;
        }

        int tankCapacity = this.getTankCapacity(tank);
        int xpInTank = this.getXpForTank(tank);
        int xpCanAdd = Math.min(xpToAdd, (tankCapacity / MB_PER_XP) - xpInTank);

        if (xpCanAdd <= 0) {
            return 0;
        }

        this.xpJournal.updateSnapshots(tx);
        this.addXp(xpCanAdd);

        return xpCanAdd * MB_PER_XP;
    }

    @Override
    public int extract(int tank, @Nonnull FluidResource resource, int amount, @Nonnull TransactionContext tx) {
        if (!this.isFluidValid(resource)) {
            return 0;
        }

        int xpToDrain = amount / MB_PER_XP;
        if (xpToDrain <= 0) {
            return 0;
        }

        int xpInTank = this.getXpForTank(tank);
        int xpCanDrain = Math.min(xpToDrain, xpInTank);

        if (xpCanDrain <= 0) {
            return 0;
        }

        this.xpJournal.updateSnapshots(tx);
        this.subtractXp(xpCanDrain);

        return xpCanDrain * MB_PER_XP;
    }

    private int getXpForTank(int tank) {
        long fluidXp = (long) this.xp * MB_PER_XP;

        if (tank == 0) {
            return fluidXp < Integer.MAX_VALUE ? (int) fluidXp : this.getTankCapacity(tank);
        }

        long xpRemaining = fluidXp;
        for (int i = 0; i < tank; i++) {
            if (xpRemaining < Integer.MAX_VALUE) {
                return 0;
            }

            xpRemaining -= Integer.MAX_VALUE;
        }

        return (int) Math.min(xpRemaining, this.getTankCapacity(tank));
    }

    public Optional<Fluid> xpFluid() {
        if (this.cachedXpFluid == null) {
            //noinspection DataFlowIssue
            HolderSet.Named<Fluid> experienceFluids = this.level.registryAccess().lookupOrThrow(Registries.FLUID).getOrThrow(Tags.Fluids.EXPERIENCE);
            //noinspection OptionalGetWithoutIsPresent
            this.cachedXpFluid = experienceFluids.stream()
                    .filter(this::isConfiguredFluid)
                    .map(Holder::value)
                    .findFirst()
                    .or(() -> experienceFluids.stream()
                            .min(Comparator.comparing(l -> l.unwrapKey().get().toString()))
                            .map(Holder::value)
                    )
                    .orElse(null);
        }

        return Optional.ofNullable(this.cachedXpFluid);
    }

    private boolean isConfiguredFluid(Holder<Fluid> fluidHolder) {
        Optional<Identifier> configuredXp = CommonConfig.ExperienceCrystal.fluidXp;
        return configuredXp.isPresent()
                && fluidHolder.unwrapKey()
                .map(ResourceKey::identifier)
                .filter(configuredXp.get()::equals)
                .isPresent();
    }
}
