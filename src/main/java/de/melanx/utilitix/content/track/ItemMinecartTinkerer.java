package de.melanx.utilitix.content.track;

import io.github.noeppi_noeppi.libx.inventory.container.GenericContainer;
import io.github.noeppi_noeppi.libx.mod.ModX;
import io.github.noeppi_noeppi.libx.mod.registration.ItemBase;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.minecart.AbstractMinecartEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemStackHandler;

public class ItemMinecartTinkerer extends ItemBase {

    public ItemMinecartTinkerer(ModX mod, Properties properties) {
        super(mod, properties);
    }

    @Override
    public boolean onLeftClickEntity(ItemStack stack, PlayerEntity player, Entity entity) {
        if (entity instanceof AbstractMinecartEntity) {
            World world = player.world;
            if (!world.isRemote && player instanceof ServerPlayerEntity) {
                IItemHandlerModifiable handler = new ItemStackHandler(1) {
                    
                    @Override
                    public int getSlotLimit(int slot) {
                        return 1;
                    }

                    @Override
                    protected void onContentsChanged(int slot) {
                        if (slot == 0) {
                            setLabelStack((AbstractMinecartEntity) entity, this.getStackInSlot(0));
                        }
                    }
                };
                handler.setStackInSlot(0, getLabelStack((AbstractMinecartEntity) entity));
                GenericContainer.open((ServerPlayerEntity) player, handler, new TranslationTextComponent("screen.utilitix.minecart_tinkerer"), null);
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean doesSneakBypassUse(ItemStack stack, IWorldReader world, BlockPos pos, PlayerEntity player) {
        return true;
    }
    
    public static ItemStack getLabelStack(AbstractMinecartEntity entity) {
        CompoundNBT nbt = entity.getPersistentData();
        return ItemStack.read(nbt.getCompound("utilitix_minecart_label_item"));
    }
    
    public static void setLabelStack(AbstractMinecartEntity entity, ItemStack stack) {
        CompoundNBT nbt = entity.getPersistentData();
        nbt.put("utilitix_minecart_label_item", stack.write(new CompoundNBT()));
    }
}
