package de.melanx.utilitix.content.track.rails;

import com.mojang.serialization.MapCodec;
import de.melanx.utilitix.UtilitiX;
import de.melanx.utilitix.content.track.TrackUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.InsideBlockEffectApplier;
import net.minecraft.world.entity.vehicle.minecart.AbstractMinecart;
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

public class PoweredRailBlock extends PowerableRailBlock implements MaxSpeedRail {

    public final double maxRailSpeed;
    public static final MapCodec<PoweredRailBlock> CODEC = Block.simpleCodec(PoweredRailBlock::new);

    public PoweredRailBlock(Properties properties) {
        this(UtilitiX.getInstance(), 0.7D, properties);
    }

    public PoweredRailBlock(ModX mod, double maxRailSpeed, Properties properties) {
        this(mod, maxRailSpeed, properties, new Item.Properties());
    }

    public PoweredRailBlock(ModX mod, double maxRailSpeed, Properties properties, Item.Properties itemProperties) {
        super(mod, null, properties, itemProperties);
        this.maxRailSpeed = maxRailSpeed;
        this.registerDefaultState(this.defaultBlockState()
                .setValue(BlockStateProperties.POWERED, false)
                .setValue(BlockStateProperties.WATERLOGGED, false));
    }

    @Override
    protected void entityInside(@Nonnull BlockState state, @Nonnull Level level, @Nonnull BlockPos pos, @Nonnull Entity entity, @Nonnull InsideBlockEffectApplier effectApplier, boolean isPrecise) {
        super.entityInside(state, level, pos, entity, effectApplier, isPrecise);
        if (!(entity instanceof AbstractMinecart cart)) {
            return;
        }

        if (state.getValue(BlockStateProperties.POWERED)) {
            TrackUtil.accelerateStraight(level, pos, this.getRailDirection(state, level, pos, cart), cart, this.maxRailSpeed);
        } else {
            TrackUtil.slowDownCart(level, cart, this.maxRailSpeed);
        }
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

    @Override
    public double getMaxRailSpeed() {
        return this.maxRailSpeed;
    }
}
