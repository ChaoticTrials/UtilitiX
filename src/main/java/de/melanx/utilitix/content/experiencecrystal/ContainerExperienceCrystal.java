package de.melanx.utilitix.content.experiencecrystal;

import io.github.noeppi_noeppi.libx.inventory.container.ContainerBase;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class ContainerExperienceCrystal extends ContainerBase<TileExperienceCrystal> {

    public ContainerExperienceCrystal(@Nullable ContainerType<?> type, int windowId, World world, BlockPos pos, PlayerInventory playerInventory, PlayerEntity player) {
        super(type, windowId, world, pos, playerInventory, player, 0, 0);
        this.layoutPlayerInventorySlots(8, 94);
    }
}
