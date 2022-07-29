package de.melanx.utilitix.content.crudefurnace;

import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SmeltingRecipe;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.AbstractFurnaceBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import org.moddingx.libx.base.tile.BlockEntityBase;
import org.moddingx.libx.base.tile.TickingBlock;
import org.moddingx.libx.capability.ItemCapabilities;
import org.moddingx.libx.inventory.BaseItemStackHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class TileCrudeFurnace extends BlockEntityBase implements TickingBlock {

    private final Object2IntOpenHashMap<ResourceLocation> recipes = new Object2IntOpenHashMap<>();
    private final BaseItemStackHandler inventory;
    private final LazyOptional<IItemHandler> fuel;
    private final LazyOptional<IItemHandler> input;
    private final LazyOptional<IItemHandler> output;
    private CrudeFurnaceRecipeHelper.ModifiedRecipe recipe;
    private int maxFuelTime;
    private int fuelTime;
    private int burnTime;
    private boolean update;
    private boolean initDone;

    public TileCrudeFurnace(BlockEntityType<?> blockEntityTypeIn, BlockPos pos, BlockState state) {
        super(blockEntityTypeIn, pos, state);
        this.inventory = BaseItemStackHandler.builder(5)
                .validator(stack -> ForgeHooks.getBurnTime(stack, RecipeType.SMELTING) > 0, 0)
                .validator(stack -> this.level != null && CrudeFurnaceRecipeHelper.getResult(this.level.getRecipeManager(), stack) != null, 1)
                .output(2)
                .contentsChanged(() -> {
                    this.setChanged();
                    this.setDispatchable();
                    this.update = true;
                })
                .build();
        this.fuel = ItemCapabilities.create(this::getInventory, slot -> false, (slot, stack) -> slot == 0).cast();
        this.input = ItemCapabilities.create(this::getInventory, slot -> false, (slot, stack) -> slot == 1).cast();
        this.output = ItemCapabilities.create(this::getInventory, slot -> slot == 2, (slot, stack) -> false).cast();
    }

    private boolean isItemValid(int slot, ItemStack stack) {
        if (slot == 0) {
            return ForgeHooks.getBurnTime(stack, RecipeType.SMELTING) > 0;
        }
        if (slot == 1) {
            return this.level != null && CrudeFurnaceRecipeHelper.getResult(this.level.getRecipeManager(), stack) != null;
        }

        return false;
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

                if (!result.isEmpty() && this.burnTime >= this.recipe.getBurnTime() && this.inventory.getUnrestricted().insertItem(2, result, true).isEmpty()) {
                    this.burnTime = 0;
                    this.inventory.getUnrestricted().extractItem(1, 1, false);
                    this.inventory.getUnrestricted().insertItem(2, result.copy(), false);
                    this.setRecipeUsed(this.recipe.getOriginalRecipe());
                    this.updateRecipe();
                    this.setDispatchable();
                }
            }

            if (this.fuelTime > 0) {
                this.fuelTime--;
                this.setDispatchable();
            }

            if (this.recipe != null && this.fuelTime <= 0) {
                this.fuelTime = ForgeHooks.getBurnTime(this.inventory.getStackInSlot(0), RecipeType.SMELTING) / 2;
                this.maxFuelTime = this.fuelTime;
                this.inventory.getUnrestricted().extractItem(0, 1, false);
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

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            if (side == null) {
                return LazyOptional.of(this::getInventory).cast();
            }
            return switch (side) {
                case NORTH, EAST, SOUTH, WEST -> this.fuel.cast();
                case UP -> this.input.cast();
                case DOWN -> this.output.cast();
            };
        } else {
            return super.getCapability(cap, side);
        }
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

    public void setRecipeUsed(@Nullable Recipe<?> recipe) {
        if (recipe != null) {
            ResourceLocation id = recipe.getId();
            this.recipes.addTo(id, 1);
        }
    }

    // [Vanilla copy start]
    public void unlockRecipes(Player player) {
        List<Recipe<?>> recipes = this.grantStoredRecipeExperience(player.level, player.position());
        player.awardRecipes(recipes);
        this.recipes.clear();
    }

    public List<Recipe<?>> grantStoredRecipeExperience(Level level, Vec3 pos) {
        List<Recipe<?>> list = Lists.newArrayList();

        for (Object2IntMap.Entry<ResourceLocation> entry : this.recipes.object2IntEntrySet()) {
            level.getRecipeManager().byKey(entry.getKey()).ifPresent((recipe) -> {
                list.add(recipe);
                splitAndSpawnExperience(level, pos, entry.getIntValue(), (new CrudeFurnaceRecipeHelper.ModifiedRecipe((SmeltingRecipe) recipe)).getXp());
            });
        }

        return list;
    }

    private static void splitAndSpawnExperience(Level level, Vec3 pos, int craftedAmount, float experience) {
        int i = Mth.floor((float) craftedAmount * experience);
        float f = Mth.frac((float) craftedAmount * experience);
        if (f != 0.0F && Math.random() < (double) f) {
            ++i;
        }

        while (i > 0) {
            int j = ExperienceOrb.getExperienceValue(i);
            i -= j;
            level.addFreshEntity(new ExperienceOrb(level, pos.x, pos.y, pos.z, j));
        }
    }

    private void updateRecipe() {
        if (this.level != null) {
            this.recipe = CrudeFurnaceRecipeHelper.getResult(this.level.getRecipeManager(), this.inventory.getStackInSlot(1));
        }
    }
    // [Vanilla copy end]

    @Override
    public void load(@Nonnull CompoundTag nbt) {
        super.load(nbt);
        this.inventory.deserializeNBT(nbt.getCompound("Inventory"));
        this.burnTime = nbt.getInt("burnTime");
        this.fuelTime = nbt.getInt("fuelTime");
        this.maxFuelTime = nbt.getInt("maxFuelTime");

        CompoundTag recipes = nbt.getCompound("RecipesUsed");
        for (String s : recipes.getAllKeys()) {
            this.recipes.put(new ResourceLocation(s), recipes.getInt(s));
        }
    }

    @Override
    public void saveAdditional(@Nonnull CompoundTag compound) {
        compound.put("Inventory", this.inventory.serializeNBT());
        compound.putInt("burnTime", this.burnTime);
        compound.putInt("fuelTime", this.fuelTime);
        compound.putInt("maxFuelTime", this.maxFuelTime);

        CompoundTag recipes = new CompoundTag();
        this.recipes.forEach((id, xp) -> recipes.putInt(id.toString(), xp));
        compound.put("RecipesUsed", recipes);
    }

    @Nonnull
    @Override
    public CompoundTag getUpdateTag() {
        CompoundTag nbt = super.getUpdateTag();
        if (this.level != null && !this.level.isClientSide) {
            nbt.put("Inventory", this.inventory.serializeNBT());
            nbt.putInt("burnTime", this.burnTime);
            nbt.putInt("fuelTime", this.fuelTime);
            nbt.putInt("maxFuelTime", this.maxFuelTime);

            CompoundTag recipes = nbt.getCompound("RecipesUsed");
            for (String s : recipes.getAllKeys()) {
                this.recipes.put(new ResourceLocation(s), recipes.getInt(s));
            }
        }
        return nbt;
    }

    @Override
    public void handleUpdateTag(CompoundTag nbt) {
        if (this.level != null && this.level.isClientSide) {
            super.handleUpdateTag(nbt);
            this.inventory.deserializeNBT(nbt.getCompound("Inventory"));
            this.burnTime = nbt.getInt("burnTime");
            this.fuelTime = nbt.getInt("fuelTime");
            this.maxFuelTime = nbt.getInt("maxFuelTime");

            CompoundTag recipes = new CompoundTag();
            this.recipes.forEach((id, xp) -> recipes.putInt(id.toString(), xp));
            nbt.put("RecipesUsed", recipes);
        }
    }
}
