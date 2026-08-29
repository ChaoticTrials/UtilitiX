package de.melanx.utilitix.content.redstone;

import de.melanx.utilitix.config.FeatureConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.StandingAndWallBlockItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.RedstoneTorchBlock;
import net.minecraft.world.level.block.RedstoneWallTorchBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.moddingx.libx.mod.ModX;
import org.moddingx.libx.registration.Registerable;
import org.moddingx.libx.registration.RegistrationContext;

import javax.annotation.Nonnull;

public class WeakRedstoneTorchBlock extends RedstoneTorchBlock implements Registerable {

    protected final ModX mod;
    private Item.Properties pendingItemProperties;
    private Item item;
    public final RedstoneWallTorchBlock wallTorch;

    public WeakRedstoneTorchBlock(ModX mod, Properties properties) {
        this(mod, properties, new Item.Properties());
    }

    public WeakRedstoneTorchBlock(ModX mod, Properties properties, Item.Properties itemProperties) {
        super(properties);
        this.mod = mod;
        this.wallTorch = new WallTorchBlock(properties);
        this.pendingItemProperties = itemProperties;
    }

    @Override
    public void registerAdditional(RegistrationContext ctx, EntryCollector builder) {
        builder.registerNamed(Registries.BLOCK, "wall", this.wallTorch);

        this.item = new StandingAndWallBlockItem(this, this.wallTorch, Direction.DOWN, this.pendingItemProperties.setId(ResourceKey.create(Registries.ITEM, ctx.id())).useBlockDescriptionPrefix()) {

            @Override
            public boolean isEnabled(@Nonnull FeatureFlagSet enabledFeatures) {
                return WeakRedstoneTorchBlock.this.isEnabled(enabledFeatures);
            }
        };
        this.pendingItemProperties = null;
        builder.register(Registries.ITEM, this.item);
    }

    @Override
    public void animateTick(@Nonnull BlockState state, @Nonnull Level level, @Nonnull BlockPos pos, @Nonnull RandomSource rand) {
        // stop redstone particles
    }

    @Override
    public boolean isEnabled(@Nonnull FeatureFlagSet enabledFeatures) {
        return FeatureConfig.Misc.Redstone.weakRedstoneTorch;
    }

    public class WallTorchBlock extends RedstoneWallTorchBlock {

        public final WeakRedstoneTorchBlock torch;

        public WallTorchBlock(Properties properties) {
            super(properties);
            this.torch = WeakRedstoneTorchBlock.this;
        }

        @Nonnull
        @Override
        public ItemStack getCloneItemStack(@Nonnull LevelReader level, @Nonnull BlockPos pos, @Nonnull BlockState state, boolean includeData, @Nonnull Player player) {
            return new ItemStack(WeakRedstoneTorchBlock.this.item);
        }

        @Override
        public void animateTick(@Nonnull BlockState state, @Nonnull Level level, @Nonnull BlockPos pos, @Nonnull RandomSource random) {
            // stop redstone particles
        }

        @Override
        public boolean isEnabled(@Nonnull FeatureFlagSet enabledFeatures) {
            return this.torch.isEnabled(enabledFeatures);
        }
    }
}
