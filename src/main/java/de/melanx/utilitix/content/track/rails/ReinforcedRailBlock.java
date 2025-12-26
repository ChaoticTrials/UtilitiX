package de.melanx.utilitix.content.track.rails;

import com.mojang.serialization.MapCodec;
import de.melanx.utilitix.UtilitiX;
import de.melanx.utilitix.block.ModProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.BaseRailBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.state.properties.RailShape;
import org.moddingx.libx.mod.ModX;

import javax.annotation.Nonnull;

public class ReinforcedRailBlock extends RailBlock {

    public static final MapCodec<ReinforcedRailBlock> CODEC = Block.simpleCodec(ReinforcedRailBlock::new);

    public ReinforcedRailBlock(Properties properties) {
        this(UtilitiX.getInstance(), properties);
    }

    public ReinforcedRailBlock(ModX mod, Properties properties) {
        super(mod, true, properties);
    }

    public ReinforcedRailBlock(ModX mod, Properties properties, Item.Properties itemProperties) {
        super(mod, true, properties, itemProperties);
    }

    @Nonnull
    @Override
    public Property<RailShape> getShapeProperty() {
        return ModProperties.RAIL_SHAPE_FLAT;
    }

    @Nonnull
    @Override
    protected MapCodec<? extends BaseRailBlock> codec() {
        return CODEC;
    }
}
