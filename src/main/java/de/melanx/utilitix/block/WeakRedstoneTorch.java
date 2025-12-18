package de.melanx.utilitix.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.StandingAndWallBlockItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.RedstoneTorchBlock;
import net.minecraft.world.level.block.RedstoneWallTorchBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.HitResult;
import org.moddingx.libx.mod.ModX;
import org.moddingx.libx.registration.Registerable;
import org.moddingx.libx.registration.RegistrationContext;

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
        this.wallTorch = new Wall(properties);
        this.item = new StandingAndWallBlockItem(this, this.wallTorch, itemProperties, Direction.DOWN);
    }

    @Override
    public void registerAdditional(RegistrationContext ctx, EntryCollector builder) {
        builder.registerNamed(Registries.BLOCK, "wall", this.wallTorch);
        builder.register(Registries.ITEM, this.item);
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

        @Nonnull
        @Override
        public ItemStack getCloneItemStack(@Nonnull BlockState state, @Nonnull HitResult target, @Nonnull LevelReader level, @Nonnull BlockPos pos, @Nonnull Player player) {
            return new ItemStack(WeakRedstoneTorch.this.item);
        }

        @Override
        public void animateTick(@Nonnull BlockState state, @Nonnull Level level, @Nonnull BlockPos pos, @Nonnull RandomSource random) {
            // stop redstone particles
        }
    }
}
