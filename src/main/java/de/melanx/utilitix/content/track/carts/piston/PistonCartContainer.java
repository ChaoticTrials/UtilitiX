package de.melanx.utilitix.content.track.carts.piston;

import de.melanx.utilitix.content.track.carts.EntityPistonCart;
import io.github.noeppi_noeppi.libx.annotation.RegName;
import io.github.noeppi_noeppi.libx.annotation.RegisterClass;
import io.github.noeppi_noeppi.libx.inventory.container.ContainerBaseEntity;
import io.github.noeppi_noeppi.libx.inventory.slot.SlotOutputOnly;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.world.World;
import net.minecraftforge.items.SlotItemHandler;

import javax.annotation.Nullable;

@RegisterClass
public class PistonCartContainer extends ContainerBaseEntity<EntityPistonCart> {

    @RegName("piston_cart_container")
    public static final ContainerType<PistonCartContainer> TYPE = createContainerType(PistonCartContainer::new);
    
    protected PistonCartContainer(@Nullable ContainerType<?> type, int windowId, World world, int entityId, PlayerInventory playerInventory, PlayerEntity player) {
        super(type, windowId, world, entityId, playerInventory, player, 13, 25);
        this.addSlotBox(this.entity.getRailInputInventory(), 0, 8, 18, 3, 18, 4, 18);
        this.addSlot(new SlotItemHandler(this.entity.getTorchInventory(), 0, 80, 72));
        this.addSlotBox(this.entity.getRailOutputInventory(), 0, 116, 18, 3, 18, 4, 18, SlotOutputOnly::new);
        this.layoutPlayerInventorySlots(8, 104);
    }
}
