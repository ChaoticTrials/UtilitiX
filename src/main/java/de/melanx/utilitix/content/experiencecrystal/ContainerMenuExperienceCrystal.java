package de.melanx.utilitix.content.experiencecrystal;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.Level;
import org.moddingx.libx.menu.BlockEntityMenu;

import javax.annotation.Nullable;

public class ContainerMenuExperienceCrystal extends BlockEntityMenu<TileExperienceCrystal> {

    public ContainerMenuExperienceCrystal(@Nullable MenuType<? extends BlockEntityMenu<?>> type, int windowId, Level level, BlockPos pos, Inventory playerContainer, Player player) {
        super(type, windowId, level, pos, playerContainer, player, 0, 0);
        this.layoutPlayerInventorySlots(8, 94);
    }
}
