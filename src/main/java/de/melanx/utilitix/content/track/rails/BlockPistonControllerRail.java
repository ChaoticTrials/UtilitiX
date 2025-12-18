package de.melanx.utilitix.content.track.rails;

import com.mojang.serialization.MapCodec;
import de.melanx.utilitix.UtilitiX;
import de.melanx.utilitix.content.track.ItemMinecartTinkerer;
import de.melanx.utilitix.content.track.carts.PistonCart;
import de.melanx.utilitix.content.track.carts.piston.PistonCartMode;
import de.melanx.utilitix.registration.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
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

public class BlockPistonControllerRail extends BlockControllerRail<TilePistonControllerRail> {

    public static final MapCodec<BlockPistonControllerRail> CODEC = Block.simpleCodec(BlockPistonControllerRail::new);

    public BlockPistonControllerRail(Properties properties) {
        this(UtilitiX.getInstance(), false, properties);
    }

    public BlockPistonControllerRail(ModX mod, boolean reinforced, Properties properties) {
        super(mod, TilePistonControllerRail::new, reinforced, properties);
    }

    public BlockPistonControllerRail(ModX mod, boolean reinforced, Properties properties, Item.Properties itemProperties) {
        super(mod, TilePistonControllerRail::new, reinforced, properties, itemProperties);
    }

    @Nonnull
    @Override
    protected ItemInteractionResult useItemOn(@Nonnull ItemStack stack, @Nonnull BlockState state, @Nonnull Level level, @Nonnull BlockPos pos, Player player, @Nonnull InteractionHand hand, @Nonnull BlockHitResult hitResult) {
        ItemInteractionResult result = super.useItemOn(stack, state, level, pos, player, hand, hitResult);
        if (result.consumesAction()) {
            return result;
        } else {
            ItemStack held = player.getItemInHand(hand);
            if (held.isEmpty() || held.getItem() != ModItems.minecartTinkerer) {
                return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
            }

            if (!level.isClientSide) {
                TilePistonControllerRail tile = this.getTile(level, pos);
                int modeIdx = tile.getMode().ordinal();
                PistonCartMode[] modes = PistonCartMode.values();
                tile.setMode(modes[(modeIdx + 1) % modes.length]);
                player.sendSystemMessage(Component.translatable("tooltip.utilitix.piston_cart_mode", tile.getMode().name));
            }

            return ItemInteractionResult.sidedSuccess(level.isClientSide);
        }
    }

    @Override
    public void onMinecartPass(@Nonnull BlockState state, @Nonnull Level level, @Nonnull BlockPos pos, @Nonnull AbstractMinecart cart) {
        if (!(cart instanceof PistonCart)) {
            return;
        }
        TilePistonControllerRail tile = this.getTile(level, pos);
        ItemStack filterThis = tile.getFilterStack();
        if (!filterThis.isEmpty()) {
            ItemStack filterCart = ItemMinecartTinkerer.getLabelStack(cart);
            if (filterCart.isEmpty()) {
                return;
            } else if (!ItemStack.isSameItemSameComponents(filterThis, filterCart)) {
                return;
            }
        }
        ((PistonCart) cart).setMode(tile.getMode());
    }

    @Nonnull
    @Override
    public Property<RailShape> getShapeProperty() {
        return BlockStateProperties.RAIL_SHAPE_STRAIGHT;
    }

    @Override
    protected MapCodec<? extends BaseRailBlock> codec() {
        return CODEC;
    }
}
