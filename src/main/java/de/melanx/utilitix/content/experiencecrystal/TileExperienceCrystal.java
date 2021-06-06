package de.melanx.utilitix.content.experiencecrystal;

import de.melanx.utilitix.UtilitiXConfig;
import io.github.noeppi_noeppi.libx.mod.registration.TileEntityBase;
import io.github.noeppi_noeppi.libx.util.BoundingBoxUtils;
import net.minecraft.block.BlockState;
import net.minecraft.entity.item.ExperienceOrbEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import java.util.List;

public class TileExperienceCrystal extends TileEntityBase implements ITickableTileEntity {

    private int xp;

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
}
