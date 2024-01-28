package de.melanx.utilitix.mixin;

import de.melanx.utilitix.UtilitiXConfig;
import de.melanx.utilitix.compat.quark.QuarkCompat;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoorHingeSide;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.fml.ModList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static net.minecraft.world.level.block.DoorBlock.isWoodenDoor;

@Mixin(DoorBlock.class)
public abstract class MixinDoorBlock {

    private static boolean HANDLE_DOOR = false;

    @Inject(
            method = "use",
            at = @At(value = "RETURN")
    )
    public void openSecondDoor(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit, CallbackInfoReturnable<InteractionResult> cir) {
        if (cir.getReturnValue() == InteractionResult.PASS || !UtilitiXConfig.doubleDoor || HANDLE_DOOR || (ModList.get().isLoaded("quark") && !QuarkCompat.useDoorOpening())) {
            return;
        }

        Direction facing = state.getValue(DoorBlock.FACING);
        DoorHingeSide hinge = state.getValue(DoorBlock.HINGE);
        DoubleBlockHalf half = state.getValue(DoorBlock.HALF);
        boolean open = !state.getValue(DoorBlock.OPEN);

        BlockPos neighborPos = pos.relative(hinge == DoorHingeSide.LEFT ? facing.getClockWise() : facing.getCounterClockWise());

        BlockState neighborState = level.getBlockState(neighborPos);
        if (!(neighborState.getBlock() instanceof DoorBlock) && !neighborState.is(BlockTags.DOORS) || neighborState.getValue(DoorBlock.OPEN) != open || !isWoodenDoor(neighborState)) {
            return;
        }

        if (neighborState.getValue(DoorBlock.HALF) == half && neighborState.getValue(DoorBlock.HINGE) != hinge && neighborState.getValue(DoorBlock.FACING) == facing) {
            BlockHitResult neighborHit = new BlockHitResult(new Vec3(neighborPos.getX() + 0.5, neighborPos.getY() + 0.5, neighborPos.getZ() + 0.5), facing, neighborPos, false);
            HANDLE_DOOR = true;
            if (neighborHit.getType() == HitResult.Type.BLOCK) {
                neighborState.use(level, player, hand, neighborHit);
            }
            HANDLE_DOOR = false;
//            ((DoorBlock) neighborState.getBlock()).setOpen(event.getEntity(), level, neighborState, neighborPos, !open);
        }
    }
}
