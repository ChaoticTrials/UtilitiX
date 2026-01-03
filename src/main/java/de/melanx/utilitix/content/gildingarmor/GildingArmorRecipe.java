package de.melanx.utilitix.content.gildingarmor;

import com.mojang.serialization.MapCodec;
import de.melanx.utilitix.client.ClientProxy;
import de.melanx.utilitix.config.FeatureConfig;
import de.melanx.utilitix.registration.ModDataComponentTypes;
import de.melanx.utilitix.registration.ModItems;
import de.melanx.utilitix.registration.ModRecipes;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterials;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SmithingRecipeInput;
import net.minecraft.world.item.crafting.SmithingTransformRecipe;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.loading.FMLEnvironment;

import javax.annotation.Nonnull;

public class GildingArmorRecipe extends SmithingTransformRecipe {

    public static final GildingArmorRecipe INSTANCE = new GildingArmorRecipe();
    private static final int ARMOR_SLOT_ID = 1;
    private static final int ADDITION_SLOT_ID = 2;

    public GildingArmorRecipe() {
        super(Ingredient.EMPTY, Ingredient.EMPTY, Ingredient.EMPTY, ItemStack.EMPTY);
    }

    @Override
    public boolean matches(@Nonnull SmithingRecipeInput inv, @Nonnull Level level) {
        ItemStack input = inv.getItem(ARMOR_SLOT_ID);
        ItemStack addition = inv.getItem(ADDITION_SLOT_ID);

        if (input.getItem() instanceof ArmorItem armor && !GildingArmorRecipe.isGilded(input) && GildingArmorRecipe.canGild(armor, input)) {
            return addition.getItem() == ModItems.gildingCrystal;
        }

        return false;
    }

    @Nonnull
    @Override
    public ItemStack assemble(@Nonnull SmithingRecipeInput inv, @Nonnull HolderLookup.Provider registry) {
        ItemStack stack = inv.getItem(ARMOR_SLOT_ID).copy();
        stack.set(ModDataComponentTypes.gilded, true);

        return stack;
    }

    @Nonnull
    @Override
    public ItemStack getResultItem(@Nonnull HolderLookup.Provider registry) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean isAdditionIngredient(@Nonnull ItemStack addition) {
        return addition.getItem() == ModItems.gildingCrystal;
    }

    @Nonnull
    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipes.gildingSerializer;
    }

    public static boolean isGilded(ItemStack stack) {
        return stack.getOrDefault(ModDataComponentTypes.gilded, false) && FeatureConfig.Misc.InWorldChanges.gilding;
    }

    public static boolean canGild(ArmorItem armor, ItemStack stack) {
        if (armor.getMaterial() == ArmorMaterials.GOLD) {
            return false;
        }

        try {
            Player player = null;
            if (FMLEnvironment.dist == Dist.CLIENT) {
                try {
                    player = ClientProxy.getClientPlayer();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }

            //noinspection DataFlowIssue
            return !armor.makesPiglinsNeutral(stack, player);
        } catch (NullPointerException e) {
            return false;
        }
    }

    public static class Serializer implements RecipeSerializer<GildingArmorRecipe> {

        @Nonnull
        @Override
        public MapCodec<GildingArmorRecipe> codec() {
            return MapCodec.unit(() -> GildingArmorRecipe.INSTANCE);
        }

        @Nonnull
        @Override
        public StreamCodec<RegistryFriendlyByteBuf, GildingArmorRecipe> streamCodec() {
            return StreamCodec.unit(GildingArmorRecipe.INSTANCE);
        }
    }
}
