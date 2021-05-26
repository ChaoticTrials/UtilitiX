package de.melanx.utilitix.content.bell;

import de.melanx.utilitix.UtilitiX;
import io.github.noeppi_noeppi.libx.mod.ModX;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class ItemMobBell extends BellBase {

    private static final IFormattableTextComponent NO_MOB = new TranslationTextComponent("tooltip." + UtilitiX.getInstance().modid + ".no_mob").mergeStyle(TextFormatting.DARK_RED);

    public ItemMobBell(ModX mod, Item.Properties properties) {
        super(mod, properties.setISTER(() -> RenderBell::new));
    }

    @Override
    protected boolean entityFilter(LivingEntity entity, ItemStack stack) {
        if (!stack.hasTag()) {
            return false;
        }
        if (!stack.getOrCreateTag().contains("Entity")) {
            return false;
        }
        String s = stack.getOrCreateTag().getString("Entity");
        return EntityType.getKey(entity.getType()).equals(ResourceLocation.tryCreate(s));
    }

    @Override
    protected boolean notifyNearbyEntities() {
        return false;
    }

    @Override
    public void addInformation(@Nonnull ItemStack stack, @Nullable World world, @Nonnull List<ITextComponent> tooltip, @Nonnull ITooltipFlag flag) {
        super.addInformation(stack, world, tooltip, flag);
        IFormattableTextComponent component = getCurrentMob(stack);
        tooltip.add(component != null ? component : NO_MOB);
    }

    @Nullable
    public static IFormattableTextComponent getCurrentMob(ItemStack stack) {
        String s = stack.getOrCreateTag().getString("Entity");
        Optional<EntityType<?>> entityType = EntityType.byKey(s);

        return entityType.map(ItemMobBell::getCurrentMob).orElse(null);
    }

    @Nonnull
    public static IFormattableTextComponent getCurrentMob(EntityType<?> entityType) {
        ITextComponent name = entityType.getName();
        IFormattableTextComponent component = new TranslationTextComponent("tooltip." + UtilitiX.getInstance().modid + ".current_mob");
        component.mergeStyle(entityType.getClassification() == EntityClassification.MONSTER ? TextFormatting.RED : TextFormatting.GOLD);

        return component.appendString(": ").appendSibling(name);
    }
    
    public static int getColor(ItemStack stack) {
        if (stack.getTag() != null && stack.getTag().contains("Entity", Constants.NBT.TAG_STRING)) {
            ResourceLocation rl = ResourceLocation.tryCreate(stack.getTag().getString("Entity"));
            EntityType<?> entityType = rl == null ? null : ForgeRegistries.ENTITIES.getValue(rl);
            SpawnEggItem egg = entityType == null ? null : SpawnEggItem.getEgg(entityType);
            if (egg != null) {
                return Objects.requireNonNull(egg).getColor(0);
            }
        }
        return 0xFFFFFF;
    }
    
    public static float[] getFloatColor(ItemStack stack) {
        int color = getColor(stack);
        float r = ((color >> 16) & 0xFF) / 255f;
        float g = ((color >> 8) & 0xFF) / 255f;
        float b = (color & 0xFF) / 255f;
        return new float[]{ r, g, b };
    }
}
