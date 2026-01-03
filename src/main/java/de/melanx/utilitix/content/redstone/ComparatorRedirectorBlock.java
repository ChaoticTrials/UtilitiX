package de.melanx.utilitix.content.redstone;

import de.melanx.utilitix.config.FeatureConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.moddingx.libx.base.BlockBase;
import org.moddingx.libx.mod.ModX;

import javax.annotation.Nonnull;

public class ComparatorRedirectorBlock extends BlockBase {

    public final Direction direction;

    public ComparatorRedirectorBlock(ModX mod, Direction direction, Properties properties) {
        super(mod, properties);
        this.direction = direction;
    }

    public ComparatorRedirectorBlock(ModX mod, Direction direction, Properties properties, Item.Properties itemProperties) {
        super(mod, properties, itemProperties);
        this.direction = direction;
    }

    @Override
    public boolean hasAnalogOutputSignal(@Nonnull BlockState state) {
        return true;
    }

    @Override
    public int getAnalogOutputSignal(@Nonnull BlockState blockState, @Nonnull Level level, @Nonnull BlockPos pos) {
        BlockState target = level.getBlockState(pos.relative(this.direction.getOpposite()));
        if (target.getBlock() instanceof ComparatorRedirectorBlock) {
            return 0;
        }

        return target.getAnalogOutputSignal(level, pos.relative(this.direction.getOpposite()));
    }

    @Override
    public boolean isEnabled(@Nonnull FeatureFlagSet enabledFeatures) {
        return FeatureConfig.Misc.Redstone.comparatorRedirector;
    }
}
