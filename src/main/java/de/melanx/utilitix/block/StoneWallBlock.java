package de.melanx.utilitix.block;

import io.github.noeppi_noeppi.libx.mod.registration.Registerable;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.WallBlock;

import java.util.Set;

public class StoneWallBlock extends WallBlock implements Registerable {

    private final BlockItem item;

    public StoneWallBlock(Properties properties, Item.Properties itemProperties) {
        super(properties);
        this.item = new BlockItem(this, itemProperties);
    }

    @Override
    public Set<Object> getAdditionalRegisters(ResourceLocation id) {
        return Set.of(this.item);
    }
}
