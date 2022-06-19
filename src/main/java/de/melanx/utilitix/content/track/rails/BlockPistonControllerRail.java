package de.melanx.utilitix.content.track.rails;

import de.melanx.utilitix.content.track.ItemMinecartTinkerer;
import de.melanx.utilitix.content.track.carts.PistonCart;
import de.melanx.utilitix.content.track.carts.piston.PistonCartMode;
import de.melanx.utilitix.registration.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.moddingx.libx.mod.ModX;

import javax.annotation.Nonnull;

public abstract class BlockPistonControllerRail extends BlockControllerRail<TilePistonControllerRail> {

    public BlockPistonControllerRail(ModX mod, boolean reinforced, Properties properties) {
        super(mod, TilePistonControllerRail::new, reinforced, properties);
    }

    public BlockPistonControllerRail(ModX mod, boolean reinforced, Properties properties, Item.Properties itemProperties) {
        super(mod, TilePistonControllerRail::new, reinforced, properties, itemProperties);
    }

    @Nonnull
    @Override
    public InteractionResult use(@Nonnull BlockState state, @Nonnull Level level, @Nonnull BlockPos pos, @Nonnull Player player, @Nonnull InteractionHand hand, @Nonnull BlockHitResult hit) {
        InteractionResult result = super.use(state, level, pos, player, hand, hit);
        if (result.consumesAction()) {
            return result;
        } else {
            ItemStack held = player.getItemInHand(hand);
            if (!held.isEmpty() && held.getItem() == ModItems.minecartTinkerer) {
                if (!level.isClientSide) {
                    TilePistonControllerRail tile = this.getTile(level, pos);
                    int modeIdx = tile.getMode().ordinal();
                    PistonCartMode[] modes = PistonCartMode.values();
                    tile.setMode(modes[(modeIdx + 1) % modes.length]);
                    player.sendSystemMessage(Component.translatable("tooltip.utilitix.piston_cart_mode", tile.getMode().name));
                }
                return InteractionResult.sidedSuccess(level.isClientSide);
            } else {
                return InteractionResult.PASS;
            }
        }
    }

    @Override
    public void onMinecartPass(BlockState state, Level level, BlockPos pos, AbstractMinecart cart) {
        if (!(cart instanceof PistonCart)) {
            return;
        }
        TilePistonControllerRail tile = this.getTile(level, pos);
        ItemStack filterThis = tile.getFilterStack();
        if (!filterThis.isEmpty()) {
            ItemStack filterCart = ItemMinecartTinkerer.getLabelStack(cart);
            if (filterCart.isEmpty()) {
                return;
            } else if (!ItemStack.isSame(filterThis, filterCart) || !ItemStack.tagMatches(filterThis, filterCart)) {
                return;
            }
        }
        ((PistonCart) cart).setMode(tile.getMode());
    }
}
