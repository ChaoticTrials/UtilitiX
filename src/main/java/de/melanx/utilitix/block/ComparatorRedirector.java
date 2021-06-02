package de.melanx.utilitix.block;

import io.github.noeppi_noeppi.libx.mod.ModX;
import io.github.noeppi_noeppi.libx.mod.registration.BlockBase;
import net.minecraft.block.BlockState;
import net.minecraft.item.Item;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

public class ComparatorRedirector extends BlockBase {

    public final Direction direction;
    
    public ComparatorRedirector(ModX mod, Direction direction, Properties properties) {
        super(mod, properties);
        this.direction = direction;
    }

    public ComparatorRedirector(ModX mod, Direction direction, Properties properties, Item.Properties itemProperties) {
        super(mod, properties, itemProperties);
        this.direction = direction;
    }
    
    @Override
    @SuppressWarnings("deprecation")
    public boolean hasComparatorInputOverride(@Nonnull BlockState state) {
        return true;
    }

    @Override
    @SuppressWarnings("deprecation")
    public int getComparatorInputOverride(@Nonnull BlockState state, @Nonnull World world, @Nonnull BlockPos pos) {
        BlockState target = world.getBlockState(pos.offset(this.direction.getOpposite()));
        if (target.getBlock() instanceof ComparatorRedirector) {
            return 0;
        } else {
            return target.getComparatorInputOverride(world, pos.offset(this.direction.getOpposite()));
        }
    }
}
