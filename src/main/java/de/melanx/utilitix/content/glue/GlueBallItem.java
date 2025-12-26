package de.melanx.utilitix.content.glue;

import de.melanx.utilitix.compat.zeta.ZetaCompat;
import de.melanx.utilitix.config.FeatureConfig;
import de.melanx.utilitix.registration.ModAttachmentTypes;
import de.melanx.utilitix.registration.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.chunk.LevelChunk;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.level.BlockEvent;
import org.moddingx.libx.base.ItemBase;
import org.moddingx.libx.mod.ModX;

import javax.annotation.Nonnull;
import java.util.List;

public class GlueBallItem extends ItemBase {

    public GlueBallItem(ModX mod, Properties properties) {
        super(mod, properties);
        NeoForge.EVENT_BUS.addListener(GlueBallItem::neighbourChange);
    }

    @Nonnull
    @Override
    public InteractionResult useOn(@Nonnull UseOnContext context) {
        Level level = context.getLevel();
        BlockPos clickedPos = context.getClickedPos();
        LevelChunk chunk = level.getChunkAt(clickedPos);
        StickyChunk glue = chunk.getExistingDataOrNull(ModAttachmentTypes.stickyChunk);

        if (glue == null) {
            return super.useOn(context);
        }

        int x = clickedPos.getX() & 0xF;
        int y = clickedPos.getY();
        int z = clickedPos.getZ() & 0xF;

        Direction face = context.getPlayer() != null && context.getPlayer().isShiftKeyDown() ? context.getClickedFace().getOpposite() : context.getClickedFace();
        if (glue.get(x, y, z, face) || !GlueBallItem.canGlue(level, clickedPos, face)) {
            return InteractionResult.sidedSuccess(level.isClientSide);
        }

        if (level instanceof ServerLevel serverLevel) {
            glue.attach(chunk);
            glue.set(x, y, z, face, true);
            chunk.setUnsaved(true);

            if (context.getPlayer() == null || !context.getPlayer().getAbilities().instabuild) {
                context.getItemInHand().shrink(1);
            }

            level.playSound(null, clickedPos, SoundEvents.PARROT_IMITATE_SLIME, SoundSource.BLOCKS);
            serverLevel.sendParticles(ParticleTypes.ITEM_SLIME,
                    clickedPos.getX() + 0.5 + (0.55 * face.getStepX()),
                    clickedPos.getY() + 0.5 + (0.55 * face.getStepY()),
                    clickedPos.getZ() + 0.5 + (0.55 * face.getStepZ()),
                    10, 0, 0, 0, 0.1
            );
        }

        return super.useOn(context);
    }

    @Override
    public void appendHoverText(@Nonnull ItemStack stack, @Nonnull TooltipContext context, @Nonnull List<Component> tooltipComponents, @Nonnull TooltipFlag tooltipFlag) {
        if (ModList.get().isLoaded("zeta")) {
            Component warning = ZetaCompat.warningForGlue();
            if (warning != null) {
                tooltipComponents.add(warning);
            }
        }
    }

    @Override
    public boolean isEnabled(@Nonnull FeatureFlagSet enabledFeatures) {
        return FeatureConfig.Misc.InWorldChanges.glue;
    }

    public static void neighbourChange(BlockEvent.NeighborNotifyEvent event) {
        if (event.getLevel().isClientSide() || !(event.getLevel() instanceof Level level)) {
            return;
        }

        for (Direction dir : Direction.values()) {
            BlockPos thePos = event.getPos().relative(dir);
            BlockState state = level.getBlockState(thePos);
            if (state.getBlock() == Blocks.MOVING_PISTON && (state.getValue(BlockStateProperties.FACING) == dir || state.getValue(BlockStateProperties.FACING) == dir.getOpposite())) {
                // Block has been changed because of a piston move.
                // Glue logic is handled in the piston til
                // Skip this here
                return;
            } else if (state.getBlock() == Blocks.PISTON_HEAD && state.getValue(BlockStateProperties.SHORT) && (state.getValue(BlockStateProperties.FACING) == dir || state.getValue(BlockStateProperties.FACING) == dir.getOpposite())) {
                // Block has been changed because of a piston move.
                // Glue logic is handled in the piston til
                // Skip this here
                // This is sometimes buggy, but we can't really do anything about this.
                return;
            }
        }

        LevelChunk chunk = level.getChunkAt(event.getPos());
        StickyChunk glue = chunk.getExistingDataOrNull(ModAttachmentTypes.stickyChunk);
        if (glue == null) {
            return;
        }

        int x = event.getPos().getX() & 0xF;
        int y = event.getPos().getY();
        int z = event.getPos().getZ() & 0xF;
        for (Direction dir : Direction.values()) {
            if (glue.get(x, y, z, dir) && !GlueBallItem.canGlue(level, event.getPos(), dir)) {
                glue.set(x, y, z, dir, false);
                chunk.setUnsaved(true);
                BlockPos targetPos = event.getPos().relative(dir);
                ItemEntity ie = new ItemEntity(level, targetPos.getX() + 0.5, targetPos.getY() + 0.5, targetPos.getZ() + 0.5, new ItemStack(ModItems.glueBall));
                ie.setPickUpDelay(20);
                level.addFreshEntity(ie);
            }
        }
    }

    public static boolean canGlue(Level level, BlockPos pos, Direction side) {
        BlockState state = level.getBlockState(pos);

        return FeatureConfig.Misc.InWorldChanges.glue && state.isFaceSturdy(level, pos, side) && !state.isStickyBlock() && state.getDestroySpeed(level, pos) >= 0;
    }
}
