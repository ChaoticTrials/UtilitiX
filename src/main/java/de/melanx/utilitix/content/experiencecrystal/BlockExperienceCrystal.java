package de.melanx.utilitix.content.experiencecrystal;

import de.melanx.utilitix.registration.ModBlocks;
import io.github.noeppi_noeppi.libx.mod.ModX;
import io.github.noeppi_noeppi.libx.mod.registration.BlockGUI;
import net.minecraft.block.BlockState;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.ExperienceOrbEntity;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import java.util.function.Consumer;

public class BlockExperienceCrystal extends BlockGUI<TileExperienceCrystal, ContainerExperienceCrystal> {
    private static final VoxelShape SHAPE = VoxelShapes.or(
            makeCuboidShape(1, 0, 1, 15, 1, 15),
            makeCuboidShape(2, 1, 2, 14, 2, 14),
            makeCuboidShape(3, 2, 3, 13, 6, 13),
            makeCuboidShape(6, 6, 6, 10, 14, 10),
            makeCuboidShape(9, 6.5, 5, 11, 11, 7),
            makeCuboidShape(8.5, 5.5, 4.5, 11.5, 6.5, 7.5),
            makeCuboidShape(8, 5.5, 8, 12, 9.5, 12),
            makeCuboidShape(4, 6, 4, 8, 12, 8),
            makeCuboidShape(5, 7.5, 9, 7, 13, 11),
            makeCuboidShape(4.5, 5.5, 8.5, 7.5, 7.5, 11.5)
    );

    public BlockExperienceCrystal(ModX mod, ContainerType<ContainerExperienceCrystal> containertype, Properties properties) {
        super(mod, TileExperienceCrystal.class, containertype, properties);
    }

    @Override
    public void registerClient(ResourceLocation id, Consumer<Runnable> defer) {
        ScreenManager.registerFactory(ModBlocks.experienceCrystal.container, ScreenExperienceCrystal::new);
        RenderTypeLookup.setRenderLayer(this, RenderType.getTranslucent());
    }

    @Override
    public void onReplaced(@Nonnull BlockState state, @Nonnull World world, @Nonnull BlockPos pos, @Nonnull BlockState newState, boolean isMoving) {
        if (!world.isRemote) {
            while (true) {
                TileExperienceCrystal tile = this.getTile(world, pos);
                int xp = Math.min(tile.getXp(), Short.MAX_VALUE);
                if (xp <= 0) {
                    break;
                }

                tile.subtractXp(xp);
                world.addEntity(new ExperienceOrbEntity(world, pos.getX() + world.rand.nextDouble(), pos.getY() + world.rand.nextDouble(), pos.getZ() + world.rand.nextDouble(), xp));
            }
        }

        super.onReplaced(state, world, pos, newState, isMoving);
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onEntityCollision(@Nonnull BlockState state, @Nonnull World world, @Nonnull BlockPos pos, @Nonnull Entity entity) {
        if (entity instanceof ExperienceOrbEntity) {
            TileExperienceCrystal tile = this.getTile(world, pos);
            if (!world.isRemote) {
                int xpValue = ((ExperienceOrbEntity) entity).xpValue;
                int added = tile.addXp(xpValue);
                if (added == xpValue) {
                    entity.remove();
                } else {
                    ((ExperienceOrbEntity) entity).xpValue -= added;
                }
            }
        }
    }

    @SuppressWarnings("deprecation")
    @Nonnull
    @Override
    public VoxelShape getShape(@Nonnull BlockState state, @Nonnull IBlockReader world, @Nonnull BlockPos pos, @Nonnull ISelectionContext context) {
        return SHAPE;
    }
}
