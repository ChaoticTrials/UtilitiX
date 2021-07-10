package de.melanx.utilitix.content.crudefurnace;

import com.google.common.collect.Lists;
import io.github.noeppi_noeppi.libx.inventory.BaseItemStackHandler;
import io.github.noeppi_noeppi.libx.inventory.ItemStackHandlerWrapper;
import io.github.noeppi_noeppi.libx.mod.registration.TileEntityBase;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.block.AbstractFurnaceBlock;
import net.minecraft.block.BlockState;
import net.minecraft.entity.item.ExperienceOrbEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.AbstractCookingRecipe;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class TileCrudeFurnace extends TileEntityBase implements ITickableTileEntity {

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

    public TileCrudeFurnace(TileEntityType<?> tileEntityTypeIn) {
        super(tileEntityTypeIn);
        this.inventory = new BaseItemStackHandler(5, slot -> {
            this.markDirty();
            this.markDispatchable();
            this.update = true;
        }, this::isItemValid);
        this.fuel = ItemStackHandlerWrapper.createLazy(this::getInventory, slot -> false, (slot, stack) -> slot == 0).cast();
        this.input = ItemStackHandlerWrapper.createLazy(this::getInventory, slot -> false, (slot, stack) -> slot == 1).cast();
        this.output = ItemStackHandlerWrapper.createLazy(this::getInventory, slot -> slot == 2, (slot, stack) -> false).cast();
    }

    private boolean isItemValid(int slot, ItemStack stack) {
        if (slot == 0) {
            return ForgeHooks.getBurnTime(stack) > 0;
        }
        if (slot == 1) {
            return this.world != null && CrudeFurnaceRecipeHelper.getResult(this.world.getRecipeManager(), stack) != null;
        }

        return false;
    }

    @Override
    public void tick() {
        if (this.world != null && !this.world.isRemote) {
            boolean isBurning = this.isBurning();
            if (!this.initDone) {
                this.update = true;
                this.initDone = true;
            }

            if (this.recipe != null) {
                ItemStack result = this.recipe.getOutput();

                if (this.fuelTime > 0) {
                    this.burnTime++;
                }

                if (!result.isEmpty() && this.burnTime >= this.recipe.getBurnTime() && this.inventory.getUnrestricted().insertItem(2, result, true).isEmpty()) {
                    this.burnTime = 0;
                    this.inventory.getUnrestricted().extractItem(1, 1, false);
                    this.inventory.getUnrestricted().insertItem(2, result.copy(), false);
                    this.setRecipeUsed(this.recipe.getOriginalRecipe());
                    this.update = true;
                    this.markDispatchable();
                }
            }

            if (this.recipe != null && this.fuelTime <= 0) {
                this.fuelTime = ForgeHooks.getBurnTime(this.inventory.getStackInSlot(0)) / 2;
                this.maxFuelTime = this.fuelTime;
                this.inventory.getUnrestricted().extractItem(0, 1, false);
                this.markDispatchable();
            }

            if (this.fuelTime <= 0 && this.burnTime != 0) {
                this.burnTime = 0;
                this.markDispatchable();
            }

            if (this.fuelTime > 0) {
                this.fuelTime--;
                this.markDispatchable();
            }

            if (isBurning != this.isBurning()) {
                this.world.setBlockState(this.pos, this.getBlockState().with(AbstractFurnaceBlock.LIT, this.isBurning()));
            }

            this.markDirty();
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
            switch (side) {
                case NORTH:
                case EAST:
                case SOUTH:
                case WEST:
                    return this.fuel.cast();
                case UP:
                    return this.input.cast();
                case DOWN:
                    return this.output.cast();
                default:
                    return super.getCapability(cap, side);
            }
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

    public void setRecipeUsed(@Nullable IRecipe<?> recipe) {
        if (recipe != null) {
            ResourceLocation id = recipe.getId();
            this.recipes.addTo(id, 1);
        }
    }

    // [Vanilla copy start]
    public void unlockRecipes(PlayerEntity player) {
        List<IRecipe<?>> recipes = this.grantStoredRecipeExperience(player.world, player.getPositionVec());
        player.unlockRecipes(recipes);
        this.recipes.clear();
    }

    public List<IRecipe<?>> grantStoredRecipeExperience(World world, Vector3d pos) {
        List<IRecipe<?>> list = Lists.newArrayList();

        for (Object2IntMap.Entry<ResourceLocation> entry : this.recipes.object2IntEntrySet()) {
            world.getRecipeManager().getRecipe(entry.getKey()).ifPresent((recipe) -> {
                list.add(recipe);
                splitAndSpawnExperience(world, pos, entry.getIntValue(), ((AbstractCookingRecipe) recipe).getExperience());
            });
        }

        return list;
    }

    private static void splitAndSpawnExperience(World world, Vector3d pos, int craftedAmount, float experience) {
        int i = MathHelper.floor((float) craftedAmount * experience);
        float f = MathHelper.frac((float) craftedAmount * experience);
        if (f != 0.0F && Math.random() < (double) f) {
            ++i;
        }

        while (i > 0) {
            int j = ExperienceOrbEntity.getXPSplit(i);
            i -= j;
            world.addEntity(new ExperienceOrbEntity(world, pos.x, pos.y, pos.z, j));
        }
    }

    private void updateRecipe() {
        if (this.world != null) {
            this.recipe = CrudeFurnaceRecipeHelper.getResult(this.world.getRecipeManager(), this.inventory.getStackInSlot(1));
        }
    }
    // [Vanilla copy end]

    @Override
    public void read(@Nonnull BlockState state, @Nonnull CompoundNBT nbt) {
        super.read(state, nbt);
        this.inventory.deserializeNBT(nbt.getCompound("Inventory"));
        this.burnTime = nbt.getInt("burnTime");
        this.fuelTime = nbt.getInt("fuelTime");
        this.maxFuelTime = nbt.getInt("maxFuelTime");

        CompoundNBT recipes = nbt.getCompound("RecipesUsed");
        for (String s : recipes.keySet()) {
            this.recipes.put(new ResourceLocation(s), recipes.getInt(s));
        }
    }

    @Nonnull
    @Override
    public CompoundNBT write(@Nonnull CompoundNBT nbt) {
        nbt.put("Inventory", this.inventory.serializeNBT());
        nbt.putInt("burnTime", this.burnTime);
        nbt.putInt("fuelTime", this.fuelTime);
        nbt.putInt("maxFuelTime", this.maxFuelTime);

        CompoundNBT recipes = new CompoundNBT();
        this.recipes.forEach((id, xp) -> recipes.putInt(id.toString(), xp));
        nbt.put("RecipesUsed", recipes);
        return super.write(nbt);
    }

    @Nonnull
    @Override
    public CompoundNBT getUpdateTag() {
        CompoundNBT nbt = super.getUpdateTag();
        if (this.world != null && !this.world.isRemote) {
            nbt.put("Inventory", this.inventory.serializeNBT());
            nbt.putInt("burnTime", this.burnTime);
            nbt.putInt("fuelTime", this.fuelTime);
            nbt.putInt("maxFuelTime", this.maxFuelTime);

            CompoundNBT recipes = nbt.getCompound("RecipesUsed");
            for (String s : recipes.keySet()) {
                this.recipes.put(new ResourceLocation(s), recipes.getInt(s));
            }
        }
        return nbt;
    }

    @Override
    public void handleUpdateTag(BlockState state, CompoundNBT nbt) {
        if (this.world != null && this.world.isRemote) {
            super.handleUpdateTag(state, nbt);
            this.inventory.deserializeNBT(nbt.getCompound("Inventory"));
            this.burnTime = nbt.getInt("burnTime");
            this.fuelTime = nbt.getInt("fuelTime");
            this.maxFuelTime = nbt.getInt("maxFuelTime");

            CompoundNBT recipes = new CompoundNBT();
            this.recipes.forEach((id, xp) -> recipes.putInt(id.toString(), xp));
            nbt.put("RecipesUsed", recipes);
        }
    }
}
