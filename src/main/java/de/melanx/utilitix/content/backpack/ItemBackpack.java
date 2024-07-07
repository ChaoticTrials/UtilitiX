package de.melanx.utilitix.content.backpack;

import de.melanx.utilitix.UtilitiXConfig;
import de.melanx.utilitix.registration.ModItems;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.DyeableLeatherItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.items.IItemHandlerModifiable;
import org.apache.commons.lang3.tuple.Pair;
import org.moddingx.libx.base.ItemBase;
import org.moddingx.libx.inventory.BaseItemStackHandler;
import org.moddingx.libx.mod.ModX;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Predicate;

public class ItemBackpack extends ItemBase implements DyeableLeatherItem {

    public static final Predicate<ItemStack> SLOT_VALIDATOR = item -> !(item.getItem() instanceof ItemBackpack);

    public ItemBackpack(ModX mod, Properties properties) {
        super(mod, properties);
    }

    @Nonnull
    @Override
    public InteractionResultHolder<ItemStack> use(@Nonnull Level level, @Nonnull Player player, @Nonnull InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (!level.isClientSide) {
            AtomicReference<BaseItemStackHandler> handler = new AtomicReference<>(null);
            handler.set(BaseItemStackHandler.builder(ItemBackpack.slotSize(stack))
                    .contentsChanged(slot -> {
                        stack.getOrCreateTag().put("Items", handler.get().serializeNBT());
                        player.setItemInHand(hand, stack);
                    })
                    .validator(SLOT_VALIDATOR)
                    .build());

            if (!ItemBackpack.isEmpty(stack)) {
                handler.get().deserializeNBT(stack.getOrCreateTag().getCompound("Items"));
            }

            if (player instanceof ServerPlayer serverPlayer) {
                BackpackMenu.open(serverPlayer, handler.get(), stack);
            }

            return InteractionResultHolder.sidedSuccess(stack, false);
        }

        return InteractionResultHolder.pass(stack);
    }

    @Override
    public boolean canFitInsideContainerItems() {
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
            player.playSound(SoundEvents.BUNDLE_INSERT, 0.8f, 0.8f + player.level().random.nextFloat() * 0.4f);
            other.shrink(1);
        }

        return upgraded;
    }

    @Nullable
    private static IItemHandlerModifiable getInventory(ItemStack stack) {
        if (!stack.hasTag() || !stack.getOrCreateTag().contains("Items")) {
            return null;
        }

        AtomicReference<BaseItemStackHandler> handler = new AtomicReference<>(null);
        handler.set(BaseItemStackHandler.builder(ItemBackpack.slotSize(stack))
                .contentsChanged(slot -> stack.getOrCreateTag().put("Items", handler.get().serializeNBT()))
                .validator(SLOT_VALIDATOR)
                .build()
        );

        handler.get().deserializeNBT(stack.getOrCreateTag().getCompound("Items"));

        return handler.get();
    }

    public static boolean isEmpty(ItemStack stack) {
        IItemHandlerModifiable inventory = ItemBackpack.getInventory(stack);

        if (inventory != null) {
            for (int i = 0; i < inventory.getSlots(); i++) {
                ItemStack itemStack = inventory.extractItem(i, 1, true);
                if (!itemStack.isEmpty()) {
                    return false;
                }
            }
        }

        return true;
    }

    public boolean upgrade(ItemStack backpack, ItemStack otherBackpack) {
        int backpackSlots = ItemBackpack.slotSize(backpack);
        int otherBackpackSlots = ItemBackpack.slotSize(otherBackpack);
        if (backpackSlots + otherBackpackSlots > UtilitiXConfig.Backpack.maxSize) {
            return false;
        }

        Set<Pair<Integer, ItemStack>> totalSlots = new HashSet<>(ItemBackpack.getSlots(backpack));
        for (Pair<Integer, ItemStack> slot : ItemBackpack.getSlots(otherBackpack)) {
            totalSlots.add(Pair.of(slot.getLeft() + backpackSlots, slot.getRight()));
        }

        CompoundTag tag = new CompoundTag();
        tag.putInt("Size", backpackSlots + otherBackpackSlots);
        tag.put("Items", ItemBackpack.createSlots(totalSlots));
        backpack.getOrCreateTag().put("Items", tag);

        ItemBackpack.combineDyeableItemColors(backpack, otherBackpack);
        return true;
    }

    public static int slotSize(ItemStack stack) {
        CompoundTag tag = stack.getOrCreateTag();
        if (!tag.contains("Items", Tag.TAG_COMPOUND)) {
            return UtilitiXConfig.Backpack.slotSize;
        }

        CompoundTag items = tag.getCompound("Items");
        if (!items.contains("Size", Tag.TAG_ANY_NUMERIC)) {
            return UtilitiXConfig.Backpack.slotSize;
        }

        return items.getInt("Size");
    }

    private static ListTag createSlots(Set<Pair<Integer, ItemStack>> slots) {
        ListTag list = new ListTag();
        for (Pair<Integer, ItemStack> slot : slots) {
            CompoundTag itemTag = new CompoundTag();
            itemTag.putInt("Slot", slot.getLeft());
            slot.getRight().save(itemTag);
            list.add(itemTag);
        }

        return list;
    }

    private static Set<Pair<Integer, ItemStack>> getSlots(ItemStack backpack) {
        CompoundTag tag = backpack.getOrCreateTag();
        if (!tag.contains("Items", Tag.TAG_COMPOUND)) {
            return Set.of();
        }

        Set<Pair<Integer, ItemStack>> slots = new HashSet<>();
        CompoundTag items = tag.getCompound("Items");
        ListTag tagList = items.getList("Items", Tag.TAG_COMPOUND);
        for (int i = 0; i < tagList.size(); i++) {
            CompoundTag itemTags = tagList.getCompound(i);
            int slot = itemTags.getInt("Slot");
            slots.add(Pair.of(slot, ItemStack.of(itemTags)));
        }

        return slots;
    }

    @Override
    public void appendHoverText(@Nonnull ItemStack stack, @Nullable Level level, @Nonnull List<Component> tooltipComponents, @Nonnull TooltipFlag isAdvanced) {
        if (isAdvanced.isAdvanced()) {
            tooltipComponents.add(Component.translatable(this.getDescriptionId() + ".slots", ItemBackpack.slotSize(stack)).withStyle(ChatFormatting.GRAY));
        }
    }

    private static void combineDyeableItemColors(ItemStack stack, ItemStack other) {
        if (!(stack.getItem() instanceof DyeableLeatherItem backpack) || !(other.getItem() instanceof DyeableLeatherItem otherBackpack)) {
            return;
        }

        int[] total = new int[3];
        int maxTotal = 0;
        int count = 0;

        if (backpack.hasCustomColor(stack)) {
            int color = backpack.getColor(stack);
            float r = (float) ((color >> 16) & 0xFF) / 0xFF;
            float g = (float) ((color >> 8) & 0xFF) / 0xFF;
            float b = (float) ((color) & 0xFF) / 0xFF;
            maxTotal += (int) (Math.max(r, Math.max(g, b)) * 0xFF);
            total[0] += (int) (r * 0xFF);
            total[1] += (int) (g * 0xFF);
            total[2] += (int) (b * 0xFF);
            count++;
        }

        if (otherBackpack.hasCustomColor(other)) {
            int color = otherBackpack.getColor(other);
            float r = (float) ((color >> 16) & 0xFF) / 0xFF;
            float g = (float) ((color >> 8) & 0xFF) / 0xFF;
            float b = (float) ((color) & 0xFF) / 0xFF;
            maxTotal += (int) (Math.max(r, Math.max(g, b)) * 0xFF);
            total[0] += (int) (r * 0xFF);
            total[1] += (int) (g * 0xFF);
            total[2] += (int) (b * 0xFF);
            count++;
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
        backpack.setColor(stack, color);
    }
}
