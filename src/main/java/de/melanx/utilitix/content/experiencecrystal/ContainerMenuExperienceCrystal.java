package de.melanx.utilitix.content.experiencecrystal;

import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.Level;
import org.moddingx.libx.menu.BlockEntityMenu;
import org.moddingx.libx.menu.type.AdvancedMenuType;

import javax.annotation.Nullable;

public class ContainerMenuExperienceCrystal extends BlockEntityMenu<TileExperienceCrystal> {

    public static final AdvancedMenuType<ContainerMenuExperienceCrystal, BlockPos> TYPE = AdvancedMenuType.create(ContainerMenuExperienceCrystal::new,
            StreamCodec.of(
                    (RegistryFriendlyByteBuf buf, BlockPos pos) -> buf.writeBlockPos(pos),
                    buffer -> RegistryFriendlyByteBuf.readBlockPos(buffer)
            ));

    public ContainerMenuExperienceCrystal(@Nullable MenuType<? extends BlockEntityMenu<?>> type, int windowId, Level level, BlockPos pos, Player player, Inventory playerContainer) {
        super(type, windowId, level, pos, player, playerContainer, 0, 0);
        this.layoutPlayerInventorySlots(8, 94);
    }
}
