package de.melanx.utilitix.content.track.rails;

import de.melanx.utilitix.content.track.ItemMinecartTinkerer;
import de.melanx.utilitix.content.track.carts.EntityPistonCart;
import de.melanx.utilitix.content.track.carts.piston.PistonCartMode;
import de.melanx.utilitix.registration.ModItems;
import io.github.noeppi_noeppi.libx.mod.ModX;
import net.minecraft.block.BlockState;
import net.minecraft.entity.item.minecart.AbstractMinecartEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

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
    public ActionResultType onBlockActivated(@Nonnull BlockState state, @Nonnull World world, @Nonnull BlockPos pos, @Nonnull PlayerEntity player, @Nonnull Hand hand, @Nonnull BlockRayTraceResult hit) {
        ActionResultType result = super.onBlockActivated(state, world, pos, player, hand, hit);
        if (result.isSuccessOrConsume()) {
            return result;
        } else {
            ItemStack held = player.getHeldItem(hand);
            if (!held.isEmpty() && held.getItem() == ModItems.minecartTinkerer) {
                if (!world.isRemote) {
                    TilePistonControllerRail tile = this.getTile(world, pos);
                    int modeIdx = tile.getMode().ordinal();
                    PistonCartMode[] modes = PistonCartMode.values();
                    tile.setMode(modes[(modeIdx + 1) % modes.length]);
                    player.sendMessage(new TranslationTextComponent("tooltip.utilitix.piston_cart_mode", tile.getMode().name), player.getUniqueID());
                }
                return ActionResultType.successOrConsume(world.isRemote);
            } else {
                return ActionResultType.PASS;
            }
        }
    }

    @Override
    public void onMinecartPass(BlockState state, World world, BlockPos pos, AbstractMinecartEntity cart) {
        if (!(cart instanceof EntityPistonCart)) {
            return;
        }
        TilePistonControllerRail tile = this.getTile(world, pos);
        ItemStack filterThis = tile.getFilterStack();
        if (!filterThis.isEmpty()) {
            ItemStack filterCart = ItemMinecartTinkerer.getLabelStack(cart);
            if (filterCart.isEmpty()) {
                return;
            } else if (!ItemStack.areItemsEqual(filterThis, filterCart) || !ItemStack.areItemStackTagsEqual(filterThis, filterCart)) {
                return;
            }
        }
        ((EntityPistonCart) cart).setMode(tile.getMode());
    }
}
