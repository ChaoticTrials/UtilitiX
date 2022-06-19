package de.melanx.utilitix.content.experiencecrystal;

import de.melanx.utilitix.UtilitiXConfig;
import de.melanx.utilitix.util.BoundingBoxUtils;
import de.melanx.utilitix.util.XPUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidTank;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.tags.ITag;
import org.jetbrains.annotations.Nullable;
import org.moddingx.libx.base.tile.BlockEntityBase;
import org.moddingx.libx.base.tile.TickingBlock;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class TileExperienceCrystal extends BlockEntityBase implements TickingBlock, IFluidTank, IFluidHandler {

    public static int MB_PER_XP = 20;
    private int xp;

    public TileExperienceCrystal(BlockEntityType<?> blockEntityTypeIn, BlockPos pos, BlockState state) {
        super(blockEntityTypeIn, pos, state);
    }

    @Override
    public void tick() {
        if (this.level != null) {
            this.moveExps(this.level, this.worldPosition);
        }
    }

    @Override
    public void load(@Nonnull CompoundTag nbt) {
        super.load(nbt);
        this.xp = nbt.getInt("Xp");
    }

    @Override
    public void saveAdditional(@Nonnull CompoundTag compound) {
        compound.putInt("Xp", this.xp);
    }

    @Override
    public void handleUpdateTag(CompoundTag nbt) {
        super.handleUpdateTag(nbt);
        this.xp = nbt.getInt("Xp");
    }

    @Nonnull
    @Override
    public CompoundTag getUpdateTag() {
        CompoundTag nbt = super.getUpdateTag();
        nbt.putInt("Xp", this.xp);
        return nbt;
    }

    public int getXp() {
        return this.xp;
    }

    public int addXp(int xp) {
        int add = Math.min(Math.max(0, xp), UtilitiXConfig.ExperienceCrystal.maxXp - this.xp);
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
        if (!UtilitiXConfig.ExperienceCrystal.pullOrbs || this.xp >= UtilitiXConfig.ExperienceCrystal.maxXp) return;
        List<ExperienceOrb> xps = level.getEntitiesOfClass(ExperienceOrb.class, BoundingBoxUtils.expand(new Vec3(pos.getX(), pos.getY(), pos.getZ()), 7));
        for (ExperienceOrb orb : xps) {
            Vec3 vector = new Vec3(pos.getX() - orb.getX() + 0.5, pos.getY() + (orb.getEyeHeight() / 2) - orb.getY(), pos.getZ() - orb.getZ() + 0.5);
            double scale = 1 - (vector.length() / 8);
            orb.setDeltaMovement(orb.getDeltaMovement().add(vector.normalize().scale(scale * scale * 0.1)));
        }
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> capability, @Nullable Direction side) {
        if (validXpFluidIsPresent() && capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) {
            return LazyOptional.of(() -> this).cast();
        }

        return super.getCapability(capability, side);
    }

    @Nonnull
    @Override
    public FluidStack getFluid() {
        return xpFluid()
                .map(fluid -> new FluidStack(fluid, this.xp * MB_PER_XP))
                .orElse(FluidStack.EMPTY);
    }

    @Override
    public int getFluidAmount() {
        if (this.xp > Integer.MAX_VALUE / MB_PER_XP) {
            return Integer.MAX_VALUE;
        }

        return this.xp * MB_PER_XP;
    }

    @Override
    public int getCapacity() {
        if (UtilitiXConfig.ExperienceCrystal.maxXp > Integer.MAX_VALUE / MB_PER_XP) {
            return Integer.MAX_VALUE;
        }

        return UtilitiXConfig.ExperienceCrystal.maxXp * MB_PER_XP;
    }

    @Override
    public boolean isFluidValid(FluidStack stack) {
        return XPUtils.XP_FLUID_TAGS
                .stream()
                .anyMatch(p -> getFluidTag(p).contains(stack.getFluid()));
    }

    @Override
    public int getTanks() {
        return 1;
    }

    @Nonnull
    @Override
    public FluidStack getFluidInTank(int tank) {
        return this.getFluid();
    }

    @Override
    public int getTankCapacity(int tank) {
        return this.getCapacity();
    }

    @Override
    public boolean isFluidValid(int tank, @Nonnull FluidStack stack) {
        return this.isFluidValid(stack);
    }

    @Override
    public int fill(FluidStack resource, IFluidHandler.FluidAction action) {
        if (!this.isFluidValid(resource)) {
            return 0;
        }

        // we need to make sure we are only adding / subbing xp in increments of MB_PER_XP
        int xpAccepted = Math.min(this.getCapacity() - this.getFluidAmount(), resource.getAmount()) / MB_PER_XP;
        if (action.execute()) {
            xpAccepted = this.addXp(xpAccepted);
        }

        return xpAccepted * MB_PER_XP;
    }

    @Nonnull
    @Override
    public FluidStack drain(int maxDrain, IFluidHandler.FluidAction action) {
        return this.drain(new FluidStack(this.getFluid(), maxDrain), action);
    }

    @Nonnull
    @Override
    public FluidStack drain(FluidStack resource, IFluidHandler.FluidAction action) {
        if (!this.isFluidValid(resource) || resource.getAmount() == 0 || this.getFluidAmount() == 0) {
            return FluidStack.EMPTY;
        }

        // we need to make sure we are only adding / subbing xp in increments of MB_PER_XP
        int xpToDrain = Math.min(this.getFluidAmount(), resource.getAmount()) / MB_PER_XP;
        FluidStack result = new FluidStack(resource.getFluid(), xpToDrain * MB_PER_XP);
        if (action.execute()) {
            this.subtractXp(xpToDrain);
        }

        return result;
    }

    @Nonnull
    private static ITag<Fluid> getFluidTag(TagKey<Fluid> tag) {
        return Objects.requireNonNull(ForgeRegistries.FLUIDS.tags())
                .getTag(tag);
    }

    // returns true if any of the xp fluid tags
    public static boolean validXpFluidIsPresent() {
        return XPUtils.XP_FLUID_TAGS
                .stream()
                .anyMatch(tag -> !getFluidTag(tag).isEmpty());
    }

    public static Optional<Fluid> xpFluid() {
        return XPUtils.XP_FLUID_TAGS
                .stream()
                .flatMap(tag -> getFluidTag(tag).stream())
                .findFirst();
    }
}
