package de.melanx.utilitix.block;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import io.github.noeppi_noeppi.libx.mod.ModX;
import io.github.noeppi_noeppi.libx.mod.registration.Registerable;
import net.minecraft.block.BlockState;
import net.minecraft.block.RedstoneTorchBlock;
import net.minecraft.block.RedstoneWallTorchBlock;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.WallOrFloorItem;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
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
        this(mod, properties, new net.minecraft.item.Item.Properties());
    }

    public WeakRedstoneTorch(ModX mod, Properties properties, net.minecraft.item.Item.Properties itemProperties) {
        super(properties);
        this.mod = mod;
        if (mod.tab != null) {
            itemProperties.group(mod.tab);
        }
        this.wallTorch = new Wall(properties);
        this.item = new WallOrFloorItem(this, this.wallTorch, itemProperties);
    }

    @Override
    public Set<Object> getAdditionalRegisters() {
        return ImmutableSet.of(this.item);
    }

    @Override
    public Map<String, Object> getNamedAdditionalRegisters() {
        return ImmutableMap.of("wall", this.wallTorch);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void registerClient(ResourceLocation id, Consumer<Runnable> defer) {
        RenderTypeLookup.setRenderLayer(this, RenderType.getCutout());
        RenderTypeLookup.setRenderLayer(this.wallTorch, RenderType.getCutout());
    }

    @Override
    public void animateTick(@Nonnull BlockState state, @Nonnull World world, @Nonnull BlockPos pos, @Nonnull Random rand) {
        // stop redstone particles
    }

    public class Wall extends RedstoneWallTorchBlock {

        public final WeakRedstoneTorch torch;
        
        public Wall(Properties properties) {
            super(properties);
            this.torch = WeakRedstoneTorch.this;
        }

        @Override
        public ItemStack getPickBlock(BlockState state, RayTraceResult target, IBlockReader world, BlockPos pos, PlayerEntity player) {
            return new ItemStack(WeakRedstoneTorch.this.item);
        }
        
        @Override
        public void animateTick(@Nonnull BlockState state, @Nonnull World world, @Nonnull BlockPos pos, @Nonnull Random rand) {
            // stop redstone particles
        }
    }
}
