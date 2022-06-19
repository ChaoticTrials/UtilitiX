package de.melanx.utilitix.block;

import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.StandingAndWallBlockItem;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.RedstoneTorchBlock;
import net.minecraft.world.level.block.RedstoneWallTorchBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.registries.ForgeRegistries;
import org.moddingx.libx.mod.ModX;
import org.moddingx.libx.registration.Registerable;
import org.moddingx.libx.registration.RegistrationContext;
import org.moddingx.libx.registration.SetupContext;

import javax.annotation.Nonnull;

public class WeakRedstoneTorch extends RedstoneTorchBlock implements Registerable {

    protected final ModX mod;
    private final Item item;
    public final RedstoneWallTorchBlock wallTorch;

    public WeakRedstoneTorch(ModX mod, Properties properties) {
        this(mod, properties, new Item.Properties());
    }

    public WeakRedstoneTorch(ModX mod, Properties properties, Item.Properties itemProperties) {
        super(properties);
        this.mod = mod;
        if (mod.tab != null) {
            itemProperties.tab(mod.tab);
        }
        this.wallTorch = new Wall(properties);
        this.item = new StandingAndWallBlockItem(this, this.wallTorch, itemProperties);
    }

    @Override
    public void registerAdditional(RegistrationContext ctx, EntryCollector builder) {
        builder.registerNamed(Registry.BLOCK_REGISTRY, "wall", this.wallTorch);
        builder.register(Registry.ITEM_REGISTRY, this.item);
    }

    @Override
    public void initTracking(RegistrationContext ctx, TrackingCollector builder) throws ReflectiveOperationException {
        builder.trackNamed(ForgeRegistries.BLOCKS, "wall", WeakRedstoneTorch.class.getDeclaredField("wallTorch"));
        builder.track(ForgeRegistries.ITEMS, WeakRedstoneTorch.class.getDeclaredField("item"));
    }

    @Override
    public void registerClient(SetupContext ctx) {
        ItemBlockRenderTypes.setRenderLayer(this, RenderType.cutout());
        ItemBlockRenderTypes.setRenderLayer(this.wallTorch, RenderType.cutout());
    }

    @Override
    public void animateTick(@Nonnull BlockState state, @Nonnull Level level, @Nonnull BlockPos pos, @Nonnull RandomSource rand) {
        // stop redstone particles
    }

    public class Wall extends RedstoneWallTorchBlock {

        public final WeakRedstoneTorch torch;
        
        public Wall(Properties properties) {
            super(properties);
            this.torch = WeakRedstoneTorch.this;
        }

        @Override
        public ItemStack getCloneItemStack(BlockState state, HitResult target, BlockGetter level, BlockPos pos, Player player) {
            return new ItemStack(WeakRedstoneTorch.this.item);
        }

        @Override
        public void animateTick(@Nonnull BlockState state, @Nonnull Level level, @Nonnull BlockPos pos, @Nonnull RandomSource random) {
            // stop redstone particles
        }
    }
}
