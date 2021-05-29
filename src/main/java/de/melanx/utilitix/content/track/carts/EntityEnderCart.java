package de.melanx.utilitix.content.track.carts;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.container.ChestContainer;
import net.minecraft.inventory.container.SimpleNamedContainerProvider;
import net.minecraft.item.Items;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

public class EntityEnderCart extends EntityCart {

    public EntityEnderCart(EntityType<?> type, World worldIn) {
        super(type, worldIn);
    }

    @Nonnull
    @Override
    public BlockState getDefaultDisplayTile() {
        return Blocks.ENDER_CHEST.getDefaultState();
    }

    @Override
    public int getDefaultDisplayTileOffset() {
        return 8;
    }

    @Override
    public void killMinecart(@Nonnull DamageSource source) {
        super.killMinecart(source);
        this.entityDropItem(Items.ENDER_CHEST);
    }

    @Nonnull
    @Override
    public ActionResultType processInitialInteract(@Nonnull PlayerEntity player, @Nonnull Hand hand) {
        ActionResultType ret = super.processInitialInteract(player, hand);
        if (ret.isSuccessOrConsume()) return ret;
        Inventory ender = player.getInventoryEnderChest();
        player.openContainer(new SimpleNamedContainerProvider((id, inventory, usingPlayer) -> ChestContainer.createGeneric9X3(id, inventory, ender), this.getDisplayName()));
        return ActionResultType.successOrConsume(player.world.isRemote);
    }
}
