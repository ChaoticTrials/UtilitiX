package de.melanx.utilitix.content.track.rails;

import de.melanx.utilitix.content.track.TrackUtil;
import io.github.noeppi_noeppi.libx.mod.ModX;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

public abstract class BlockPoweredRail extends BlockPowerableRail {
    
    public final double maxRailSpeed;

    public BlockPoweredRail(ModX mod, double maxRailSpeed, Properties properties) {
        this(mod, maxRailSpeed, properties, new Item.Properties());
    }

    public BlockPoweredRail(ModX mod, double maxRailSpeed, Properties properties, Item.Properties itemProperties) {
        super(mod, null, properties, itemProperties);
        this.maxRailSpeed = maxRailSpeed;
        this.registerDefaultState(this.getStateDefinition().any().setValue(BlockStateProperties.POWERED, false));
    }

    @Override
    public void onMinecartPass(BlockState state, Level level, BlockPos pos, AbstractMinecart cart) {
        if (state.getValue(BlockStateProperties.POWERED)) {
            TrackUtil.accelerateStraight(level, pos, this.getRailDirection(state, level, pos, cart), cart, this.maxRailSpeed);
        } else {
            TrackUtil.slowDownCart(level, cart, this.maxRailSpeed);
        }
    }

    @Override
    public float getRailMaxSpeed(BlockState state, Level level, BlockPos pos, AbstractMinecart cart) {
        return (float) this.maxRailSpeed;
    }
}
