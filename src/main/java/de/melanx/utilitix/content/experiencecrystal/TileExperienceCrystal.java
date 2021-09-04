package de.melanx.utilitix.content.experiencecrystal;

import de.melanx.utilitix.UtilitiXConfig;
import de.melanx.utilitix.util.BoundingBoxUtils;
import io.github.noeppi_noeppi.libx.base.tile.BlockEntityBase;
import io.github.noeppi_noeppi.libx.base.tile.TickableBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nonnull;
import java.util.List;

public class TileExperienceCrystal extends BlockEntityBase implements TickableBlock {

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

    @Nonnull
    @Override
    public CompoundTag save(@Nonnull CompoundTag compound) {
        compound.putInt("Xp", this.xp);
        return super.save(compound);
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
}
