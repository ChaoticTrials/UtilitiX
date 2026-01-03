package de.melanx.utilitix.content.experiencecrystal;

import de.melanx.utilitix.config.FeatureConfig;
import de.melanx.utilitix.util.XPUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.fluids.FluidUtil;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import org.moddingx.libx.base.tile.MenuBlockBE;
import org.moddingx.libx.block.DirectionShape;
import org.moddingx.libx.mod.ModX;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;

public class ExperienceCrystalBlock extends MenuBlockBE<ExperienceCrystalBlockEntity, ExperienceCrystalMenu> {

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

    public ExperienceCrystalBlock(ModX mod, Properties properties) {
        super(mod, ExperienceCrystalBlockEntity.class, ExperienceCrystalMenu.TYPE, properties);
    }

    @Override
    public void onPlace(@Nonnull BlockState state, @Nonnull Level level, @Nonnull BlockPos pos, @Nonnull BlockState oldState, boolean movedByPiston) {
        super.onPlace(state, level, pos, oldState, movedByPiston);

        if (level.getBlockEntity(pos) instanceof ExperienceCrystalBlockEntity crystal) {
            crystal.setDispatchable();
        }
    }

    @Override
    public void entityInside(@Nonnull BlockState state, @Nonnull Level level, @Nonnull BlockPos pos, @Nonnull Entity entity) {
        if (!(entity instanceof ExperienceOrb orb) || level.isClientSide) {
            return;
        }

        ExperienceCrystalBlockEntity blockEntity = this.getBlockEntity(level, pos);
        int xpValue = orb.value;
        int added = blockEntity.addXp(xpValue);

        if (added == xpValue) {
            entity.remove(Entity.RemovalReason.KILLED);
        } else {
            orb.value -= added;
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
    public VoxelShape getShape(@Nonnull BlockState state, @Nonnull BlockGetter level, @Nonnull BlockPos pos, @Nonnull CollisionContext context) {
        return SHAPE.getShape(state.getValue(BlockStateProperties.FACING));
    }

    @Nonnull
    @Override
    public VoxelShape getCollisionShape(@Nonnull BlockState state, @Nonnull BlockGetter level, @Nonnull BlockPos pos, @Nonnull CollisionContext context) {
        return COLLISION_SHAPE.getShape(state.getValue(BlockStateProperties.FACING));
    }

    private boolean useFluidItem(BlockEntity blockEntity, Player player, InteractionHand hand, Level level, BlockPos pos, BlockHitResult hitResult) {
        Optional<IFluidHandler> handler = FluidUtil.getFluidHandler(level, pos, hitResult.getDirection());

        // Shouldn't happen but if there's no tank pass
        if (handler.isEmpty()) {
            return false;
        }

        // If the user isn't holding an item or if the tank is empty, pass
        if (player.getItemInHand(hand).isEmpty() && !handler.get().getFluidInTank(0).isEmpty()) {
            return false;
        }

        // try to interact. If that fails, pass
        return FluidUtil.interactWithFluidHandler(player, hand, level, pos, hitResult.getDirection());
    }

    @Nonnull
    @Override
    protected ItemInteractionResult useItemOn(@Nonnull ItemStack stack, @Nonnull BlockState state, @Nonnull Level level, @Nonnull BlockPos pos, @Nonnull Player player, @Nonnull InteractionHand hand, @Nonnull BlockHitResult hitResult) {
        if (level.isClientSide || !this.useFluidItem(this.getBlockEntity(level, pos), player, hand, level, pos, hitResult)) {
            return super.useItemOn(stack, state, level, pos, player, hand, hitResult);
        }

        return ItemInteractionResult.SUCCESS;
    }

    @Override
    public void appendHoverText(@Nonnull ItemStack stack, @Nonnull Item.TooltipContext context, @Nonnull List<Component> tooltipComponents, @Nonnull TooltipFlag tooltipFlag) {
        if (!stack.has(DataComponents.BLOCK_ENTITY_DATA)) {
            return;
        }

        CustomData blockEntityData = stack.getOrDefault(DataComponents.BLOCK_ENTITY_DATA, CustomData.EMPTY);
        if (!blockEntityData.contains("Xp")) {
            return;
        }

        int xp = blockEntityData.copyTag().getInt("Xp");
        if (xp <= 0) {
            return;
        }

        int level = XPUtils.getLevelExp(xp).getLeft();
        if (level > 0) {
            tooltipComponents.add(Component.translatable("tooltip.utilitix.experience_crystal.xp_level", level).withStyle(ChatFormatting.GREEN));
        } else {
            tooltipComponents.add(Component.translatable("tooltip.utilitix.experience_crystal.xp_points", xp).withStyle(ChatFormatting.GREEN));
        }
    }

    @Override
    public boolean isEnabled(@Nonnull FeatureFlagSet enabledFeatures) {
        return FeatureConfig.Machines.experienceCrystal;
    }
}
