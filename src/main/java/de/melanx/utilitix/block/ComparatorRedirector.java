package de.melanx.utilitix.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.moddingx.libx.base.BlockBase;
import org.moddingx.libx.mod.ModX;

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
    public boolean hasAnalogOutputSignal(@Nonnull BlockState state) {
        return true;
    }

    @Override
    @SuppressWarnings("deprecation")
    public int getAnalogOutputSignal(@Nonnull BlockState blockState, @Nonnull Level level, @Nonnull BlockPos pos) {
        BlockState target = level.getBlockState(pos.relative(this.direction.getOpposite()));
        if (target.getBlock() instanceof ComparatorRedirector) {
            return 0;
        } else {
            return target.getAnalogOutputSignal(level, pos.relative(this.direction.getOpposite()));
        }
    }
}
