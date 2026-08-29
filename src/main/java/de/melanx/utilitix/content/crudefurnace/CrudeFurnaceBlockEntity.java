package de.melanx.utilitix.content.crudefurnace;

import de.melanx.utilitix.util.ItemHandlerUtil;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SmeltingRecipe;
import net.minecraft.world.level.block.AbstractFurnaceBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.item.ItemUtil;
import org.moddingx.libx.base.tile.BlockEntityBase;
import org.moddingx.libx.base.tile.TickingBlock;
import org.moddingx.libx.inventory.BaseItemStackHandler;
import org.moddingx.libx.inventory.FilterItemHandler;
import org.moddingx.libx.inventory.IAdvancedItemHandler;
import org.moddingx.libx.inventory.IAdvancedItemHandlerModifiable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class CrudeFurnaceBlockEntity extends BlockEntityBase implements TickingBlock {

    public static final int FUEL_SLOT = 0;
    public static final int INPUT_SLOT = 1;
    public static final int OUTPUT_SLOT = 2;

    private final Object2IntOpenHashMap<ResourceKey<Recipe<?>>> recipesUsed = new Object2IntOpenHashMap<>();
    private final BaseItemStackHandler inventory;
    private final IAdvancedItemHandler fuel;
    private final IAdvancedItemHandler input;
    final IAdvancedItemHandler output;
    private CrudeFurnaceRecipeHelper.ModifiedRecipe recipe;
    private int maxFuelTime;
    private int fuelTime;
    private int burnTime;
    private int totalCookTime;
    private boolean update;
    private boolean initDone;

    public CrudeFurnaceBlockEntity(BlockEntityType<?> blockEntityTypeIn, BlockPos pos, BlockState state) {
        super(blockEntityTypeIn, pos, state);
        this.inventory = BaseItemStackHandler.builder(5)
                .validator(stack -> this.level != null && stack.getBurnTime(RecipeType.SMELTING, this.level.fuelValues()) > 0, FUEL_SLOT)
                .validator(stack -> this.level != null && CrudeFurnaceRecipeHelper.getResult(this.level, stack) != null, INPUT_SLOT)
                .output(OUTPUT_SLOT)
                .contentsChanged(slot -> {
                    this.setChanged();
                    this.setDispatchable();
                    if (slot == INPUT_SLOT) {
                        this.update = true;
                    }
                })
                .build();

        this.fuel = new FilterItemHandler(this.inventory, slot -> false, (slot, stack) -> slot == FUEL_SLOT);
        this.input = new FilterItemHandler(this.inventory, slot -> false, (slot, stack) -> slot == INPUT_SLOT);
        this.output = new FilterItemHandler(this.inventory, slot -> slot == OUTPUT_SLOT, (slot, stack) -> false);
    }

    @Override
    public void tick() {
        if (this.level != null && !this.level.isClientSide()) {
            boolean isBurning = this.isBurning();
            if (!this.initDone) {
                this.updateRecipe();
                this.initDone = true;
            }

            if (this.recipe != null) {
                ItemStack result = this.recipe.getOutput();
                boolean recipeOutputMatchesOutputSlot = ItemUtil.insertItemReturnRemaining(this.inventory.getUnrestricted(), OUTPUT_SLOT, result, true, null).isEmpty();

                if (this.fuelTime > 0) {
                    if (recipeOutputMatchesOutputSlot) {
                        this.burnTime++;
                    } else {
                        this.burnTime = 0;
                    }
                    this.setDispatchable();
                }

                if (!result.isEmpty() && this.burnTime >= this.recipe.getBurnTime() && recipeOutputMatchesOutputSlot) {
                    this.burnTime = 0;
                    ItemHandlerUtil.extractItem(this.inventory.getUnrestricted(), INPUT_SLOT, 1, false);
                    ItemUtil.insertItemReturnRemaining(this.inventory.getUnrestricted(), OUTPUT_SLOT, result.copy(), false, null);
                    this.setRecipeUsed(this.recipe.getRecipeHolder());
                    this.updateRecipe();
                    this.setDispatchable();
                }
            }

            if (this.fuelTime > 0) {
                this.fuelTime--;
                this.setDispatchable();
            }

            if (this.recipe != null && this.fuelTime <= 0 && ItemUtil.insertItemReturnRemaining(this.inventory.getUnrestricted(), OUTPUT_SLOT, recipe.getOutput().copy(), true, null).isEmpty()) {
                this.fuelTime = ItemUtil.getStack(this.inventory, FUEL_SLOT).getBurnTime(RecipeType.SMELTING, this.level.fuelValues()) / 2;
                this.maxFuelTime = this.fuelTime;
                ItemHandlerUtil.extractItem(this.inventory.getUnrestricted(), FUEL_SLOT, 1, false);
                this.setDispatchable();
            }

            if (this.fuelTime <= 0 && this.burnTime != 0) {
                this.burnTime = 0;
                this.setDispatchable();
            }

            if (isBurning != this.isBurning()) {
                this.level.setBlockAndUpdate(this.worldPosition, this.getBlockState().setValue(AbstractFurnaceBlock.LIT, this.isBurning()));
            }

            this.setChanged();
        }

        if (this.update) {
            this.updateRecipe();
            this.update = false;
        }
    }

    public boolean isBurning() {
        return this.fuelTime > 0;
    }

    public int getBurnTime() {
        return this.burnTime;
    }

    public int getScaledBurnTime() {
        return this.fuelTime * 13 / this.maxFuelTime;
    }

    public int getCookProgressionScaled() {
        return this.burnTime != 0 && this.totalCookTime != 0 ? this.burnTime * 24 / this.totalCookTime : 0;
    }

    public static ResourceHandler<ItemResource> getCapability(CrudeFurnaceBlockEntity be, Direction side) {
        if (side == null) {
            return be.getInventory();
        }

        return switch(side) {
            case NORTH, EAST, SOUTH, WEST -> be.fuel;
            case UP -> be.input;
            case DOWN -> be.output;
        };
    }

    @Nonnull
    public BaseItemStackHandler getInventory() {
        return this.inventory;
    }

    @Nonnull
    public IAdvancedItemHandlerModifiable getUnrestricted() {
        return this.inventory.getUnrestricted();
    }

    public CrudeFurnaceRecipeHelper.ModifiedRecipe getRecipe() {
        return this.recipe;
    }

    public void setRecipeUsed(@Nullable RecipeHolder<?> recipe) {
        if (recipe != null) {
            ResourceKey<Recipe<?>> id = recipe.id();
            this.recipesUsed.addTo(id, 1);
        }
    }

    // [Vanilla copy start]
    public void unlockRecipes(ServerPlayer player) {
        List<RecipeHolder<?>> recipes = this.getRecipesToAwardAndPopExperience(player.level(), player.position());
        player.awardRecipes(recipes);
        this.recipesUsed.clear();
    }

    public List<RecipeHolder<?>> getRecipesToAwardAndPopExperience(ServerLevel level, Vec3 pos) {
        List<RecipeHolder<?>> list = new ArrayList<>();

        for (Object2IntMap.Entry<ResourceKey<Recipe<?>>> entry : this.recipesUsed.object2IntEntrySet()) {
            level.recipeAccess().byKey(entry.getKey()).ifPresent(holder -> {
                if (holder.value() instanceof SmeltingRecipe) {
                    @SuppressWarnings("unchecked")
                    RecipeHolder<SmeltingRecipe> smelt = (RecipeHolder<SmeltingRecipe>) holder;
                    splitAndSpawnExperience(level, pos, entry.getIntValue(),
                            new CrudeFurnaceRecipeHelper.ModifiedRecipe(smelt).getXp()
                    );
                }
            });
        }

        return list;
    }

    private static void splitAndSpawnExperience(ServerLevel level, Vec3 pos, int craftedAmount, float experience) {
        int i = Mth.floor((float) craftedAmount * experience);
        float f = Mth.frac((float) craftedAmount * experience);
        if (f != 0.0F && Math.random() < (double) f) {
            i++;
        }

        ExperienceOrb.award(level, pos, i);
    }

    private void updateRecipe() {
        if (this.level != null) {
            this.recipe = CrudeFurnaceRecipeHelper.getResult(this.level, ItemUtil.getStack(this.inventory, INPUT_SLOT));
            this.totalCookTime = this.recipe != null ? this.recipe.getBurnTime() : 0;
        }
    }
    // [Vanilla copy end]

    @Override
    protected void loadAdditional(@Nonnull ValueInput input) {
        super.loadAdditional(input);
        this.inventory.deserialize(input.childOrEmpty("Inventory"));
        this.burnTime = input.getIntOr("burnTime", 0);
        this.fuelTime = input.getIntOr("fuelTime", 0);
        this.maxFuelTime = input.getIntOr("maxFuelTime", 0);
        this.totalCookTime = input.getIntOr("totalCookTime", 0);

        this.recipesUsed.clear();
        CompoundTag recipes = input.read("RecipesUsed", CompoundTag.CODEC).orElseGet(CompoundTag::new);
        for (String s : recipes.keySet()) {
            Identifier id = Identifier.tryParse(s);
            if (id != null) {
                this.recipesUsed.put(ResourceKey.create(Registries.RECIPE, id), recipes.getIntOr(s, 0));
            }
        }
    }

    @Override
    protected void saveAdditional(@Nonnull ValueOutput output) {
        super.saveAdditional(output);
        this.inventory.serialize(output.child("Inventory"));
        output.putInt("burnTime", this.burnTime);
        output.putInt("fuelTime", this.fuelTime);
        output.putInt("maxFuelTime", this.maxFuelTime);
        output.putInt("totalCookTime", this.totalCookTime);

        CompoundTag recipes = new CompoundTag();
        this.recipesUsed.forEach((id, xp) -> recipes.putInt(id.identifier().toString(), xp));
        output.store("RecipesUsed", CompoundTag.CODEC, recipes);
    }
}
