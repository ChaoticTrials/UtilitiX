package de.melanx.utilitix.block;

import net.minecraft.core.Registry;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.WallBlock;
import net.minecraftforge.registries.ForgeRegistries;
import org.moddingx.libx.registration.Registerable;
import org.moddingx.libx.registration.RegistrationContext;

public class StoneWallBlock extends WallBlock implements Registerable {

    private final BlockItem item;

    public StoneWallBlock(Properties properties, Item.Properties itemProperties) {
        super(properties);
        this.item = new BlockItem(this, itemProperties);
    }

    @Override
    public void registerAdditional(RegistrationContext ctx, EntryCollector builder) {
        builder.register(Registry.ITEM_REGISTRY, this.item);
    }

    @Override
    public void initTracking(RegistrationContext ctx, TrackingCollector builder) throws ReflectiveOperationException {
        builder.track(ForgeRegistries.ITEMS, StoneWallBlock.class.getDeclaredField("item"));
    }
}
