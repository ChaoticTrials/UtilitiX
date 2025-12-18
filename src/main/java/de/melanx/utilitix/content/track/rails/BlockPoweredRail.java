package de.melanx.utilitix.content.track.rails;

import com.mojang.serialization.MapCodec;
import de.melanx.utilitix.UtilitiX;
import de.melanx.utilitix.content.track.TrackUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseRailBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.state.properties.RailShape;
import org.moddingx.libx.mod.ModX;

import javax.annotation.Nonnull;

public class BlockPoweredRail extends BlockPowerableRail {

    public final double maxRailSpeed;
    public static final MapCodec<BlockPoweredRail> CODEC = Block.simpleCodec(BlockPoweredRail::new);

    public BlockPoweredRail(Properties properties) {
        this(UtilitiX.getInstance(), 0.7D, properties);
    }

    public BlockPoweredRail(ModX mod, double maxRailSpeed, Properties properties) {
        this(mod, maxRailSpeed, properties, new Item.Properties());
    }

    public BlockPoweredRail(ModX mod, double maxRailSpeed, Properties properties, Item.Properties itemProperties) {
        super(mod, null, properties, itemProperties);
        this.maxRailSpeed = maxRailSpeed;
        this.registerDefaultState(this.defaultBlockState()
                .setValue(BlockStateProperties.POWERED, false)
                .setValue(BlockStateProperties.WATERLOGGED, false));
    }

    @Override
    public void onMinecartPass(BlockState state, @Nonnull Level level, @Nonnull BlockPos pos, @Nonnull AbstractMinecart cart) {
        if (state.getValue(BlockStateProperties.POWERED)) {
            TrackUtil.accelerateStraight(level, pos, this.getRailDirection(state, level, pos, cart), cart, this.maxRailSpeed);
        } else {
            TrackUtil.slowDownCart(level, cart, this.maxRailSpeed);
        }
    }

    @Override
    public float getRailMaxSpeed(@Nonnull BlockState state, @Nonnull Level level, @Nonnull BlockPos pos, @Nonnull AbstractMinecart cart) {
        return (float) this.maxRailSpeed;
    }

    @Nonnull
    @Override
    protected MapCodec<? extends BaseRailBlock> codec() {
        return CODEC;
    }

    @Nonnull
    @Override
    public Property<RailShape> getShapeProperty() {
        return BlockStateProperties.RAIL_SHAPE_STRAIGHT;
    }
}
