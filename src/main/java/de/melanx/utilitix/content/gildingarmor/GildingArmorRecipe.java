package de.melanx.utilitix.content.gildingarmor;

import com.google.gson.JsonObject;
import de.melanx.utilitix.registration.ModItems;
import de.melanx.utilitix.registration.ModRecipes;
import net.minecraft.client.Minecraft;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterials;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SmithingTransformRecipe;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;

import javax.annotation.Nonnull;

public class GildingArmorRecipe extends SmithingTransformRecipe {

    private static final int ARMOR_SLOT_ID = 1;
    private static final int ADDITION_SLOT_ID = 2;

    public GildingArmorRecipe(ResourceLocation id) {
        super(id, Ingredient.EMPTY, Ingredient.EMPTY, Ingredient.EMPTY, ItemStack.EMPTY);
    }

    @Override
    public boolean matches(@Nonnull Container inv, @Nonnull Level level) {
        ItemStack input = inv.getItem(ARMOR_SLOT_ID);
        ItemStack addition = inv.getItem(ADDITION_SLOT_ID);

        if (input.getItem() instanceof ArmorItem armor && !isGilded(input) && canGild(armor, input)) {
            return addition.getItem() == ModItems.gildingCrystal;
        }

        return false;
    }

    @Nonnull
    @Override
    public ItemStack assemble(@Nonnull Container inv, @Nonnull RegistryAccess registry) {
        ItemStack stack = inv.getItem(ARMOR_SLOT_ID).copy();
        stack.getOrCreateTag().putBoolean("Gilded_UtilitiX", true);

        return stack;
    }

    @Nonnull
    @Override
    public ItemStack getResultItem(@Nonnull RegistryAccess registry) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean isAdditionIngredient(@Nonnull ItemStack addition) {
        return addition.getItem() == ModItems.gildingCrystal;
    }

    @Nonnull
    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipes.GILDING_SERIALIZER;
    }

    public static boolean isGilded(ItemStack stack) {
        return stack.hasTag() && stack.getOrCreateTag().getBoolean("Gilded_UtilitiX");
    }

    public static boolean canGild(ArmorItem armor, ItemStack stack) {
        if (armor.getMaterial() == ArmorMaterials.GOLD) return false;
        try {
            Player player = DistExecutor.unsafeCallWhenOn(Dist.CLIENT, () -> () -> Minecraft.getInstance().player);
            return !armor.makesPiglinsNeutral(stack, player);
        } catch (NullPointerException e) {
            return false;
        }
    }

    public static class Serializer implements RecipeSerializer<GildingArmorRecipe> {

        @Nonnull
        public GildingArmorRecipe fromJson(@Nonnull ResourceLocation id, @Nonnull JsonObject json) {
            return new GildingArmorRecipe(id);
        }

        public GildingArmorRecipe fromNetwork(@Nonnull ResourceLocation id, @Nonnull FriendlyByteBuf buffer) {
            return new GildingArmorRecipe(id);
        }

        public void toNetwork(@Nonnull FriendlyByteBuf buffer, @Nonnull GildingArmorRecipe recipe) {
            // NO-OP
        }
    }
}
