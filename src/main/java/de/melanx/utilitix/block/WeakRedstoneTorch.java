package de.melanx.utilitix.block;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import io.github.noeppi_noeppi.libx.mod.ModX;
import io.github.noeppi_noeppi.libx.mod.registration.Registerable;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
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
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.function.Consumer;

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
    public Set<Object> getAdditionalRegisters(ResourceLocation id) {
        return ImmutableSet.of(this.item);
    }

    @Override
    public Map<String, Object> getNamedAdditionalRegisters(ResourceLocation id) {
        return ImmutableMap.of("wall", this.wallTorch);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void registerClient(ResourceLocation id, Consumer<Runnable> defer) {
        ItemBlockRenderTypes.setRenderLayer(this, RenderType.cutout());
        ItemBlockRenderTypes.setRenderLayer(this.wallTorch, RenderType.cutout());
    }

    @Override
    public void animateTick(@Nonnull BlockState state, @Nonnull Level level, @Nonnull BlockPos pos, @Nonnull Random rand) {
        // stop redstone particles
    }

    public class Wall extends RedstoneWallTorchBlock {

        public final WeakRedstoneTorch torch;
        
        public Wall(Properties properties) {
            super(properties);
            this.torch = WeakRedstoneTorch.this;
        }

        @Override
        public ItemStack getPickBlock(BlockState state, HitResult target, BlockGetter level, BlockPos pos, Player player) {
            return new ItemStack(WeakRedstoneTorch.this.item);
        }

        @Override
        public void animateTick(@Nonnull BlockState state, @Nonnull Level level, @Nonnull BlockPos pos, @Nonnull Random random) {
            // stop redstone particles
        }
    }
}
