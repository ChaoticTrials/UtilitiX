package de.melanx.utilitix.content.backpack;

import de.melanx.utilitix.config.CommonConfig;
import de.melanx.utilitix.registration.ModDataComponentTypes;
import de.melanx.utilitix.registration.ModItems;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.DyedItemColor;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.access.ItemAccess;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.item.ItemUtil;
import org.moddingx.libx.base.ItemBase;
import org.moddingx.libx.menu.type.AdvancedMenuType;
import org.moddingx.libx.mod.ModX;
import org.moddingx.libx.registration.Registerable;
import org.moddingx.libx.registration.RegistrationContext;
import org.moddingx.libx.registration.util.CapabilityInfo;
import org.moddingx.libx.registration.util.ClientExtensionInfo;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class BackpackItem extends ItemBase implements Registerable {

    public final AdvancedMenuType<BackpackMenu, ItemStack> menuType;

    public BackpackItem(ModX mod, Properties properties) {
        super(mod, properties);
        this.menuType = AdvancedMenuType.create(BackpackMenu::new, ItemStack.STREAM_CODEC);
    }

    public static void openMenu(ItemStack stack, Player player) {
        if (player instanceof ServerPlayer serverPlayer) {
            ModItems.backpack.menuType.open(serverPlayer, Component.translatable("screen.utilitix.backpack"), stack);
        }
    }

    @Nonnull
    @Override
    public InteractionResult use(@Nonnull Level level, @Nonnull Player player, @Nonnull InteractionHand usedHand) {
        BackpackItem.openMenu(player.getItemInHand(usedHand), player);

        return InteractionResult.SUCCESS;
    }

    @Override
    public boolean canFitInsideContainerItems(@Nonnull ItemStack stack) {
        return false;
    }

    @Override
    public boolean overrideOtherStackedOnMe(@Nonnull ItemStack stack, @Nonnull ItemStack other, @Nonnull Slot slot, @Nonnull ClickAction action, @Nonnull Player player, @Nonnull SlotAccess access) {
        if (stack.getCount() != 1) {
            return false;
        }

        if (action != ClickAction.SECONDARY || !slot.allowModification(player)) {
            return false;
        }

        if (!other.is(ModItems.backpack)) {
            return false;
        }

        boolean upgraded = this.upgrade(stack, other);
        if (upgraded) {
            player.playSound(SoundEvents.BUNDLE_INSERT, 0.8f, 0.8f + player.level().getRandom().nextFloat() * 0.4f);
            other.shrink(1);
        }

        return upgraded;
    }

    @SuppressWarnings("deprecation")
    @Override
    public void appendHoverText(@Nonnull ItemStack stack, @Nonnull TooltipContext context, @Nonnull TooltipDisplay tooltipDisplay, @Nonnull Consumer<Component> tooltipComponents, @Nonnull TooltipFlag tooltipFlag) {
        if (tooltipFlag.isAdvanced()) {
            tooltipComponents.accept(Component.translatable("item.utilitix.backpack.slots", BackpackItem.slotSize(stack)));
        }
    }

    public boolean upgrade(ItemStack backpack, ItemStack otherBackpack) {
        int backpackSlots = BackpackItem.slotSize(backpack);
        int otherBackpackSlots = BackpackItem.slotSize(otherBackpack);
        if (backpackSlots < 0 || otherBackpackSlots < 0) {
            return false;
        }

        int newSize = backpackSlots + otherBackpackSlots;
        if (newSize > CommonConfig.Backpack.maxSize) {
            return false;
        }

        ResourceHandler<ItemResource> donor = otherBackpack.getCapability(Capabilities.Item.ITEM, ItemAccess.forStack(otherBackpack));
        if (donor == null) {
            return false;
        }

        List<ItemStack> donorContents = new ArrayList<>(donor.size());
        for (int i = 0; i < donor.size(); i++) {
            donorContents.add(ItemUtil.getStack(donor, i));
        }

        backpack.set(ModDataComponentTypes.inventorySize, newSize);
        if (!(backpack.getCapability(Capabilities.Item.ITEM, ItemAccess.forStack(backpack)) instanceof VariableSizeStackItemHandler itemHandler)) {
            backpack.set(ModDataComponentTypes.inventorySize, backpackSlots);
            return false;
        }

        for (int i = 0; i < donorContents.size(); i++) {
            ItemStack slotStack = donorContents.get(i);
            if (!slotStack.isEmpty()) {
                itemHandler.set(backpackSlots + i, ItemResource.of(slotStack), slotStack.getCount());
            }
        }

        BackpackItem.combineDyeableItemColors(backpack, otherBackpack);
        return true;
    }

    public static boolean isEmpty(ItemStack stack) {
        ResourceHandler<ItemResource> inventory = stack.getCapability(Capabilities.Item.ITEM, ItemAccess.forStack(stack));

        if (inventory != null) {
            for (int i = 0; i < inventory.size(); i++) {
                if (!inventory.getResource(i).isEmpty()) {
                    return false;
                }
            }
        }

        return true;
    }

    public static int slotSize(ItemStack stack) {
        ResourceHandler<ItemResource> itemHandler = stack.getCapability(Capabilities.Item.ITEM, ItemAccess.forStack(stack));
        if (itemHandler == null) {
            return -1;
        }

        return itemHandler.size();
    }

    private static void combineDyeableItemColors(ItemStack stack, ItemStack other) {
        if (!stack.has(DataComponents.DYED_COLOR) && !other.has(DataComponents.DYED_COLOR)) {
            return;
        }

        int[] total = new int[3];
        int maxTotal = 0;
        int count = 0;

        DyedItemColor stackColor = stack.get(DataComponents.DYED_COLOR);
        if (stackColor != null) {
            int color = stackColor.rgb();
            float r = (float) ((color >> 16) & 0xFF) / 0xFF;
            float g = (float) ((color >> 8) & 0xFF) / 0xFF;
            float b = (float) ((color) & 0xFF) / 0xFF;
            maxTotal += (int) (Math.max(r, Math.max(g, b)) * 0xFF);
            total[0] += (int) (r * 0xFF);
            total[1] += (int) (g * 0xFF);
            total[2] += (int) (b * 0xFF);
            count++;
        }

        DyedItemColor otherColor = other.get(DataComponents.DYED_COLOR);
        if (otherColor != null) {
            int color = otherColor.rgb();
            float r = (float) ((color >> 16) & 0xFF) / 0xFF;
            float g = (float) ((color >> 8) & 0xFF) / 0xFF;
            float b = (float) ((color) & 0xFF) / 0xFF;
            maxTotal += (int) (Math.max(r, Math.max(g, b)) * 0xFF);
            total[0] += (int) (r * 0xFF);
            total[1] += (int) (g * 0xFF);
            total[2] += (int) (b * 0xFF);
            count++;
        }

        if (count == 0) {
            return;
        }

        int r = total[0] / count;
        int g = total[1] / count;
        int b = total[2] / count;
        float average = (float) maxTotal / (float) count;
        float max = (float) Math.max(r, Math.max(g, b));
        r = (int) ((float) r * average / max);
        g = (int) ((float) g * average / max);
        b = (int) ((float) b * average / max);

        int color = (r << 8) + g;
        color = (color << 8) + b;
        stack.set(DataComponents.DYED_COLOR, new DyedItemColor(color));
        other.set(DataComponents.DYED_COLOR, new DyedItemColor(color));
    }

    @Override
    public void registerAdditional(RegistrationContext ctx, EntryCollector builder) {
        builder.register(null, new CapabilityInfo.Item<>(this, Capabilities.Item.ITEM, (stack, itemAccess) -> new VariableSizeStackItemHandler(Math.min(stack.getOrDefault(ModDataComponentTypes.inventorySize, CommonConfig.Backpack.slotSize), CommonConfig.Backpack.maxSize), itemAccess)));
        builder.register(Registries.MENU, this.menuType);
    }

    @Override
    public void registerClientAdditional(RegistrationContext ctx, EntryCollector builder) {
        builder.register(null, new ClientExtensionInfo.MenuScreen<>(this.menuType, BackpackScreen::new));
    }
}
