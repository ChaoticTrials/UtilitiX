package de.melanx.utilitix.content.experiencecrystal;

import de.melanx.utilitix.UtilitiXConfig;
import de.melanx.utilitix.data.FluidTagProvider;
import de.melanx.utilitix.registration.ModFluids;
import io.github.noeppi_noeppi.libx.mod.registration.TileEntityBase;
import io.github.noeppi_noeppi.libx.util.BoundingBoxUtils;
import net.minecraft.block.BlockState;
import net.minecraft.entity.item.ExperienceOrbEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidTank;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class TileExperienceCrystal extends TileEntityBase implements ITickableTileEntity {

    private int xp;
    private final ExperienceFluidTank fluidInventory = new ExperienceFluidTank();
    private final LazyOptional<IFluidHandler> fluidHandler = LazyOptional.of(() -> this.fluidInventory);

    public TileExperienceCrystal(TileEntityType<?> tileEntityTypeIn) {
        super(tileEntityTypeIn);
    }

    @Override
    public void tick() {
        if (this.world != null && this.pos != null) {
            this.moveExps(this.world, this.pos);
        }
    }

    @Override
    public void read(@Nonnull BlockState state, @Nonnull CompoundNBT nbt) {
        super.read(state, nbt);
        this.xp = nbt.getInt("Xp");
    }

    @Nonnull
    @Override
    public CompoundNBT write(@Nonnull CompoundNBT nbt) {
        nbt.putInt("Xp", this.xp);
        return super.write(nbt);
    }

    @Override
    public void handleUpdateTag(BlockState state, CompoundNBT nbt) {
        super.handleUpdateTag(state, nbt);
        this.xp = nbt.getInt("Xp");
    }

    @Nonnull
    @Override
    public CompoundNBT getUpdateTag() {
        CompoundNBT nbt = super.getUpdateTag();
        nbt.putInt("Xp", this.xp);
        return nbt;
    }

    @Nonnull
    public ExperienceFluidTank getFluidInventory() {
        return this.fluidInventory;
    }

    public int getXp() {
        return this.xp;
    }

    public int addXp(int xp) {
        int add = Math.min(Math.max(0, xp), UtilitiXConfig.ExperienceCrystal.maxXp - this.xp);
        this.xp += add;
        this.markDirty();
        this.markDispatchable();

        return add;
    }

    public int subtractXp(int xp) {
        int remove = Math.max(0, Math.min(xp, this.xp));
        this.xp -= remove;
        this.markDirty();
        this.markDispatchable();

        return remove;
    }

    private void moveExps(World world, BlockPos pos) {
        if (!UtilitiXConfig.ExperienceCrystal.pullOrbs || this.xp >= UtilitiXConfig.ExperienceCrystal.maxXp) return;
        List<ExperienceOrbEntity> xps = world.getEntitiesWithinAABB(ExperienceOrbEntity.class, BoundingBoxUtils.expand(new Vector3d(pos.getX(), pos.getY(), pos.getZ()), 7));
        for (ExperienceOrbEntity orb : xps) {
            Vector3d vector = new Vector3d(pos.getX() - orb.getPosX() + 0.5, pos.getY() + (orb.getEyeHeight() / 2) - orb.getPosY(), pos.getZ() - orb.getPosZ() + 0.5);
            double scale = 1 - (vector.length() / 8);
            orb.setMotion(orb.getMotion().add(vector.normalize().scale(scale * scale * 0.1)));
        }
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        Direction bottom = this.getBlockState().get(BlockStateProperties.FACING).getOpposite();
        if (!this.removed && side == bottom && cap == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) {
            return this.fluidHandler.cast();
        }
        return super.getCapability(cap, side);
    }

    public class ExperienceFluidTank implements IFluidHandler, IFluidTank {

        @Override
        public int getTanks() {
            return 1;
        }

        @Nonnull
        @Override
        public FluidStack getFluidInTank(int tank) {
            return new FluidStack(ModFluids.liquidExperience, this.getMaxInt(TileExperienceCrystal.this.xp * 20L));
        }

        @Override
        public int getTankCapacity(int tank) {
            return Integer.MAX_VALUE;
        }

        @Override
        public boolean isFluidValid(int tank, @Nonnull FluidStack stack) {
            return FluidTagProvider.EXPERIENCE.contains(stack.getFluid());
        }

        @Override
        public int fill(FluidStack resource, FluidAction action) {
            if (resource.isEmpty() || !FluidTagProvider.EXPERIENCE.contains(resource.getFluid()) || resource.getAmount() < 20 || TileExperienceCrystal.this.xp >= Integer.MAX_VALUE) {
                return 0;
            }
            if (!action.simulate()) {
                TileExperienceCrystal.this.xp += resource.getAmount() / 20;
                this.onContentsChanged();
            }
            return (int) Math.min(Long.MAX_VALUE - TileExperienceCrystal.this.xp * 20L, resource.getAmount() - resource.getAmount() % 20);
        }

        @Nonnull
        @Override
        public FluidStack drain(FluidStack resource, FluidAction action) {
            if (resource.isEmpty() || resource.getFluid() != ModFluids.liquidExperience || resource.getAmount() < 20) {
                return FluidStack.EMPTY;
            }
            return this.drain(resource.getAmount(), action);
        }

        @Nonnull
        @Override
        public FluidStack drain(int maxDrain, FluidAction action) {
            long drained = maxDrain;
            if (TileExperienceCrystal.this.xp * 20L < drained) {
                drained = TileExperienceCrystal.this.xp * 20L;
            }
            FluidStack stack = new FluidStack(ModFluids.liquidExperience, (int) drained);
            if (action.execute() && drained > 0) {
                TileExperienceCrystal.this.xp -= drained / 20;
                this.onContentsChanged();
            }
            return stack;
        }

        @Nonnull
        @Override
        public FluidStack getFluid() {
            return new FluidStack(ModFluids.liquidExperience, (int) (long) TileExperienceCrystal.this.xp * 20);
        }

        @Override
        public int getFluidAmount() {
            return (int) (long) TileExperienceCrystal.this.xp * 20;
        }

        @Override
        public int getCapacity() {
            return Integer.MAX_VALUE;
        }

        @Override
        public boolean isFluidValid(FluidStack stack) {
            return FluidTagProvider.EXPERIENCE.contains(stack.getFluid());
        }

        private int getMaxInt(long value) {
            int ivalue;
            if (value > Integer.MAX_VALUE) {
                ivalue = Integer.MAX_VALUE;
            } else {
                ivalue = (int) value;
            }
            return ivalue;
        }

        protected void onContentsChanged() {
            TileExperienceCrystal.this.markDirty();
            TileExperienceCrystal.this.markDispatchable();
        }
    }
}
