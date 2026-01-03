package de.melanx.utilitix.content.experiencecrystal;

import de.melanx.utilitix.config.CommonConfig;
import de.melanx.utilitix.util.BoundingBoxUtils;
import net.minecraft.core.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import org.moddingx.libx.base.tile.BlockEntityBase;
import org.moddingx.libx.base.tile.TickingBlock;

import javax.annotation.Nonnull;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class ExperienceCrystalBlockEntity extends BlockEntityBase implements TickingBlock, IFluidHandler {

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
    protected void loadAdditional(@Nonnull CompoundTag tag, @Nonnull HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        this.xp = tag.getInt("Xp");
    }

    @Override
    protected void saveAdditional(@Nonnull CompoundTag tag, @Nonnull HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putInt("Xp", this.xp);
    }

    @Override
    public void handleUpdateTag(@Nonnull CompoundTag tag, @Nonnull HolderLookup.Provider lookupProvider) {
        super.handleUpdateTag(tag, lookupProvider);
        this.xp = tag.getInt("Xp");
    }

    @Nonnull
    @Override
    public CompoundTag getUpdateTag(@Nonnull HolderLookup.Provider registries) {
        CompoundTag tag = super.getUpdateTag(registries);
        tag.putInt("Xp", this.xp);
        return tag;
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

    public static IFluidHandler getCapability(ExperienceCrystalBlockEntity blockEntity, Direction side) {
        if (blockEntity.xpFluid().isPresent()) {
            return blockEntity;
        }

        return null;
    }

    @Override
    public int getTanks() {
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
    public FluidStack getFluidInTank(int tank) {
        int xpForTank = this.getXpForTank(tank);

        return xpForTank <= 0 ? FluidStack.EMPTY : this.xpFluid()
                .map(fluid -> new FluidStack(fluid, xpForTank))
                .orElse(FluidStack.EMPTY);
    }

    @Override
    public int getTankCapacity(int tank) {
        long totalMb = (long) CommonConfig.ExperienceCrystal.maxXp * MB_PER_XP;
        int maxCapacityPerTank = Integer.MAX_VALUE;

        if (tank < this.getTanks() - 1) {
            return maxCapacityPerTank;
        }

        return (int) (totalMb - ((long) maxCapacityPerTank * tank));
    }

    public boolean isFluidValid(@Nonnull FluidStack stack) {
        return stack.is(Tags.Fluids.EXPERIENCE);
    }

    @Override
    public boolean isFluidValid(int tank, @Nonnull FluidStack stack) {
        return this.isFluidValid(stack);
    }

    @Override
    public int fill(@Nonnull FluidStack resource, @Nonnull IFluidHandler.FluidAction action) {
        if (!this.isFluidValid(resource)) {
            return 0;
        }

        // we need to make sure we are only adding / subbing xp in increments of MB_PER_XP
        int xpToAdd = resource.getAmount() / MB_PER_XP;
        int totalXpAdded = 0;

        for (int tank = 0; tank < getTanks(); tank++) {
            int tankCapacity = getTankCapacity(tank);
            int xpInTank = getXpForTank(tank);
            int xpCanAdd = Math.min(xpToAdd, (tankCapacity / MB_PER_XP) - xpInTank);

            if (xpCanAdd > 0) {
                xpToAdd -= xpCanAdd;
                totalXpAdded += xpCanAdd;
                if (action.execute()) {
                    this.addXp(xpCanAdd);
                }
            }

            if (xpToAdd <= 0) {
                break;
            }
        }

        return totalXpAdded * MB_PER_XP;
    }

    @Nonnull
    @Override
    public FluidStack drain(int maxDrain, @Nonnull IFluidHandler.FluidAction action) {
        return this.drain(new FluidStack(this.getFluidInTank(0).getFluid(), maxDrain), action);
    }

    @Nonnull
    @Override
    public FluidStack drain(@Nonnull FluidStack resource, @Nonnull IFluidHandler.FluidAction action) {
        if (!this.isFluidValid(resource) || resource.getAmount() == 0 || this.getFluidInTank(0).isEmpty()) {
            return FluidStack.EMPTY;
        }

        int xpToDrain = resource.getAmount() / MB_PER_XP;
        int totalXpDrained = 0;

        for (int tank = getTanks() - 1; tank >= 0; tank--) {
            int xpInTank = getXpForTank(tank);
            int xpCanDrain = Math.min(xpToDrain, xpInTank);

            if (xpCanDrain > 0) {
                xpToDrain -= xpCanDrain;
                totalXpDrained += xpCanDrain;
                if (action.execute()) {
                    this.subtractXp(xpCanDrain);
                }
            }

            if (xpToDrain <= 0) {
                break;
            }
        }

        return new FluidStack(resource.getFluid(), totalXpDrained * MB_PER_XP);
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
            HolderSet.Named<Fluid> experienceFluids = this.level.registryAccess().registryOrThrow(Registries.FLUID).getOrCreateTag(Tags.Fluids.EXPERIENCE);
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
        Optional<ResourceLocation> configuredXp = CommonConfig.ExperienceCrystal.fluidXp;
        return configuredXp.isPresent()
                && fluidHolder.unwrapKey()
                .map(ResourceKey::location)
                .filter(configuredXp.get()::equals)
                .isPresent();
    }
}
