package de.melanx.utilitix.content.track.rails;

import com.mojang.serialization.MapCodec;
import de.melanx.utilitix.UtilitiX;
import de.melanx.utilitix.content.track.MinecartTinkererItem;
import de.melanx.utilitix.content.track.carts.PistonCart;
import de.melanx.utilitix.content.track.carts.piston.PistonCartMode;
import de.melanx.utilitix.registration.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.InsideBlockEffectApplier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseRailBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.state.properties.RailShape;
import net.minecraft.world.phys.BlockHitResult;
import org.moddingx.libx.mod.ModX;

import javax.annotation.Nonnull;

public class PistonControllerRailBlock extends ControllerRailBlock<PistonControllerRailBlockEntity> {

    public static final MapCodec<PistonControllerRailBlock> CODEC = Block.simpleCodec(PistonControllerRailBlock::new);

    public PistonControllerRailBlock(Properties properties) {
        this(UtilitiX.getInstance(), false, properties);
    }

    public PistonControllerRailBlock(ModX mod, boolean reinforced, Properties properties) {
        super(mod, PistonControllerRailBlockEntity::new, reinforced, properties);
    }

    public PistonControllerRailBlock(ModX mod, boolean reinforced, Properties properties, Item.Properties itemProperties) {
        super(mod, PistonControllerRailBlockEntity::new, reinforced, properties, itemProperties);
    }

    @Nonnull
    @Override
    protected InteractionResult useItemOn(@Nonnull ItemStack stack, @Nonnull BlockState state, @Nonnull Level level, @Nonnull BlockPos pos, @Nonnull Player player, @Nonnull InteractionHand hand, @Nonnull BlockHitResult hitResult) {
        InteractionResult result = super.useItemOn(stack, state, level, pos, player, hand, hitResult);
        if (result.consumesAction()) {
            return result;
        }

        ItemStack held = player.getItemInHand(hand);
        if (held.isEmpty() || held.getItem() != ModItems.minecartTinkerer) {
            return InteractionResult.TRY_WITH_EMPTY_HAND;
        }

        if (!level.isClientSide()) {
            PistonControllerRailBlockEntity blockEntity = this.getBlockEntity(level, pos);
            int modeIdx = blockEntity.getMode().ordinal();
            PistonCartMode[] modes = PistonCartMode.values();
            blockEntity.setMode(modes[(modeIdx + 1) % modes.length]);
            player.sendSystemMessage(Component.translatable("tooltip.utilitix.piston_cart_mode", blockEntity.getMode().name));
        }

        return InteractionResult.SUCCESS;
    }

    @Override
    protected void entityInside(@Nonnull BlockState state, @Nonnull Level level, @Nonnull BlockPos pos, @Nonnull Entity entity, @Nonnull InsideBlockEffectApplier effectApplier, boolean isPrecise) {
        super.entityInside(state, level, pos, entity, effectApplier, isPrecise);
        if (!(entity instanceof PistonCart pistonCart)) {
            return;
        }

        PistonControllerRailBlockEntity blockEntity = this.getBlockEntity(level, pos);
        ItemStack filterThis = blockEntity.getFilterStack();
        if (!filterThis.isEmpty()) {
            ItemStack filterCart = MinecartTinkererItem.getLabelStack(pistonCart);
            if (filterCart.isEmpty()) {
                return;
            }

            if (!ItemStack.isSameItemSameComponents(filterThis, filterCart)) {
                return;
            }
        }

        pistonCart.setMode(blockEntity.getMode());
    }

    @Nonnull
    @Override
    public Property<RailShape> getShapeProperty() {
        return BlockStateProperties.RAIL_SHAPE_STRAIGHT;
    }

    @Nonnull
    @Override
    protected MapCodec<? extends BaseRailBlock> codec() {
        return CODEC;
    }
}
