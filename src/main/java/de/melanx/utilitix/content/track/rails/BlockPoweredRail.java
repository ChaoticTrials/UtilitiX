package de.melanx.utilitix.content.track.rails;

import io.github.noeppi_noeppi.libx.mod.ModX;
import net.minecraft.block.BlockState;
import net.minecraft.entity.item.minecart.AbstractMinecartEntity;
import net.minecraft.item.Item;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public abstract class BlockPoweredRail extends BlockPowerableRail {
    
    public final double maxRailSpeed;

    public BlockPoweredRail(ModX mod, double maxRailSpeed, Properties properties) {
        this(mod, maxRailSpeed, properties, new Item.Properties());
    }

    public BlockPoweredRail(ModX mod, double maxRailSpeed, Properties properties, Item.Properties itemProperties) {
        super(mod, null, properties, itemProperties);
        this.maxRailSpeed = maxRailSpeed;
        this.setDefaultState(this.getStateContainer().getBaseState().with(BlockStateProperties.POWERED, false));
    }
    
    @Override
    public void onMinecartPass(BlockState state, World world, BlockPos pos, AbstractMinecartEntity cart) {
        if (state.get(BlockStateProperties.POWERED)) {
            RailUtil.accelerateStraight(world, pos, this.getRailDirection(state, world, pos, cart), cart, this.maxRailSpeed);
        } else {
            RailUtil.slowDownCart(world, cart, this.maxRailSpeed);
        }
    }

    @Override
    public float getRailMaxSpeed(BlockState state, World world, BlockPos pos, AbstractMinecartEntity cart) {
        return (float) this.maxRailSpeed;
    }
}
