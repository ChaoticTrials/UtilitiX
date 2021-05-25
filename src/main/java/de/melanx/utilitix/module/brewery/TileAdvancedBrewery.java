package de.melanx.utilitix.module.brewery;

import de.melanx.utilitix.recipe.BreweryRecipe;
import de.melanx.utilitix.recipe.PotionOutput;
import de.melanx.utilitix.registration.ModItemTags;
import de.melanx.utilitix.registration.ModItems;
import de.melanx.utilitix.registration.ModRecipes;
import io.github.noeppi_noeppi.libx.crafting.recipe.RecipeHelper;
import io.github.noeppi_noeppi.libx.inventory.BaseItemStackHandler;
import io.github.noeppi_noeppi.libx.inventory.ItemStackHandlerWrapper;
import io.github.noeppi_noeppi.libx.inventory.VanillaWrapper;
import io.github.noeppi_noeppi.libx.mod.registration.TileEntityBase;
import net.minecraft.block.BlockState;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

//Ingredient-Slot: 0
//Potion-Ingredient-Slot R: 1
//Potion-Ingredient-Slot L: 2
//Output-Slot: 3
//Blaze-Slot: 4
public class TileAdvancedBrewery extends TileEntityBase implements ITickableTileEntity {

    public static final int MAX_BREW_TIME = 400;

    private int brewTime = 0;
    private int fuel = 0;

    private final BaseItemStackHandler inventory;
    private final IInventory vanilla;
    private final LazyOptional<IItemHandler> inventoryTop;
    private final LazyOptional<IItemHandler> inventorySide;
    private final LazyOptional<IItemHandler> inventoryBottom;

    public TileAdvancedBrewery(TileEntityType<?> tileEntityTypeIn) {
        super(tileEntityTypeIn);
        this.inventory = new BaseItemStackHandler(5, slot -> {
            this.markDirty();
            this.markDispatchable();
        }, this::isItemValid);
        this.inventory.addSlotLimit(1, 1);
        this.inventory.addSlotLimit(2, 1);
        this.inventory.addSlotLimit(3, 1);
        this.vanilla = new VanillaWrapper(this.inventory, this::markDirty);
        this.inventoryTop = ItemStackHandlerWrapper.createLazy(this::getInventory, slot -> false, (slot, stack) -> slot == 0 || slot == 3).cast();
        this.inventorySide = ItemStackHandlerWrapper.createLazy(this::getInventory, slot -> false, (slot, stack) -> slot == 1 || slot == 2 || slot == 4).cast();
        this.inventoryBottom = ItemStackHandlerWrapper.createLazy(this::getInventory, slot -> slot == 0 || slot == 1 || slot == 2, (slot, stack) -> false).cast();
    }

    private boolean isItemValid(int slot, ItemStack stack) {
        if (slot == 0)
            return this.world != null && RecipeHelper.isItemValidInput(this.world.getRecipeManager(), ModRecipes.BREWERY, stack);
        if (slot >= 1 && slot <= 3) return ModItemTags.BOTTLES.contains(stack.getItem());
        if (slot == 4) return stack.getItem() == Items.BLAZE_POWDER;
        return false;
    }

    @Override
    public void tick() {
        if (this.world != null && !this.world.isRemote) {
            if (this.fuel <= 0) {
                ItemStack fuelStack = this.inventory.getStackInSlot(4);
                if (fuelStack.getItem() == Items.BLAZE_POWDER && !fuelStack.isEmpty()) {
                    this.fuel = 20;
                    ItemStack fuelNew = fuelStack.copy();
                    fuelNew.shrink(1);
                    this.inventory.setStackInSlot(4, fuelNew);
                    this.markDirty();
                    this.markDispatchable();
                }
            }
            BreweryRecipe recipe = this.world.getRecipeManager().getRecipe(ModRecipes.BREWERY, this.vanilla, this.world).orElse(null);
            if ((this.fuel <= 0 || recipe == null) && this.brewTime > 0) {
                this.brewTime = 0;
                this.markDirty();
                this.markDispatchable();
            } else if (recipe != null && this.fuel >= 0) {
                if (this.brewTime <= 0) {
                    this.markDispatchable();
                }
                this.brewTime = MathHelper.clamp(this.brewTime + 1, 0, MAX_BREW_TIME);
                if (this.brewTime >= MAX_BREW_TIME) {
                    PotionOutput output = recipe.getPotionResult(this.vanilla);
                    if (output == null || output.getMain().isEmpty()) {
                        this.consumeItem(3);
                    } else {
                        this.inventory.setStackInSlot(3, output.getMain());
                    }
                    this.consumeItem(0);
                    if (output == null || output.getOut1().isEmpty()) {
                        this.consumeItem(1);
                    } else {
                        this.inventory.setStackInSlot(1, output.getOut1());
                    }
                    if (output == null || output.getOut2().isEmpty()) {
                        this.consumeItem(2);
                    } else {
                        this.inventory.setStackInSlot(2, output.getOut2());
                    }
                    this.brewTime = 0;
                    this.fuel -= 1;
                    this.world.playSound(null, this.pos, SoundEvents.BLOCK_BREWING_STAND_BREW, SoundCategory.BLOCKS, 1, 1);
                    this.markDispatchable();
                }
                this.markDirty();
            }
        } else {
            if (this.brewTime > 0 && this.brewTime < MAX_BREW_TIME) {
                this.brewTime += 1;
                if (this.world != null && this.world.getGameTime() % 4 == 0 && this.brewTime < MAX_BREW_TIME - 30) {
                    double xf = 0.5;
                    double zf = 0.15;
                    Direction dir = this.getBlockState().get(BlockStateProperties.HORIZONTAL_FACING);
                    if (dir.getAxis() == Direction.Axis.X) {
                        double tmp = xf;
                        xf = zf;
                        zf = tmp;
                    }
                    if (dir.getAxisDirection() == Direction.AxisDirection.POSITIVE) {
                        xf = 1 - xf;
                        zf = 1 - zf;
                    }
                    this.world.addParticle(ParticleTypes.DRIPPING_WATER, this.pos.getX() + xf, this.pos.getY() + 0.34, this.pos.getZ() + zf, 0, -0.6, 0);
                }
            }
        }
    }

    private void consumeItem(int slot) {
        ItemStack stack = this.inventory.getStackInSlot(slot);
        if (!stack.isEmpty()) {
            if (stack.hasContainerItem()) {
                this.inventory.setStackInSlot(slot, stack.getContainerItem().copy());
            } else if (stack.getItem() == Items.POTION || stack.getItem() == ModItems.failedPotion) {
                // Only give glass bottles for normal potions as other types don't give glass bottles as well.
                this.inventory.setStackInSlot(slot, new ItemStack(Items.GLASS_BOTTLE, stack.getCount()));
            } else if (stack.getCount() <= 1) {
                this.inventory.setStackInSlot(slot, ItemStack.EMPTY);
            } else {
                ItemStack copy = stack.copy();
                copy.shrink(1);
                this.inventory.setStackInSlot(slot, copy);
            }
        }
    }
    
    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            if (side == null) {
                return LazyOptional.of(this::getInventory).cast();
            }
            switch (side) {
                case DOWN: return this.inventoryBottom.cast();
                case UP: return this.inventoryTop.cast();
                default: return this.inventorySide.cast();
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

    public int getBrewTime() {
        return this.brewTime;
    }

    public int getFuel() {
        return this.fuel;
    }

    @Override
    public void read(@Nonnull BlockState state, @Nonnull CompoundNBT nbt) {
        super.read(state, nbt);
        this.inventory.deserializeNBT(nbt.getCompound("Inventory"));
        this.brewTime = nbt.getInt("brewTime");
        this.fuel = nbt.getInt("fuel");
    }

    @Nonnull
    @Override
    public CompoundNBT write(CompoundNBT nbt) {
        nbt.put("Inventory", this.inventory.serializeNBT());
        nbt.putInt("brewTime", this.brewTime);
        nbt.putInt("fuel", this.fuel);
        return super.write(nbt);
    }

    @Nonnull
    @Override
    public CompoundNBT getUpdateTag() {
        CompoundNBT nbt = super.getUpdateTag();
        if (this.world != null && !this.world.isRemote) {
            nbt.put("Inventory", this.inventory.serializeNBT());
            nbt.putInt("brewTime", this.brewTime);
            nbt.putInt("fuel", this.fuel);
        }
        return nbt;

    }

    @Override
    public void handleUpdateTag(@Nonnull BlockState state, @Nonnull CompoundNBT nbt) {
        if (this.world != null && this.world.isRemote) {
            super.handleUpdateTag(state, nbt);
            this.inventory.deserializeNBT(nbt.getCompound("Inventory"));
            this.brewTime = nbt.getInt("brewTime");
            this.fuel = nbt.getInt("fuel");
        }
    }
}
