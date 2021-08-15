package de.melanx.utilitix.content.experiencecrystal;

import de.melanx.utilitix.registration.ModBlocks;
import io.github.noeppi_noeppi.libx.base.tile.BlockMenu;
import io.github.noeppi_noeppi.libx.block.DirectionShape;
import io.github.noeppi_noeppi.libx.mod.ModX;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.Consumer;

public class BlockExperienceCrystal extends BlockMenu<TileExperienceCrystal, ContainerMenuExperienceCrystal> {

    private static final VoxelShape BASE_SHAPE = Shapes.or(
            box(2, 1, 2, 14, 2, 14),
            box(3, 2, 3, 13, 6, 13),
            box(6, 6, 6, 10, 14, 10),
            box(9, 6.5, 5, 11, 11, 7),
            box(8.5, 5.5, 4.5, 11.5, 6.5, 7.5),
            box(8, 5.5, 8, 12, 9.5, 12),
            box(4, 6, 4, 8, 12, 8),
            box(5, 7.5, 9, 7, 13, 11),
            box(4.5, 5.5, 8.5, 7.5, 7.5, 11.5)
    );

    private static final DirectionShape SHAPE = new DirectionShape(Shapes.or(
            BASE_SHAPE,
            box(1, 0, 1, 15, 1, 15)
    ));

    private static final DirectionShape COLLISION_SHAPE = new DirectionShape(Shapes.or(
            BASE_SHAPE,
            box(1, 0.05, 1, 15, 1, 15)
    ));

    public BlockExperienceCrystal(ModX mod, MenuType<ContainerMenuExperienceCrystal> menutype, Properties properties) {
        super(mod, TileExperienceCrystal.class, menutype, properties);
    }

    @Override
    public void registerClient(ResourceLocation id, Consumer<Runnable> defer) {
        MenuScreens.register(ModBlocks.experienceCrystal.menu, ScreenExperienceCrystal::new);
        ItemBlockRenderTypes.setRenderLayer(this, RenderType.translucent());
    }

    @SuppressWarnings("deprecation")
    @Override
    public void entityInside(@Nonnull BlockState state, @Nonnull Level level, @Nonnull BlockPos pos, @Nonnull Entity entity) {
        if (entity instanceof ExperienceOrb) {
            TileExperienceCrystal tile = this.getBlockEntity(level, pos);
            if (!level.isClientSide) {
                int xpValue = ((ExperienceOrb) entity).value;
                int added = tile.addXp(xpValue);
                if (added == xpValue) {
                    entity.remove(Entity.RemovalReason.KILLED);
                } else {
                    ((ExperienceOrb) entity).value -= added;
                }
            }
        }
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(@Nonnull BlockPlaceContext context) {
        return this.defaultBlockState().setValue(BlockStateProperties.FACING, context.getClickedFace());
    }

    @Override
    protected void createBlockStateDefinition(@Nonnull StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(BlockStateProperties.FACING);
    }

    @Nonnull
    @Override
    @SuppressWarnings("deprecation")
    public VoxelShape getShape(@Nonnull BlockState state, @Nonnull BlockGetter level, @Nonnull BlockPos pos, @Nonnull CollisionContext context) {
        return SHAPE.getShape(state.getValue(BlockStateProperties.FACING));
    }

    @Nonnull
    @Override
    @SuppressWarnings("deprecation")
    public VoxelShape getCollisionShape(@Nonnull BlockState state, @Nonnull BlockGetter level, @Nonnull BlockPos pos, @Nonnull CollisionContext context) {
        return COLLISION_SHAPE.getShape(state.getValue(BlockStateProperties.FACING));
    }
}
