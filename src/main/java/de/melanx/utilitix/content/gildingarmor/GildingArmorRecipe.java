package de.melanx.utilitix.content.gildingarmor;

import com.mojang.authlib.GameProfile;
import de.melanx.utilitix.registration.ModItems;
import de.melanx.utilitix.registration.ModRecipes;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Container;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterials;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.UpgradeRecipe;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.fml.DistExecutor;

import javax.annotation.Nonnull;

public class GildingArmorRecipe extends UpgradeRecipe {

    private static final GameProfile FAKE_PROFILE = new GameProfile(Util.NIL_UUID, "Steve");
    
    public GildingArmorRecipe(ResourceLocation id) {
        super(id, Ingredient.EMPTY, Ingredient.EMPTY, ItemStack.EMPTY);
    }

    @Override
    public boolean matches(@Nonnull Container inv, @Nonnull Level level) {
        ItemStack input = inv.getItem(0);
        ItemStack addition = inv.getItem(1);

        if (input.getItem() instanceof ArmorItem armor && !isGilded(input) && canGild(armor, input, level)) {
            return addition.getItem() == ModItems.gildingCrystal;
        }

        return false;
    }

    @Nonnull
    @Override
    public ItemStack assemble(@Nonnull Container inv) {
        ItemStack stack = inv.getItem(0).copy();
        stack.getOrCreateTag().putBoolean("Gilded_UtilitiX", true);

        return stack;
    }

    @Nonnull
    @Override
    public ItemStack getResultItem() {
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

    public static boolean canGild(ArmorItem armor, ItemStack stack, Level level) {
        if (armor.getMaterial() == ArmorMaterials.GOLD) return false;
        if (level instanceof ServerLevel serverLevel) {
            return !armor.makesPiglinsNeutral(stack, new FakePlayer(serverLevel, FAKE_PROFILE));
        } else {
            return DistExecutor.unsafeRunForDist(() -> () -> {
                if (!(level instanceof ClientLevel) || Minecraft.getInstance().player == null) return false;
                return !armor.makesPiglinsNeutral(stack, Minecraft.getInstance().player);
            }, () -> () -> false);
        }
    }
}
