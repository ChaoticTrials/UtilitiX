package de.melanx.utilitix.content.bell;

import de.melanx.utilitix.UtilitiX;
import de.melanx.utilitix.util.MobUtil;
import io.github.noeppi_noeppi.libx.mod.ModX;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class ItemMobBell extends BellBase {

    public ItemMobBell(ModX mod, Item.Properties properties) {
        super(mod, properties);
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
        return EntityType.getKey(entity.getType()).equals(ResourceLocation.tryParse(s));
    }

    @Override
    protected boolean notifyNearbyEntities() {
        return false;
    }

    @Override
    public void appendHoverText(@Nonnull ItemStack stack, @Nullable Level level, @Nonnull List<Component> tooltip, @Nonnull TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);
        MutableComponent component = getCurrentMob(stack);
        tooltip.add(component != null ? component : MobUtil.NO_MOB);
    }

    @Nullable
    public static MutableComponent getCurrentMob(ItemStack stack) {
        String s = stack.getOrCreateTag().getString(MobUtil.ENTITY_TYPE_TAG);
        Optional<EntityType<?>> entityType = EntityType.byString(s);

        return entityType.map(ItemMobBell::getCurrentMob).orElse(null);
    }

    @Nonnull
    public static MutableComponent getCurrentMob(EntityType<?> entityType) {
        Component name = entityType.getDescription();
        MutableComponent component = new TranslatableComponent("tooltip." + UtilitiX.getInstance().modid + ".current_mob");
        component.withStyle(entityType.getCategory() == MobCategory.MONSTER ? ChatFormatting.RED : ChatFormatting.GOLD);

        return component.append(": ").append(name);
    }
    
    public static int getColor(ItemStack stack) {
        if (stack.getTag() != null && stack.getTag().contains(MobUtil.ENTITY_TYPE_TAG, Tag.TAG_STRING)) {
            ResourceLocation rl = ResourceLocation.tryParse(stack.getTag().getString(MobUtil.ENTITY_TYPE_TAG));
            EntityType<?> entityType = rl == null ? null : ForgeRegistries.ENTITIES.getValue(rl);
            SpawnEggItem egg = entityType == null ? null : SpawnEggItem.byId(entityType);
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
