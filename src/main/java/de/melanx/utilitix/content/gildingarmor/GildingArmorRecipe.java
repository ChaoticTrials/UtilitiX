package de.melanx.utilitix.content.gildingarmor;

import de.melanx.utilitix.client.ClientProxy;
import de.melanx.utilitix.config.FeatureConfig;
import de.melanx.utilitix.registration.ModDataComponentTypes;
import de.melanx.utilitix.registration.ModItems;
import de.melanx.utilitix.registration.ModRecipes;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.item.equipment.Equippable;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.loading.FMLEnvironment;

import javax.annotation.Nonnull;
import java.util.Optional;

public class GildingArmorRecipe extends SmithingTransformRecipe {

    public static final GildingArmorRecipe INSTANCE = new GildingArmorRecipe();
    private static final int ARMOR_SLOT_ID = 1;
    private static final int ADDITION_SLOT_ID = 2;

    public GildingArmorRecipe() {
        super(
                // Iron Chestplate is only used for rendering in Cooking Book and JEI
                new Recipe.CommonInfo(true),
                Optional.empty(),
                Ingredient.of(Items.IRON_CHESTPLATE),
                Optional.of(Ingredient.of(ModItems.gildingCrystal)),
                new ItemStackTemplate(Items.IRON_CHESTPLATE)
        );
    }

    @Override
    public boolean matches(@Nonnull SmithingRecipeInput inv, @Nonnull Level level) {
        ItemStack input = inv.getItem(ARMOR_SLOT_ID);
        ItemStack addition = inv.getItem(ADDITION_SLOT_ID);

        if (!GildingArmorRecipe.isGilded(input) && GildingArmorRecipe.canGild(input)) {
            return addition.getItem() == ModItems.gildingCrystal;
        }

        return false;
    }

    @Nonnull
    @Override
    public ItemStack assemble(SmithingRecipeInput input) {
        ItemStack stack = input.getItem(ARMOR_SLOT_ID).copy();
        stack.set(ModDataComponentTypes.gilded, true);

        return stack;
    }

    @Nonnull
    @Override
    public RecipeSerializer<SmithingTransformRecipe> getSerializer() {
        return ModRecipes.gildingSerializer;
    }

    public static boolean isGilded(ItemStack stack) {
        return stack.getOrDefault(ModDataComponentTypes.gilded, false) && FeatureConfig.Misc.InWorldChanges.gilding;
    }

    public static boolean canGild(ItemStack armor) {
        Equippable equippable = armor.get(DataComponents.EQUIPPABLE);
        if (equippable == null || !EquipmentSlotGroup.ARMOR.slots().contains(equippable.slot())) {
            return false;
        }

        try {
            Player player = null;
            if (FMLEnvironment.getDist() == Dist.CLIENT) {
                try {
                    player = ClientProxy.getClientPlayer();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }

            //noinspection DataFlowIssue
            return !armor.makesPiglinsNeutral(player);
        } catch (NullPointerException e) {
            return false;
        }
    }
}
