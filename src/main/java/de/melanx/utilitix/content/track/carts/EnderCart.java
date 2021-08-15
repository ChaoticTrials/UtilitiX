package de.melanx.utilitix.content.track.carts;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nonnull;

public class EnderCart extends Cart {

    public EnderCart(EntityType<?> type, Level level) {
        super(type, level);
    }

    @Nonnull
    @Override
    public BlockState getDefaultDisplayBlockState() {
        return Blocks.ENDER_CHEST.defaultBlockState();
    }

    @Override
    public int getDefaultDisplayOffset() {
        return 8;
    }

    @Override
    public void destroy(@Nonnull DamageSource source) {
        super.destroy(source);
        this.spawnAtLocation(Items.ENDER_CHEST);
    }

    @Nonnull
    @Override
    public InteractionResult interact(@Nonnull Player player, @Nonnull InteractionHand hand) {
        InteractionResult ret = super.interact(player, hand);
        if (ret.consumesAction()) return ret;
        SimpleContainer ender = player.getEnderChestInventory();
        player.openMenu(new SimpleMenuProvider((id, inventory, usingPlayer) -> ChestMenu.threeRows(id, inventory, ender), this.getDisplayName()));
        return InteractionResult.sidedSuccess(player.level.isClientSide);
    }
}
