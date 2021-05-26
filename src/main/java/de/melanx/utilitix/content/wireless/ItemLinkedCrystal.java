package de.melanx.utilitix.content.wireless;

import io.github.noeppi_noeppi.libx.mod.ModX;
import io.github.noeppi_noeppi.libx.mod.registration.ItemBase;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

public class ItemLinkedCrystal extends ItemBase {

    public ItemLinkedCrystal(ModX mod, Properties properties) {
        super(mod, properties);
    }

    @Nonnull
    @Override
    public ActionResult<ItemStack> onItemRightClick(@Nonnull World world, @Nonnull PlayerEntity player, @Nonnull Hand hand) {
        ItemStack held = player.getHeldItem(hand);
        UUID uid = getId(held);
        if (uid != null) {
            if (!world.isRemote) {
                WirelessStorage storage = WirelessStorage.get(world);
                int strength = storage.getStrength(uid);
                player.sendMessage(new TranslationTextComponent("tooltip.utilitix.signal_strength", new StringTextComponent(Integer.toString(strength)).mergeStyle(TextFormatting.RED)), player.getUniqueID());
            }
            return ActionResult.successOrConsume(held, world.isRemote);
        } else if (held.getCount() < 2) {
            if (world.isRemote) {
                player.sendStatusMessage(new TranslationTextComponent("tooltip.utilitix.link_failed"), true);
            }
            return new ActionResult<>(ActionResultType.FAIL, held);
        } else {
            if (!world.isRemote) {
                ItemStack stack = held.copy();
                stack.getOrCreateTag().putUniqueId("redstone_id", UUID.randomUUID());
                player.dropItem(stack, false);
            }
            player.setHeldItem(hand, ItemStack.EMPTY);
            return ActionResult.successOrConsume(ItemStack.EMPTY, world.isRemote);
        }
    }
    
    @Override
    public void addInformation(@Nonnull ItemStack stack, @Nullable World worldIn, @Nonnull List<ITextComponent> tooltip, @Nonnull ITooltipFlag flag) {
        super.addInformation(stack, worldIn, tooltip, flag);
        UUID uid = getId(stack);
        if (uid == null) {
            tooltip.add(new TranslationTextComponent("tooltip.utilitix.invalid_link").mergeStyle(TextFormatting.RED));
        } else {
            tooltip.add(new TranslationTextComponent("tooltip.utilitix.valid_link", new StringTextComponent(uid.toString()).mergeStyle(TextFormatting.GREEN)).mergeStyle(TextFormatting.RED));
        }
    }
    
    @Nullable
    public static UUID getId(ItemStack stack) {
        if (!stack.hasTag()) {
            return null;
        } else {
            try {
                return stack.getOrCreateTag().getUniqueId("redstone_id");
            } catch (Exception e) {
                return null;
            }
        }
    }
}
