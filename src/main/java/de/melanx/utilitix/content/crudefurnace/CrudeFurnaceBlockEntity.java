package de.melanx.utilitix.content.crudefurnace;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SmeltingRecipe;
import net.minecraft.world.level.block.AbstractFurnaceBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import org.moddingx.libx.base.tile.BlockEntityBase;
import org.moddingx.libx.base.tile.TickingBlock;
import org.moddingx.libx.inventory.BaseItemStackHandler;
import org.moddingx.libx.inventory.FilterItemHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class CrudeFurnaceBlockEntity extends BlockEntityBase implements TickingBlock {

    public static final int FUEL_SLOT = 0;
    public static final int INPUT_SLOT = 1;
    public static final int OUTPUT_SLOT = 2;

    private final Object2IntOpenHashMap<ResourceLocation> recipesUsed = new Object2IntOpenHashMap<>();
    private final BaseItemStackHandler inventory;
    private final IItemHandler fuel;
    private final IItemHandler input;
    final IItemHandler output;
    private CrudeFurnaceRecipeHelper.ModifiedRecipe recipe;
    private int maxFuelTime;
    private int fuelTime;
    private int burnTime;
    private boolean update;
    private boolean initDone;

    public CrudeFurnaceBlockEntity(BlockEntityType<?> blockEntityTypeIn, BlockPos pos, BlockState state) {
        super(blockEntityTypeIn, pos, state);
        this.inventory = BaseItemStackHandler.builder(5)
                .validator(stack -> stack.getBurnTime(RecipeType.SMELTING) > 0, FUEL_SLOT)
                .validator(stack -> this.level != null && CrudeFurnaceRecipeHelper.getResult(this.level, stack) != null, INPUT_SLOT)
                .output(OUTPUT_SLOT)
                .contentsChanged(() -> {
                    this.setChanged();
                    this.setDispatchable();
                    this.update = true;
                })
                .build();

        this.fuel = new FilterItemHandler(this.inventory, slot -> false, (slot, stack) -> slot == FUEL_SLOT);
        this.input = new FilterItemHandler(this.inventory, slot -> false, (slot, stack) -> slot == INPUT_SLOT);
        this.output = new FilterItemHandler(this.inventory, slot -> slot == OUTPUT_SLOT, (slot, stack) -> false);
    }

    @Override
    public void tick() {
        if (this.level != null && !this.level.isClientSide) {
            boolean isBurning = this.isBurning();
            if (!this.initDone) {
                this.updateRecipe();
                this.initDone = true;
            }

            if (this.recipe != null) {
                ItemStack result = this.recipe.getOutput();

                if (this.fuelTime > 0) {
                    this.burnTime++;
                    this.setDispatchable();
                }

                if (!result.isEmpty() && this.burnTime >= this.recipe.getBurnTime() && this.inventory.getUnrestricted().insertItem(OUTPUT_SLOT, result, true).isEmpty()) {
                    this.burnTime = 0;
                    this.inventory.getUnrestricted().extractItem(INPUT_SLOT, 1, false);
                    this.inventory.getUnrestricted().insertItem(OUTPUT_SLOT, result.copy(), false);
                    this.setRecipeUsed(this.recipe.getRecipeHolder());
                    this.updateRecipe();
                    this.setDispatchable();
                }
            }

            if (this.fuelTime > 0) {
                this.fuelTime--;
                this.setDispatchable();
            }

            if (this.recipe != null && this.fuelTime <= 0) {
                this.fuelTime = this.inventory.getStackInSlot(FUEL_SLOT).getBurnTime(RecipeType.SMELTING) / 2;
                this.maxFuelTime = this.fuelTime;
                this.inventory.getUnrestricted().extractItem(FUEL_SLOT, 1, false);
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

    public int getScaledBurnTime() {
        return this.fuelTime * 13 / this.maxFuelTime;
    }

    public int getCookProgressionScaled() {
        return this.burnTime != 0 && this.recipe != null && this.recipe.getBurnTime() != 0 ? this.burnTime * 24 / this.recipe.getBurnTime() : 0;
    }

    public static IItemHandler getCapability(CrudeFurnaceBlockEntity be, Direction side) {
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
    public IItemHandlerModifiable getInventory() {
        return this.inventory;
    }

    @Nonnull
    public IItemHandlerModifiable getUnrestricted() {
        return this.inventory.getUnrestricted();
    }

    public CrudeFurnaceRecipeHelper.ModifiedRecipe getRecipe() {
        return this.recipe;
    }

    public void setRecipeUsed(@Nullable RecipeHolder<?> recipe) {
        if (recipe != null) {
            ResourceLocation id = recipe.id();
            this.recipesUsed.addTo(id, 1);
        }
    }

    // [Vanilla copy start]
    public void unlockRecipes(ServerPlayer player) {
        List<RecipeHolder<?>> recipes = this.getRecipesToAwardAndPopExperience(player.serverLevel(), player.position());
        player.awardRecipes(recipes);
        this.recipesUsed.clear();
    }

    public List<RecipeHolder<?>> getRecipesToAwardAndPopExperience(ServerLevel level, Vec3 pos) {
        List<RecipeHolder<?>> list = new ArrayList<>();

        for (Object2IntMap.Entry<ResourceLocation> entry : this.recipesUsed.object2IntEntrySet()) {
            level.getRecipeManager().byKey(entry.getKey()).ifPresent(holder -> {
                if (holder.value() instanceof SmeltingRecipe) {
                    @SuppressWarnings("unchecked")
                    RecipeHolder<SmeltingRecipe> smelt = (RecipeHolder<SmeltingRecipe>) holder;
                    splitAndSpawnExperience(level, pos, entry.getIntValue(),
                            new CrudeFurnaceRecipeHelper.ModifiedRecipe(level.registryAccess(), smelt).getXp()
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
            this.recipe = CrudeFurnaceRecipeHelper.getResult(this.level, this.inventory.getStackInSlot(INPUT_SLOT));
        }
    }
    // [Vanilla copy end]

    @Override
    public void loadAdditional(@Nonnull CompoundTag tag, @Nonnull HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        this.inventory.deserializeNBT(registries, tag.getCompound("Inventory"));
        this.burnTime = tag.getInt("burnTime");
        this.fuelTime = tag.getInt("fuelTime");
        this.maxFuelTime = tag.getInt("maxFuelTime");

        CompoundTag recipes = tag.getCompound("RecipesUsed");
        for (String s : recipes.getAllKeys()) {
            this.recipesUsed.put(ResourceLocation.tryParse(s), recipes.getInt(s));
        }
    }

    @Override
    public void saveAdditional(@Nonnull CompoundTag tag, @Nonnull HolderLookup.Provider registries) {
        tag.put("Inventory", this.inventory.serializeNBT(registries));
        tag.putInt("burnTime", this.burnTime);
        tag.putInt("fuelTime", this.fuelTime);
        tag.putInt("maxFuelTime", this.maxFuelTime);

        CompoundTag recipes = new CompoundTag();
        this.recipesUsed.forEach((id, xp) -> recipes.putInt(id.toString(), xp));
        tag.put("RecipesUsed", recipes);
    }

    @Nonnull
    @Override
    public CompoundTag getUpdateTag(@Nonnull HolderLookup.Provider registries) {
        CompoundTag nbt = super.getUpdateTag(registries);

        if (this.level != null && !this.level.isClientSide) {
            nbt.put("Inventory", this.inventory.serializeNBT(registries));
            nbt.putInt("burnTime", this.burnTime);
            nbt.putInt("fuelTime", this.fuelTime);
            nbt.putInt("maxFuelTime", this.maxFuelTime);

            CompoundTag recipes = nbt.getCompound("RecipesUsed");
            for (String s : recipes.getAllKeys()) {
                this.recipesUsed.put(ResourceLocation.tryParse(s), recipes.getInt(s));
            }
        }

        return nbt;
    }

    @Override
    public void handleUpdateTag(@Nonnull CompoundTag tag, @Nonnull HolderLookup.Provider registries) {
        if (this.level == null || !this.level.isClientSide) {
            return;
        }

        super.handleUpdateTag(tag, registries);

        this.inventory.deserializeNBT(registries, tag.getCompound("Inventory"));
        this.burnTime = tag.getInt("burnTime");
        this.fuelTime = tag.getInt("fuelTime");
        this.maxFuelTime = tag.getInt("maxFuelTime");

        CompoundTag recipes = new CompoundTag();
        this.recipesUsed.forEach((id, xp) -> recipes.putInt(id.toString(), xp));
        tag.put("RecipesUsed", recipes);
    }
}
